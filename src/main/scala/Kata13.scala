import java.io
import java.net.InetAddress
import java.time.{Clock, LocalDateTime}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.{FtpFile, FtpSettings}
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.concurrent.duration._

// we will try to read all files from FTP every 2 minutes and dump to console

// this task completely failed but it shows as a side-effect use of zipN operator
object Kata13 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val ftpSettings = FtpSettings(InetAddress.getByName("192.168.100.8"))
    .withBinary(true)
    .withPassiveMode(true)

  val tickSource = Source.tick(0 second, 1 second, LocalDateTime.now(Clock.systemDefaultZone()))
  val ftpSource = Ftp.ls("/", ftpSettings)

  // zipN combined two input sources together
  val combined: Source[immutable.Seq[io.Serializable], NotUsed] = Source.zipN(List(ftpSource, tickSource))

  val helloGraph: RunnableGraph[NotUsed] = combined
      .via(Flow.fromFunction((list: immutable.Seq[io.Serializable]) => {
        list.head.asInstanceOf[FtpFile]
      }))
    .to(Sink.foreach(file => println(file.name)))

  helloGraph.run()
}
