package com.ldaniels528.trifecta.modules.kafka

import com.ldaniels528.trifecta.support.io.{KeyAndMessage, OutputHandler}
import com.ldaniels528.trifecta.support.kafka.{Broker, KafkaPublisher}
import com.ldaniels528.trifecta.support.messaging.MessageDecoder

import scala.concurrent.ExecutionContext

/**
 * Kafka Topic Output Source
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
class KafkaTopicOutputHandler(brokers: Seq[Broker], outputTopic: String) extends OutputHandler {
  private val publisher = KafkaPublisher(brokers)
  publisher.open()

  /**
   * Writes the given key-message pair to the underlying stream
   * @param data the given key and message
   * @return the response value
   */
  override def write(data: KeyAndMessage, decoder: Option[MessageDecoder[_]])(implicit ec: ExecutionContext) {
    publisher.publish(outputTopic, data.key, data.message)
  }

  override def close() = publisher.close()


}
