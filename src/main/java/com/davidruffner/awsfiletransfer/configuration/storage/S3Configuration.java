package com.davidruffner.awsfiletransfer.configuration.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Configuration {
    private static final String S3_HOST = "http://{address}:{port}";

    @Autowired
    Environment environment;

    private Optional<Integer> port;
    private Optional<String> address;
    private Optional<String> regionName;
    private Optional<String> accessKey;
    private Optional<String> secretKey;

    private boolean isConfigReady() {
        return this.port.isPresent() && this.address.isPresent() &&
                this.regionName.isPresent() && this.accessKey.isPresent() &&
                this.secretKey.isPresent();
    }

    private String getURL() {
        return S3_HOST
                .replace("{address}", this.address.get())
                .replace("{port}", String.valueOf(this.port.get()));
    }

    public AmazonS3 getS3Client() throws RuntimeException {
        if (!isConfigReady())
            throw new RuntimeException("S3 Configuration Not Ready");

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                this.accessKey.get(), this.secretKey.get()
        );

        EndpointConfiguration endpointConfiguration
                = new EndpointConfiguration(
                        getURL(), this.regionName.get()
        );

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public void setPort(Optional<Integer> port) {
        this.port = port;
    }

    public Optional<String> getAddress() {
        return address;
    }

    public void setAddress(Optional<String> address) throws UnknownHostException {
        if (address.isPresent() && address.get().equals("localhost")) {
            this.address = Optional.of(InetAddress.getLocalHost().getHostAddress());
        } else {
            this.address = address;
        }
    }

    public Optional<String> getRegionName() {
        return regionName;
    }

    public void setRegionName(Optional<String> regionName) {
        this.regionName = regionName;
    }

    public Optional<String> getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(Optional<String> accessKey) {
        this.accessKey = accessKey;
    }

    public Optional<String> getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Optional<String> secretKey) {
        this.secretKey = secretKey;
    }
}
