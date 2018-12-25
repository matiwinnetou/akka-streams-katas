import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

// .splitAfter
// This operation applies the given predicate to all incoming elements and
// emits them to a stream of output streams. It *ends* the current substream when the
// predicate is true.

object Kata15 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val text =
    "This is the first line.\n" +
      "The second line.\n" +
      "There is also the 3rd line\n"

  val charCount = Source(text.toList)
    .splitAfter(c => c == '\n')
    .filter(_ != '\n')
    .map(c => 1)
    .reduce((c1, c2) => c1 + c2)
    .to(Sink.foreach(println))
    .run()
}

// output
//23
//16
//26
