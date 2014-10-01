package com.ldaniels528.trifecta.modules.zookeeper

import com.ldaniels528.trifecta.support.avro.AvroDecoder
import com.ldaniels528.trifecta.support.io.{KeyAndMessage, OutputHandler}
import com.ldaniels528.trifecta.support.messaging.MessageDecoder
import com.ldaniels528.trifecta.support.zookeeper.ZKProxy

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Zookeeper Output Writer
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
class ZookeeperOutputHandler(zk: ZKProxy, rootPath: String) extends OutputHandler {

  /**
   * Returns the binary encoding
   * @return the binary encoding
   */
  val encoding: String = "UTF8"

  /**
   * Writes the given key and decoded message to the underlying stream
   * @param data the given key and message
   * @return the response value
   */
  override def write(data: KeyAndMessage, decoder: Option[MessageDecoder[_]])(implicit ec: ExecutionContext) = {
    decoder match {
      case Some(av: AvroDecoder) =>
        av.decode(data.message) match {
          case Success(record) =>
            val path = s"$rootPath/${new String(data.key, encoding)}"
            zk.create(path, data.message)
          case Failure(e) =>
            throw new IllegalStateException(e.getMessage, e)
        }
      case Some(unhandled) =>
        throw new IllegalStateException(s"Unhandled decoder '$unhandled'")
      case None =>
        val path = s"$rootPath/${new String(data.key, encoding)}"
        zk.create(path, data.message)
    }
  }

  override def close() = ()

}
