import java.io.PrintWriter
import java.net.InetAddress

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.{FtpFile, FtpSettings}
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink}
import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient

object Kata12 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val ftpSettings = FtpSettings(InetAddress.getByName("192.168.100.8"))
    .withBinary(true)
    .withPassiveMode(true)
    .withConfigureConnection((ftpClient: FTPClient) => {
      ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true))
    })

  Ftp.ls("/", ftpSettings)
    .to(Sink.foreach(println))

  val helloGraph: RunnableGraph[NotUsed] =

    Ftp.ls("/", ftpSettings)
      .to(Sink.foreach(file => println(file.name)))

  helloGraph.run()
}
