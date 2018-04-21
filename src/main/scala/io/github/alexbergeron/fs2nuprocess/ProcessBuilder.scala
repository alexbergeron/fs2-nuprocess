package io.github.alexbergeron.fs2nuprocess

import java.nio.ByteBuffer

import cats.effect.IO
import com.zaxxer.nuprocess.{ NuProcess, NuProcessBuilder }
import fs2.{async, Stream}

import scala.concurrent.ExecutionContext

object ProcessBuilder {

  def create(cmd: String)(implicit ec: ExecutionContext): IO[Process] = {
    val builder = new NuProcessBuilder
    val handler = new ProcessHandler

    val onExit = IO.async[Int](cb => handler.onExitCallback = cb)
    val onStdout = IO.async[ByteBuffer](cb => handler.onStdoutCallback = cb)
    val onStderr = IO.async[ByteBuffer](cb => handler.onStderrCallback = cb)

    IO {
      new Process(builder.start(), onExit, onStdout, onStderr)
    }
  }

}
