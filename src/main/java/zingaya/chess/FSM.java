package zingaya.chess;

import akka.actor.UntypedActor;

public abstract class FSM extends UntypedActor {

	protected enum State {
		IDLE, M1, M2, BLOCKED, DONE
	}

	private State state = State.IDLE;

	protected void setState(State s) {
		if (state != s) {
			transition(state, s);
			state = s;
		}
	}

	protected State getState() {
		return state;
	}

	abstract protected void transition(State old, State next);
}
