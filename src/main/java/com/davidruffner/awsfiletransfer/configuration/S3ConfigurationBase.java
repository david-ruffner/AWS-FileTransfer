package com.davidruffner.awsfiletransfer.configuration;

import com.amazonaws.regions.Regions;

public abstract class S3ConfigurationBase {
    protected String accessKey;
    protected String secretKey;
    protected String bucketName;

    public abstract void setAccessKey(String accessKey);
    public abstract void setSecretKey(String secretKey);
    public abstract void setBucketName(String bucketName);

    protected abstract void isConfigReady() throws RuntimeException;

    public abstract String getURL(String keyName);

    public abstract Regions getRegionName();

    public String getAccessKey() {
        isConfigReady();
        return accessKey;
    }

    public String getSecretKey() {
        isConfigReady();
        return secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }
}
