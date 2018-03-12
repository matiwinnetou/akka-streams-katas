import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

object Kata10 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)
  val factorials: Source[BigInt, NotUsed] = source.fold(BigInt(0))((acc, next) => acc + next)

  val incFlow: Flow[BigInt, BigInt, NotUsed] = Flow.fromFunction(i => i + 1)
  val incFlow2: Flow[BigInt, BigInt, NotUsed] = Flow.apply

  val result: Future[Done] =
    factorials
      .via(incFlow)
      .runWith(Sink.foreach(println))
}
