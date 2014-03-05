package zingaya.chess;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import zingaya.chess.messages.NewGame;

public class Main {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("chess");
		LoggingAdapter log = Logging.getLogger(system, "Whatever");

		ActorRef table = system.actorOf(Props.create(Chessboard.class), "table");
		table.tell(new NewGame(6), ActorRef.noSender());

		log.info("Awaiting game to finish.");
		system.awaitTermination();
	}
}
