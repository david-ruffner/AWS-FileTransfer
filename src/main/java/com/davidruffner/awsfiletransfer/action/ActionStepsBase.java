//package com.davidruffner.awsfiletransfer.action;
//
//import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
//import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
//import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType;
//
//public class ActionStepsBase StorageControllerStep, KeyNameStep, ContainerNameStep {
//    private StorageBase storageController;
//    private StorageControllerFactory storageControllerFactory;
//    private String keyName;
//    private String containerName;
//
//    private ActionStepsBase(StorageControllerFactory storageControllerFactory) {
//        this.storageControllerFactory = storageControllerFactory;
//    }
//
//    public ActionBuilderBase.KeyNameStep storageController(StorageControllerType controller) {
//        this.storageController =
//                this.storageControllerFactory.getStorageController(controller);
//        return this;
//    }
//
//    public UploadObjectAction.Builder.ContainerNameStep keyName(String keyName) {
//        this.keyName = keyName;
//        return this;
//    }
//
//    public UploadObjectAction.Builder.InputStreamStep containerName(String containerName) {
//        this.containerName = containerName;
//        return this;
//    }
//}
