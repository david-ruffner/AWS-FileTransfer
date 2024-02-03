package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class SocketCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, Void> {
    private final AsynchronousServerSocketChannel serverSocketChannel;

    SocketCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
        serverSocketChannel.accept(null, this); // non-blocking
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
        socketChannel.read(buffer, null, readCompletionHandler); // non-blocking

        System.out.println("\nSocket Connected\n");
    }
    @Override
    public void failed(Throwable t, Void attachment) {
        // exception handling
    }
}
