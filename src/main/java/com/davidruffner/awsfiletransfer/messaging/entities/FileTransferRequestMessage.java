package com.davidruffner.awsfiletransfer.messaging.entities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class FileTransferRequestMessage {
    private String token;
    private Timestamp timestamp;
    private FileTransferRequest payload;

    public FileTransferRequestMessage(String token, FileTransferRequest payload) {
        this.token = token;
        this.timestamp = new Timestamp(Instant.now().toEpochMilli());
        this.payload = payload;
    }

    public FileTransferRequestMessage(String token, FileTransferRequest payload,
                                      Timestamp timestamp) {
        this.token = token;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public FileTransferRequestMessage(String token, String payload) {
        this.token = token;
        this.timestamp = new Timestamp(Instant.now().toEpochMilli());
        this.payload = new FileTransferRequest(payload);
    }

    public FileTransferRequestMessage(String token, String payload,
                                      Timestamp timestamp) {
        this.token = token;
        this.timestamp = timestamp;
        this.payload = new FileTransferRequest(payload);
    }

    public String getToken() {
        return token;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public FileTransferRequest getPayload() {
        return payload;
    }

    public static String serialize(FileTransferRequestMessage message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(FileTransferRequestMessage.class, new FTRMessageSerializer());
        mapper.registerModule(module);

        return mapper.writeValueAsString(message);
    }

    public static FileTransferRequestMessage deserialize(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(FileTransferRequestMessage.class,
                new FTRMessageDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(message, FileTransferRequestMessage.class);
    }

    public static class FTRMessageDeserializer extends StdDeserializer<FileTransferRequestMessage> {
        public FTRMessageDeserializer() {
            this(null);
        }

        public FTRMessageDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public FileTransferRequestMessage deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            Timestamp timestamp;
            String token = node.get("token").asText();
            String payload = node.get("payload").asText();

            String currentTimeStr = node.get("timestamp").asText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                Date parsedDate = dateFormat.parse(currentTimeStr);
                timestamp = new Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return new FileTransferRequestMessage(token, payload, timestamp);
        }
    }

    public static class FTRMessageSerializer extends StdSerializer<FileTransferRequestMessage> {
        public FTRMessageSerializer() {
            this(null);
        }

        public FTRMessageSerializer(Class<FileTransferRequestMessage> t) {
            super(t);
        }

        @Override
        public void serialize(FileTransferRequestMessage ftrObj, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("token", ftrObj.getToken());
            jsonGenerator.writeStringField("timestamp", ftrObj.getTimestamp().toString());
            jsonGenerator.writeStringField("payload",
                    FileTransferRequest.encode(ftrObj.getPayload()));
            jsonGenerator.writeEndObject();
        }
    }
}
