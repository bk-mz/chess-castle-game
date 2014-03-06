package zingaya.chess;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import zingaya.chess.matan.HorzVector;
import zingaya.chess.matan.Pos;
import zingaya.chess.matan.Vector;
import zingaya.chess.matan.VertVector;
import zingaya.chess.messages.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Chessboard extends UntypedActor {

	static final int SIZE = 8;

	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	final ConcurrentHashMap<ActorRef, Pos> positions = new ConcurrentHashMap<>();

	final Random rnd = new Random();

	@Override
	public void onReceive(Object o) throws Exception {
		if (o instanceof NewGame) {
			handleNewGame((NewGame) o);
		} else if (o instanceof Move) {
			handleNewMove((Move) o);
		} else if (o instanceof Done) {
			handleDone((Done) o);
		} else {
			log.error("Unknown message {}", o);
		}
	}

	private void handleDone(Done o) {
		ActorRef sender = sender();
		positions.remove(sender);
		if (positions.size() == 0) {
			log.info("All done, initiating shutdown");
			context().system().shutdown();
		}
		else {
			log.info("Sender {} is done. Left: {}", sender.path(), positions.size());
		}
	}

	private void handleNewMove(Move msg) {
		ActorRef actorRef = sender();
		for (; ; ) {
			int hash = positionHash();
			Blocked blocker = canMove(actorRef, msg.position);
			if (hash != positionHash()) {
				continue;
			}

			if (blocker == null) {
				positions.put(actorRef, msg.position);
				actorRef.tell(new Okay(msg.position), self());
			} else {
				actorRef.tell(blocker, self());
			}
			return;
		}
	}

	private Blocked canMove(ActorRef key, Pos desiredPos) {
		Pos current = positions.get(key);

		HorzVector horz = new HorzVector(current.x, desiredPos.x, current.y);

		Vector vector = !horz.isStill() ? horz : new VertVector(current.y, desiredPos.y, current.x);

		for (ActorRef ref : positions.keySet()) {
			if (!ref.equals(key)) {
				Pos otherPosition = positions.get(ref);
				if (vector.contains(otherPosition))
					return new Blocked(desiredPos, vector, ref, otherPosition);
			}
		}
		return null;
	}

	private void handleNewGame(NewGame msg) {
		int total = msg.castleNum;
		log.info("Will start the game with {} castles.", total);
		int[] xs = randomArray(total);
		int[] ys = randomArray(total);
		for (int i = 0; i < total; i++) {
			ActorRef castle = context().actorOf(Props.create(Castle.class), "Castle_" + i);
			Pos pos = new Pos(xs[i], ys[i]);
			castle.tell(new Okay(pos), self());
			positions.put(castle, pos);
		}
	}

	@SuppressWarnings("unchecked")
	private int[] randomArray(int total) {
		Set s = new HashSet<Integer>();
		while (s.size() != total) {
			s.add(rnd.nextInt(SIZE));
		}

		Object[] boxed = s.toArray();
		int[] result = new int[total];
		for (int i = 0; i < total; i++) {
			result[i] = (int) boxed[i];
		}
		return result;
	}

	private int positionHash() {
		int code = 0;
		for (Pos p : positions.values())
			code ^= p.hashCode();
		return code;
	}
}
