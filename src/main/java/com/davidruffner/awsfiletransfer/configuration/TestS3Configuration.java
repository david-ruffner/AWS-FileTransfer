package com.davidruffner.awsfiletransfer.configuration;

import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
//@Component("TestS3Configuration")
@Qualifier("testS3Configuration")
public class TestS3Configuration extends S3ConfigurationBase {
    private int port = 0;
    private String address;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    protected void isConfigReady() throws RuntimeException {
        if (null == this.accessKey || this.accessKey.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
        else if (null == this.secretKey || this.secretKey.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
        else if (null == this.bucketName || this.bucketName.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
        else if (null == this.address || this.address.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
        else if (this.port == 0)
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
    }

    @Override
    public String getURL(String keyName) {
        isConfigReady();
        String S3_HOST = "http://{address}:{port}/{bucketName}/{keyName}";

        if (null == keyName || keyName.isEmpty()) {
            throw new RuntimeException("Provided keyName was null or empty");
        }

        return S3_HOST
                .replace("{address}", this.address)
                .replace("{port}", String.valueOf(this.port))
                .replace("{bucketName}", this.bucketName)
                .replace("{keyName}", keyName);
    }

    @Override
    public Regions getRegionName() {
        return Regions.US_EAST_2;
    }
}
