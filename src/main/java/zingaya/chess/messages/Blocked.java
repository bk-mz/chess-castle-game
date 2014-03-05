package zingaya.chess.messages;

import akka.actor.ActorRef;
import zingaya.chess.matan.Pos;
import zingaya.chess.matan.Vector;

public class Blocked {

	public final Pos desiredPos;
	public final Vector vector;
	public final ActorRef blocker;
	public final Pos blockingPos;

	public Blocked(Pos nextPos, Vector vector, ActorRef blocker, Pos blockingPos) {
		this.desiredPos = nextPos;
		this.vector = vector;
		this.blocker = blocker;
		this.blockingPos = blockingPos;
	}

	@Override
	public String toString() {
		return "Blocked{" +
				"desiredPos=" + desiredPos +
				", vector=" + vector +
				", blockingPos=" + blockingPos +
				", blocker=" + blocker +
				'}';
	}
}
