import java.io
import java.net.InetAddress
import java.time.{Clock, LocalDateTime}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.alpakka.ftp.{FtpFile, FtpSettings}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

// we will try to read all files from FTP every 30 seconds and dump to console

// this task completely failed but it shows as a side-effect use of zipN operator
object Kata14 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val ftpSettings = FtpSettings(InetAddress.getByName("192.168.100.8"))
    .withBinary(true)
    .withPassiveMode(true)

  val ftpFilesGraph: RunnableGraph[Future[immutable.Seq[FtpFile]]] =
    Ftp.ls("/", ftpSettings)
      .toMat(Sink.seq)(Keep.right)

  val ftpFilesF: Future[immutable.Seq[FtpFile]] = ftpFilesGraph.run

  val files = Await.result(ftpFilesF, 10 minutes)

  val tickSource = Source.tick(0 second, 30 second, files)

  val helloGraph = tickSource
    .to(Sink.foreach((files: immutable.Seq[FtpFile]) => files.foreach(f => println(f.name))))

  helloGraph.run()
}
