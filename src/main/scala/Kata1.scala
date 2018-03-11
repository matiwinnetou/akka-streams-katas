import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

object Kata1 extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val helloGraph: RunnableGraph[NotUsed] =
    Source.single("Hello world")
      .via(Flow[String].map(s => s.toUpperCase()))
      .to(Sink.foreach(println))

  helloGraph.run()

  actorSystem.terminate()
}

