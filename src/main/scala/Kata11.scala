import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.Done
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future

object Kata11 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  class DB {

    private val offset = new AtomicLong

    def save(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
      println(s"DB.save: ${record.value}")
      offset.set(record.offset)
      Future.successful(Done)
    }

    def loadOffset(): Future[Long] =
      Future.successful(offset.get)

    def update(data: String): Future[Done] = {
      println(s"DB.update: $data")
      Future.successful(Done)
    }
  }

  val db = new DB

  val consumerSettings = ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("192.168.1.215:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val done =
    Consumer.committableSource(consumerSettings, Subscriptions.topics("hello"))
      .mapAsync(1) { msg =>
        import scala.concurrent.ExecutionContext.Implicits.global
        db.update(msg.record.value).map(_ => msg)
      }
      .mapAsync(1) { msg =>
        msg.committableOffset.commitScaladsl()
      }
      .runWith(Sink.foreach(println))
}
