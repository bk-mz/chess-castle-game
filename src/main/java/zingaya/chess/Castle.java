package zingaya.chess;

import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import zingaya.chess.matan.Pos;
import zingaya.chess.messages.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Castle extends FSM {

	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	final static Random rnd = new Random();

	Pos position;
	Pos desiredPos;

	int movesLeft = 50 + 1; // 1 is for initial setup

	@Override
	public void onReceive(Object o) throws Exception {
		log.debug("Handling {}, state {}", o, getState());
		switch (getState()) {
			case BLOCKED:
				if (o instanceof Okay) {
					acknowledge((Okay) o);
					nextState();
				} else if (o instanceof Blocked) {
					log.debug("Still blocked...");
					scheduleTryAgain();
				} else if (o instanceof Unblock) {
					log.debug("Unblocking");
					desiredPos = newPosition();
					scheduleDeblock();
				}
				break;
			case DONE:
				log.debug("Nope, i'm done.");
				break;
			default: // any other state
				if (o instanceof Okay) {
					acknowledge((Okay) o);
					nextState();
				} else if (o instanceof Blocked) {
					Blocked msg = (Blocked) o;
					desiredPos = msg.desiredPos;
					setState(State.BLOCKED);
					log.debug("Blocked by {}", msg.blocker);
				} else if (o instanceof Unblock) {
					log.debug("Whatever...");
				} else {
					log.error("{} Don't know what to do with message {}", getState(), o);
				}
		}
	}

	@Override
	protected void transition(State old, State next) {
		log.debug("state changed: {} => {}", old, next);
		if (next == State.M1) {
			scheduleMove(m1Cooldown(), newPosition());
		} else if (next == State.M2) {
			scheduleMove(m2Cooldown(), newPosition());
		} else if (next == State.BLOCKED) {
			scheduleTryAgain();
			scheduleDeblock();
		} else if (next == State.DONE) {
			done();
		}
	}

	private void done() {
		context().parent().tell(new Done(), self());
	}

	static FiniteDuration m1Cooldown() {
		return Duration.create(10, TimeUnit.MILLISECONDS);
	}

	static FiniteDuration m2Cooldown() {
		return Duration.create(200 + rnd.nextInt(100), TimeUnit.MILLISECONDS);
	}

	private void scheduleMove(FiniteDuration cooldown, Pos desired) {
		context().system().scheduler().scheduleOnce(
				cooldown,
				context().parent(),
				new Move(desired),
				context().system().dispatcher(),
				self());
	}

	private void scheduleDeblock() {
		context().system().scheduler().scheduleOnce(
				Duration.create(5, TimeUnit.SECONDS),
				self(),
				new Unblock(),
				context().system().dispatcher(),
				self());
	}

	private void scheduleTryAgain() {
		context().system().scheduler().scheduleOnce(
				m1Cooldown(),
				context().parent(),
				new Move(desiredPos),
				context().system().dispatcher(),
				self());
	}

	private Pos newPosition() {
		// true is horz, false is vert
		boolean wantsHorz = rnd.nextBoolean();
		int coord;
		Pos pos = position;
		coord = wantsHorz ? pos.x : pos.y;

		int tmp;
		do {
			tmp = rnd.nextInt(Chessboard.SIZE);
		}
		while (tmp == coord);

		return wantsHorz ? new Pos(tmp, pos.y) : new Pos(pos.x, tmp);
	}

	private void acknowledge(Okay o) {
		position = o.position;
		movesLeft--;
		log.info("Moved to {}, Moves left: {}", o.position, movesLeft);
	}

	private void nextState() {
		if (movesLeft <= 0) {
			setState(State.DONE);
			return;
		}

		if (movesLeft % 2 == 0)
			setState(State.M2);
		else
			setState(State.M1);
	}
}
