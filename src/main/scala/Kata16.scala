import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

// https://doc.akka.io/docs/akka/2.5/stream/stream-substream.html

// this kata shows an example
// of groupBy operator
// it will group the same values and split a source into multiple sources
object Kata16 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Source(1 to 10)
    .groupBy(3, value => value % 3)
    .mergeSubstreams
    .runForeach(println)

  actorSystem.terminate()
}

// output:
// 3
// 2
// 1
// 4
// 5
// 6
// 7
// 8
// 9
// 10