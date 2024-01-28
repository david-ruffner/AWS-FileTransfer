package com.davidruffner.awsfiletransfer.storage;

import com.davidruffner.awsfiletransfer.configuration.LiveS3Configuration;
import com.davidruffner.awsfiletransfer.configuration.TestS3Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3StorageFactory {
    @Autowired
    LiveS3Configuration liveS3Configuration;

    @Autowired
    TestS3Configuration testS3Configuration;

    public S3Storage getLiveS3Storage() {
        return new S3Storage(this.liveS3Configuration);
    }

    public S3Storage getTestS3Storage() {
        return new S3Storage(this.testS3Configuration);
    }
}
