import java.net.InetAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.alpakka.ftp.{FtpFile, FtpSettings}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// we will try to read all files from FTP every 30 seconds and dump to console file names
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

  val tickSource = Source.tick(0 second, 30 second, ftpFilesF)

  val helloGraph = tickSource
    .to(Sink.foreach((filesF: Future[immutable.Seq[FtpFile]]) => {
      import ExecutionContext.Implicits._

      filesF.onComplete {
        case Success(files) => files.foreach(f => println(f.name))
        case Failure(ex) => println(ex.getMessage)
      }
    }))

  helloGraph.run()
}
