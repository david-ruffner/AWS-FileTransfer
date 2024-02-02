package com.davidruffner.awsfiletransfer.action;

import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;

public abstract class ActionBase {
    protected StorageBase storageController;
    protected String keyName;
    protected String containerName;

    protected abstract void doAction();
}
