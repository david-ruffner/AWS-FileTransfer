package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class WriteCompletionHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel socketChannel;

    WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
    @Override
    public void completed(Integer bytesWritten, Void attachment) {
        try {
            System.out.printf("\nBytes Written: %d\n", bytesWritten);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            socketChannel.read(buffer);

            System.out.printf("\n\nBuffer: %s\n\n", StandardCharsets.UTF_8.decode(buffer));

            socketChannel.close();
        } catch (IOException e) {
            // exception handling
        }
    }
    @Override
    public void failed(Throwable t, Void attachment) {
        // exception handling
    }
}
