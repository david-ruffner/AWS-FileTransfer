package com.davidruffner.awsfiletransfer.storage.metadata;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.util.Date;
import java.util.Map;

public class S3Metadata implements MetadataBase {
    private ObjectMetadata objectMetadata;

    public S3Metadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    public S3Metadata(Map<String, String> userMetadata) {
        this.objectMetadata = new ObjectMetadata();
        this.objectMetadata.setUserMetadata(userMetadata);
    }

    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    public Map<String, String> getUserMetadata() {
        return objectMetadata.getUserMetadata();
    }

    public void setUserMetadata(Map<String, String> userMetadata) {
        this.objectMetadata.setUserMetadata(userMetadata);
    }

    public Map<String, Object> getRawMetadata() {
        return this.objectMetadata.getRawMetadata();
    }

    public Object getRawMetadataValue(String key) {
        return this.objectMetadata.getRawMetadataValue(key);
    }

    public Date getHttpExpiresDate() {
        return this.objectMetadata.getHttpExpiresDate();
    }

    public void setHttpExpiresDate(Date httpExpiresDate) {
        this.objectMetadata.setHttpExpiresDate(httpExpiresDate);
    }

    public Date getExpirationTime() {
        return this.objectMetadata.getExpirationTime();
    }

    public void setExpirationTime(Date expirationTime) {
        this.objectMetadata.setExpirationTime(expirationTime);
    }

    public String getExpirationTimeRuleId() {
        return this.objectMetadata.getExpirationTimeRuleId();
    }

    public void setExpirationTimeRuleId(String expirationTimeRuleId) {
        this.objectMetadata.setExpirationTimeRuleId(expirationTimeRuleId);
    }

    public Boolean getOngoingRestore() {
        return this.objectMetadata.getOngoingRestore();
    }

    public void setOngoingRestore(Boolean ongoingRestore) {
        this.objectMetadata.setOngoingRestore(ongoingRestore);
    }

    public Date getRestoreExpirationTime() {
        return this.objectMetadata.getRestoreExpirationTime();
    }

    public void setRestoreExpirationTime(Date restoreExpirationTime) {
        this.objectMetadata.setRestoreExpirationTime(restoreExpirationTime);
    }

    public Boolean getBucketKeyEnabled() {
        return this.objectMetadata.getBucketKeyEnabled();
    }

    public void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
        this.objectMetadata.setBucketKeyEnabled(bucketKeyEnabled);
    }
}
