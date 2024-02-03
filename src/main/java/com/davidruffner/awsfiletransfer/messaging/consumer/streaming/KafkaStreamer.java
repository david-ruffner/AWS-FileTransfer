//package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;
//
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.InputStream;
//import java.util.Arrays;
//
//@Component
//public class KafkaStreamer {
//
//    private static final Serde<String> STRING_SERDE = Serdes.String();
//
//    @Autowired
//    void buildPipeline(StreamsBuilder streamsBuilder) {
//        InputStream inputStream = new
//
//        KStream<String, String> messageStream = streamsBuilder
//                .stream("DATA_STREAM", Consumed.with(STRING_SERDE, STRING_SERDE));
//
//        KTable<String, Long> wordCounts = messageStream
//                .mapValues((ValueMapper<String, String>) String::toLowerCase)
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
//                .count();
//
////        wordCounts.toStream().foreach((k, v) -> {
////            System.out.printf("\nKey: %s | Val: %s\n\n", k, v);
////        });
//
//        wordCounts.toStream().to("AWS_FILE_TRANSFER");
//    }
//}
