import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

object Kata2a extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val helloGraph: RunnableGraph[Cancellable] =
    Source.tick(0 second, 1 second, "tick")
      .via(Flow[String].map(s => s.toUpperCase()))
      .groupedWithin(10, 5 second)
      .mapConcat(f => f)
      .to(Sink.foreach(println))

  helloGraph.run()

  //actorSystem.terminate()
}