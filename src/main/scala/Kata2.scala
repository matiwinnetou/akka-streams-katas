import java.util.UUID

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

object Kata2 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val helloGraph: RunnableGraph[Cancellable] =
    Source.tick(0 second, 1 second, UUID.randomUUID().toString)
      .groupedWithin(10, 5 second)
      .to(Sink.foreach(println))

  helloGraph.run()

  //actorSystem.terminate()
}

// output
//Vector(fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d)
//Vector(fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d)
//Vector(fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d, fcd48665-4c91-456e-a2c3-743abab45d8d)
