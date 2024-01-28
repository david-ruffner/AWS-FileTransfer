package com.davidruffner.awsfiletransfer.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.davidruffner.awsfiletransfer.configuration.LiveS3Configuration;
import com.davidruffner.awsfiletransfer.configuration.S3ConfigurationBase;
import com.davidruffner.awsfiletransfer.configuration.TestS3Configuration;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component("S3Storage")
public class S3Storage {
    private AmazonS3 s3Client;
    private S3ConfigurationBase s3Configuration;

    private String getBucket() {
        return this.s3Configuration.getBucketName();
    }

    public S3Storage() {}

    public S3Storage(LiveS3Configuration liveS3Configuration) {
        this.s3Configuration = liveS3Configuration;

        AWSCredentials credentials = new BasicAWSCredentials(
                s3Configuration.getAccessKey(),
                s3Configuration.getSecretKey()
        );

        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(s3Configuration.getRegionName())
                .build();
    }

    public S3Storage(TestS3Configuration testS3Configuration) {
        this.s3Configuration = testS3Configuration;

        AWSCredentials credentials = new BasicAWSCredentials(
                s3Configuration.getAccessKey(),
                s3Configuration.getSecretKey()
        );

        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(
                        "http://192.168.50.121:9000", "US_EAST_2"
                );

        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public void uploadObject(String keyName, InputStream inputStream) throws RuntimeException {
        try {
            this.s3Client.putObject(getBucket(), keyName, inputStream, null);
        } catch (Exception ex) {
            // TODO: Log
            throw new RuntimeException(String.format("S3 Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public void uploadObject(String keyName, InputStream inputStream,
                             ObjectMetadata metadata) throws RuntimeException {
        try {
            s3Client.putObject(getBucket(), keyName, inputStream, metadata);
        } catch (Exception ex) {
            // TODO: Log
            throw new RuntimeException(String.format("S3 Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public void renameObject(String oldKeyName, String newKeyName) throws RuntimeException {
        try {
            s3Client.copyObject(getBucket(), oldKeyName, getBucket(), newKeyName);
            s3Client.deleteObject(getBucket(), oldKeyName);
        } catch (AmazonS3Exception ex) {
            switch (ex.getErrorCode()) {
                case "NoSuchKey":
                    throw new RuntimeException(String.format("S3 Storage Exception " +
                            "| Key '%s' was not found", oldKeyName));

                default:
                    throw new RuntimeException(String.format("S3 Storage Exception | %s",
                            ex.getMessage()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(String.format("S3 Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public S3StorageObject getObject(String keyName) throws RuntimeException {
        try {
            return new S3StorageObject(s3Client.getObject(getBucket(), keyName),
                    s3Client, getBucket());
        } catch (AmazonS3Exception ex) {
            switch (ex.getErrorCode()) {
                case "NoSuchKey":
                    throw new RuntimeException(String.format("S3 Storage Exception " +
                            "| Key '%s' was not found", keyName));

                default:
                    throw new RuntimeException(String.format("S3 Storage Exception | %s",
                            ex.getMessage()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(String.format("S3 Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public void deleteObject(String keyName) throws RuntimeException {
        try {
            s3Client.deleteObject(getBucket(), keyName);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("S3 Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public void addMetadata(String objectKey, String metaKey,
                            String metaValue) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.addMetadata(metaKey, metaValue);
    }

    public void addMetadata(String objectKey, Map<String, String> metadata)
        throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.addMetadata(metadata);
    }

    public void updateMetadata(String objectKey, String metaKey,
                               String newValue) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.updateMetadata(metaKey, newValue);
    }

    public void updateMetadata(String objectKey, Map<String, String> metadata)
        throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.updateMetadata(metadata);
    }

    public void deleteMetadata(String objectKey, String metaKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.deleteMetadata(metaKey);
    }

    public void deleteAllMetadata(String objectKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        storageObject.deleteAllMetadata();
    }

    public String getMetadata(String objectKey, String metaKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        return storageObject.getMetadata(metaKey);
    }

    public Map<String, String> getAllMetadata(String objectKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey);
        return storageObject.retrieveMetadata().getUserMetadata();
    }

    public Boolean doesObjectExist(String keyName) {
        try {
            S3Object obj = this.s3Client.getObject(getBucket(), keyName);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }
}
