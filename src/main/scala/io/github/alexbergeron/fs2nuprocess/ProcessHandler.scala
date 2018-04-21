package io.github.alexbergeron.fs2nuprocess

import java.nio.ByteBuffer

import com.zaxxer.nuprocess.NuAbstractProcessHandler

class ProcessHandler() extends NuAbstractProcessHandler {

  var onExitCallback: Either[Throwable, Int] => Unit = null
  var onStderrCallback: Either[Throwable, ByteBuffer] => Unit = null
  var onStdoutCallback: Either[Throwable, ByteBuffer] => Unit = null

	override def onExit(exitCode: Int): Unit = {
    onExitCallback(Right(exitCode))
	}

	override def onStderr(buffer: ByteBuffer, closed: Boolean): Unit = {
		if (!closed) {
      onStderrCallback(Right(buffer))
    }
	}

	override def onStdout(buffer: ByteBuffer, closed: Boolean): Unit = {
		if (!closed) {
      onStdoutCallback(Right(buffer))
    }
  }
}
