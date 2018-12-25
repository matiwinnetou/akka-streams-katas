import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

object Kata2 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val toUpperFlow = Flow[String].map(_.toUpperCase)

  val helloGraph: RunnableGraph[Cancellable] =
    Source.tick(0 second, 1 second, "tick")
      .via(toUpperFlow)
      .groupedWithin(10, 5 second)
      .to(Sink.foreach(println))

  helloGraph.run()

  //actorSystem.terminate()
}