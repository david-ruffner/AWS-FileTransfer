package com.davidruffner.awsfiletransfer.action;

import com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.CONTAINER_NAME;
import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.KEY_NAME;

public abstract class ActionBase {

    protected StorageBase storageController;
    protected ValidationConfiguration validationConfiguration;
    protected String keyName;
    protected String containerName;

    protected ActionResponse doAction() {
        ActionResponse verifyResp = verifyParams();
        if (null != verifyResp) {
            return verifyResp;
        } else {
            return defineAction();
        }
    }

    protected ActionResponse verifyKeyName() {
        if (!validationConfiguration.verifyKeyName(this.keyName)) {
            return new ActionResponse.ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given keyName '%s' is invalid. %s",
                            this.keyName, validationConfiguration
                                    .generateErrorMessage(KEY_NAME)))
                    .build();
        }

        return null;
    }

    protected ActionResponse verifyContainerName() {
        if (!validationConfiguration.verifyContainerName(this.containerName)) {
            return new ActionResponse.ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given containerName '%s' is invalid. %s",
                            this.containerName, validationConfiguration
                                    .generateErrorMessage(CONTAINER_NAME)))
                    .build();
        }

        return null;
    }

    protected abstract ActionResponse verifyParams();
    protected abstract ActionResponse defineAction();
}
