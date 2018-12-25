import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

object Kata1 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // use fromFunction if you wanna change the type
  val toUpperFlowLength = Flow.fromFunction[String, Int](_.length)

  // use .map if types are the same
  val toUpperFlow = Flow[String].map(_.toUpperCase)

  val helloGraph: RunnableGraph[NotUsed] =

    Source.single("Hello world")
      .via(toUpperFlow)
      .via(toUpperFlowLength)
      .to(Sink.foreach(println))

  helloGraph.run()

  actorSystem.terminate()
}

// output
// 11
