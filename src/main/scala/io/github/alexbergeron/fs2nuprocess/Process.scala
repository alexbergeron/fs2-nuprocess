package io.github.alexbergeron.fs2nuprocess

import java.nio.ByteBuffer

import cats.effect.IO
import com.zaxxer.nuprocess.NuProcess
import fs2.{async, Stream}

import scala.concurrent.ExecutionContext

class Process private (
    nuProcess: NuProcess,
    onExit: (Either[Throwable, Int] => Unit) => Unit,
    onStdout: (Either[Throwable, ByteBuffer] => Unit) => Unit,
    onStderr: (Either[Throwable, ByteBuffer] => Unit) => Unit
)(implicit ec: ExecutionContext) {
  val stdout: Stream[IO, ByteBuffer] = cbToStream(onStdout)
  val stderr: Stream[IO, ByteBuffer] = cbToStream(onStderr)

  val exitCode: IO[Int] = IO.async(onExit)

  val term: IO[Unit] = IO(nuProcess.destroy(false))
  val kill: IO[Unit] = IO(nuProcess.destroy(true))

  def io: Stream[IO, ProcessIO] = stdout.map(ProcessIO.Stdout)
    .merge(stderr.map(ProcessIO.Stderr))
    .merge(exitStream.map(ProcessIO.Exit))
    .takeThrough(!_.isInstanceOf[ProcessIO.Exit])

  private def cbToStream(
    cb: (Either[Throwable, ByteBuffer] => Unit) => Unit
  ): Stream[IO, ByteBuffer] = {
    for {
      q <- Stream.eval(async.boundedQueue[IO, Either[Throwable, ByteBuffer]](10))
      _ <- Stream.eval(IO.async(cb))
      bb <- q.dequeue.rethrow
    } yield bb
  }

  private val exitStream: Stream[IO, Int] = Stream.eval(exitCode)
}

sealed trait ProcessIO extends Product with Serializable
object ProcessIO {
  final case class Stdout private[fs2nuprocess] (buffer: ByteBuffer) extends ProcessIO
  final case class Stderr private[fs2nuprocess] (buffer: ByteBuffer) extends ProcessIO
  final case class Exit private[fs2nuprocess] (statusCode: Int) extends ProcessIO
}
