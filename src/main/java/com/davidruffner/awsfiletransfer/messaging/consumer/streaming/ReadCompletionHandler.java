package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCompletionHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer buffer;

    ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
    }
    @Override
    public void completed(Integer bytesRead, Void attachment) {
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        buffer.flip();
        socketChannel.write(buffer, null, writeCompletionHandler); // non-blocking
    }
    @Override
    public void failed(Throwable t, Void attachment) {
        // exception handling
    }
}
