package com.davidruffner.awsfiletransfer.configuration;

import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
//@Component("LiveS3Configuration")
@Qualifier("liveS3Configuration")
public class LiveS3Configuration extends S3ConfigurationBase {
    private String serviceName;
    private String regionName;

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
        else if (null == this.serviceName || this.serviceName.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
        else if (null == this.regionName || this.regionName.isEmpty())
            throw new RuntimeException("Tried to retrieve S3 URL before config was ready");
    }

    @Override
    public String getURL(String keyName) {
        isConfigReady();
        String S3_HOST =
                "https://{serviceName}.{regionName}.amazonaws.com/{bucketName}/{keyName}";

        if (null == keyName || keyName.isEmpty()) {
            throw new RuntimeException("Provided keyName was null or empty");
        }

        return S3_HOST
                .replace("{serviceName}", this.serviceName)
                .replace("{regionName}", this.regionName)
                .replace("{bucketName}", this.bucketName)
                .replace("{keyName}", keyName);
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public Regions getRegionName() {
        isConfigReady();

        try {
            return Regions.valueOf(this.regionName);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "Could not convert region name '%s' to proper AWS region", this.regionName));
        }
    }
}
