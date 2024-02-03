package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;

import org.apache.commons.io.output.AppendableOutputStream;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SocketStream {
    public SocketStream( String bindAddr, int bindPort ) throws IOException {
        InetSocketAddress sockAddr = new InetSocketAddress(bindAddr, bindPort);

        //create a socket channel and bind to local bind address
        AsynchronousServerSocketChannel serverSock =  AsynchronousServerSocketChannel.open().bind(sockAddr);

        //start to accept the connection from client
        serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel,AsynchronousServerSocketChannel >() {

            @Override
            public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock ) {
                //a connection is accepted, start to accept next connection
                serverSock.accept( serverSock, this );
                //start to read message from the client
                try {
                    startRead( sockChannel );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
                System.out.println( "fail to accept a connection");
            }

        } );

    }

    private void startRead( AsynchronousSocketChannel sockChannel) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(10000);

        //read message from client
        sockChannel.read( buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            /**
             * some message is read from client, this callback will be called
             */
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel  ) {
                buf.flip();

                if(result == -1){
                    try {
                        System.out.println("Client disconnected");
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                byte[] newBytes = new byte[buf.remaining()];
                buf.get(newBytes);
//                String content = new String(newBytes, StandardCharsets.UTF_8);
//                System.out.printf("\nContent: %s\n", content);

                try {
//                    if (!Files.exists(Paths.get("/tmp/FileTransfer/temp.tmp"))) {
//                        Files.createFile(Paths.get("/tmp/FileTransfer/temp.tmp"));
//                    }
//
//
//                    Files.write(Paths.get("/tmp/FileTransfer/temp.tmp"),
//                            newBytes, StandardOpenOption.APPEND);

//                    String fileName = "/tmp/FileTransfer/temp.tmp";
//                    File file = new File(fileName);
//                    ObjectOutputStream outputStream = null;
//
//                    if (!file.exists()) {
//                        outputStream = new ObjectOutputStream(new FileOutputStream(file));
//                    } else {
//                        outputStream = new AppendableOutputStream<>(new FileOutputStream(fileName, true))
//                    }

                    FileOutputStream fos = new FileOutputStream("/tmp/FileTransfer/temp.jpg", true);
                    fos.write(newBytes);
                    fos.close();

                    System.out.println("\nWrote Bytes\n");

//                    if (totalCount >= 3) {
//                        sockChannel.close();
//                    }
                } catch (Exception ex) {
                    System.out.printf("\nError: %s\n", ex.getMessage());
                }

//                try (FileOutputStream fs = new FileOutputStream("/tmp/FileTransfer/temp.tmp", true)) {
//                    fs.write(newBytes);
//                    System.out.println("\nWrote Bytes\n");
//                    sockChannel.close();
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

                // echo the message
//                startWrite( channel, buf );
//
//                //start to read next message again
                try {
                    startRead( channel );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel ) {
                System.out.println( "fail to read message from client");
            }
        });
    }

    private void startWrite( AsynchronousSocketChannel sockChannel, final ByteBuffer buf) {
        sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //finish to write message to client, nothing to do
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                //fail to write message to client
                System.out.println( "Fail to write message to client");
            }

        });
    }
}
