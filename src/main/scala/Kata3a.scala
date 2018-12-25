import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}

// this kata demonstrates usage of mapConcat,
// mapConcat will create a list of values after mapping
object Kata3a extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val graph: RunnableGraph[NotUsed] = Source('A' to 'E')
    .mapConcat((letter: Char) => (1 to 3).map(index => s"$letter$index"))
    .to(Sink.foreach(value => println(value)))

  graph.run()

  actorSystem.terminate()
}

//output without mapConcat (just map)
//Vector(A1, A2, A3)
//Vector(B1, B2, B3)
//Vector(C1, C2, C3)
//Vector(D1, D2, D3)
//Vector(E1, E2, E3)

//output with mapConcat
//A1
//A2
//A3
//B1
//B2
//B3
//C1
//C2
//C3
//D1
//D2
//D3
//E1
//E2
//E3