import java.util.Random

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future
import scala.concurrent.duration._

object Kata6 extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val r = new Random()

  def debounceSelect[A](interval: FiniteDuration, pick: Seq[A] => A, max: Int = 100) = Flow[A].groupedWithin(max, interval).map { group => pick(group) }

  val source: Source[Int, NotUsed] = Source.repeat(NotUsed).map(_ => r.nextInt())

  val result: Future[Done] =
    source
      .throttle(50, 1 second, 1, ThrottleMode.Shaping)
      .via(debounceSelect[Int](1 second, _.tail.head))
      .runWith(Sink.foreach(println))
}
