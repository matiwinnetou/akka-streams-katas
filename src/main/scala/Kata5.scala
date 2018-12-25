import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

// this kata demonstrates how to create a source of factorials up to 100 and then pass
// this source to a flow that will incremement factorial numbers by 1
object Kata5 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)
  val factorials: Source[BigInt, NotUsed] = source.fold(BigInt(0))((acc, next) => acc + next)

  val incFlow: Flow[BigInt, BigInt, NotUsed] = Flow.fromFunction(i => i + 1)

  val result: Future[Done] =
    factorials
      .via(incFlow)
      .runWith(Sink.foreach(println))

}
