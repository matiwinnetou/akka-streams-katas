import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

object Kata3 extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Source('A' to 'E')
    .map(letter => (1 to 3).map(index => s"$letter$index"))
    .runForeach(println)
}
