import java.net.InetAddress

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.FtpSettings
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.scaladsl.{RunnableGraph, Sink}

// this kata shows how to list files from an anonymous FTP server using alpakka
object Kata12 extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("akka-streams-example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val ftpSettings = FtpSettings(InetAddress.getByName("192.168.100.8"))
    .withBinary(true)
    .withPassiveMode(true)
//    .withConfigureConnection((ftpClient: FTPClient) => {
//      ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true))
//    })

  val helloGraph: RunnableGraph[NotUsed] =
    Ftp.ls("/", ftpSettings)
    .to(Sink.foreach(fileName => println(fileName)))

  helloGraph.run()
}
