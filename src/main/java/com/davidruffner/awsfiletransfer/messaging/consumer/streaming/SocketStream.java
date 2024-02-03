package com.davidruffner.awsfiletransfer.messaging.consumer.streaming;

import com.davidruffner.awsfiletransfer.configuration.FileStreamingConfiguration;
import com.davidruffner.awsfiletransfer.database.entities.FileStreamingChunkEntity;
import com.davidruffner.awsfiletransfer.database.entities.FileStreamingEntity;
import com.davidruffner.awsfiletransfer.database.service.FileStreamingService;
import org.apache.commons.io.output.AppendableOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import java.util.Arrays;
import java.util.List;

@Component
public class SocketStream {
    @Autowired
    FileStreamingConfiguration configuration;

    @Autowired
    FileStreamingService fileStreamingService;

    public SocketStream(@Value("${fileStreaming.hostAddress}") String bindAddr,
                        @Value("${fileStreaming.hostPort}") int bindPort ) throws IOException {
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

                try {
                    byte[] idBytes = Arrays.copyOfRange(newBytes, 0, configuration.getStreamPacketHeaderLength());
                    byte[] dataBytes = Arrays.copyOfRange(newBytes,
                            configuration.getStreamPacketHeaderLength(), newBytes.length);

                    FileStreamingChunkEntity chunkEntity =
                            fileStreamingService.parseFileStreamHeader(idBytes);
                    String filePath = chunkEntity.getFileStreamingEntity().getFilePath();
                    String chunkPath = String.format("%s_%d", chunkEntity.getFileStreamingEntity().getFilePath(),
                            chunkEntity.getChunkPosition());

                    FileOutputStream fos = new FileOutputStream(chunkPath);
                    fos.write(dataBytes);
                    fos.close();

                    if (chunkEntity.getFileStreamingEntity().getFileComplete()) {
                        List<String> chunkPaths = fileStreamingService.getSortedChunkPaths(chunkEntity
                                        .getFileStreamingEntity().getFileId());
                        chunkPaths.forEach(path -> {
                            try (FileInputStream fis = new FileInputStream(path)) {
                                try (FileOutputStream fos2 = new FileOutputStream(filePath, true)) {
                                    fos2.write(fis.readAllBytes());
                                    System.out.println("\nWrote Bytes\n");
                                } catch (Exception ex) {
                                    throw new RuntimeException(String.format(
                                            "File Transfer Exception | %s", ex.getMessage()));
                                }
                            } catch (Exception ex) {
                                throw new RuntimeException(String.format(
                                        "File Transfer Exception | %s", ex.getMessage()));
                            }
                        });

                        chunkPaths.forEach(path -> {
                            try {
                                Files.delete(Paths.get(path));
                            } catch (IOException e) {
                                throw new RuntimeException(String.format(
                                        "File Transfer Exception | %s", e.getMessage()));
                            }
                        });

                        fileStreamingService.deleteFileStream(chunkEntity.getFileStreamingEntity());
                    }
                } catch (Exception ex) {
                    System.out.printf("\nError: %s\n", ex.getMessage());
                }

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
