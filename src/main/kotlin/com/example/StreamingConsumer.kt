package com.example

import com.example.models.Product
import com.fasterxml.jackson.databind.JsonNode
import jsonMapper
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.connect.json.JsonSerializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.*
import org.apache.kafka.streams.kstream.KStream
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
            logger.debug("Person: $product")
            product
        }

        personStream.mapValues {
            print("VALUE: $it")
        }
        val topology = streamsBuilder.build()

        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["application.id"] = "rtra"
        val streams = KafkaStreams(topology, props)
        streams.start()
    }
}