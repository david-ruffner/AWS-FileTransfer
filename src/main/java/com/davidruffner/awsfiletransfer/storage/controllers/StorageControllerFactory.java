package com.davidruffner.awsfiletransfer.storage.controllers;

import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.StorageControllerType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.StorageControllerType.getBeanName;

@Component
public class StorageControllerFactory {
//    private S3Storage s3Storage;
//    private Map<StorageControllerType, StorageBase> controllerMap;
    private ApplicationContext applicationContext;

    public StorageControllerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

//        this.s3Storage = s3Storage;
//
//        this.controllerMap = Map.ofEntries(
//                Map.entry(S3, this.s3Storage)
//        );
    }

    public StorageBase getStorageController(StorageControllerType controller) throws RuntimeException {
        try {
            return (StorageBase) this.applicationContext.getBean(getBeanName(controller));
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | %s", ex.getMessage()));
        }

//        if (!this.controllerMap.containsKey(controller)) {
//            throw new RuntimeException(String.format("S3 Storage Exception | " +
//                    "Storage Controller '%s' doesn't exist", controller.name()));
//        }
//
//        return this.controllerMap.get(controller);
    }
}
