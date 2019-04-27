package com.example

import com.example.models.Product
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import jsonMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.log4j.LogManager
import productTopic

import java.util.Properties

fun main(args: Array<String>) {
    StreamsProcessor("localhost:9092").process()
}

class StreamsProcessor(val brokers: String) {

    private val logger = LogManager.getLogger(javaClass)

    fun process() {
        val streamsBuilder = StreamsBuilder()

        val personJsonStream: KStream<String, String> = streamsBuilder
                .stream(productTopic, Consumed.with(Serdes.String(), Serdes.String()))

        val personStream: KStream<String, Product> = personJsonStream.mapValues { v ->
            val product = jsonMapper.readValue(v, Product::class.java)
            logger.debug("Product: $product")
            product
        }

        personStream.mapValues {  _, p ->
            print("VALUE: $p")
        }

        val topology = streamsBuilder.build()

        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["application.id"] = "rtra"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        val streams = KafkaStreams(topology, props)
        streams.start()
    }
}