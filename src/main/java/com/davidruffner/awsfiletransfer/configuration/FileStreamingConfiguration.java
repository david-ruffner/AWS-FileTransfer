package com.davidruffner.awsfiletransfer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.regex.Pattern;

@Configuration
@ConfigurationProperties(prefix = "file-streaming")
public class FileStreamingConfiguration {
    private String tempPath;
    private int streamIdLength;
    private int streamPacketHeaderLength;
    private Pattern streamPacketHeaderRegex;
    private long chunkSize;

    public Pattern getStreamPacketHeaderRegex() {
        return streamPacketHeaderRegex;
    }

    public void setStreamPacketHeaderRegex(String streamPacketHeaderRegex) {
        this.streamPacketHeaderRegex = Pattern.compile(streamPacketHeaderRegex);
    }

    public int getStreamPacketHeaderLength() {
        return streamPacketHeaderLength;
    }

    public void setStreamPacketHeaderLength(int streamPacketHeaderLength) {
        this.streamPacketHeaderLength = streamPacketHeaderLength;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public int getStreamIdLength() {
        return streamIdLength;
    }

    public void setStreamIdLength(int streamIdLength) {
        this.streamIdLength = streamIdLength;
    }
}
