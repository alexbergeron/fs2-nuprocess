package io.github.alexbergeron.fs2nuprocess

import java.nio.ByteBuffer

import cats.effect.IO
import cats.implicits._
import com.zaxxer.nuprocess.{ NuProcess, NuProcessBuilder }
import fs2.{async, Stream}

import scala.concurrent.ExecutionContext
import scala.collection.JavaConverters._

object ProcessBuilder {

  def create(cmd: Seq[String])(implicit ec: ExecutionContext): IO[Process] = {
    val builder = new NuProcessBuilder(cmd.asJava)
    val handler = new ProcessHandler

    val onExit = IO.async[Int](cb => handler.onExitCallback = cb)
    val onStdout = IO.async[ByteBuffer](cb => handler.onStdoutCallback = cb)
    val onStderr = IO.async[ByteBuffer](cb => handler.onStderrCallback = cb)

    builder.setProcessListener(handler)

    for {
      exit <- onExit.start
      stdout <- onStdout.start
      stderr <- onStderr.start
      process <- IO(new Process(builder.start(), exit.join, stdout.join, stderr.join))
    } yield process
  }

}
