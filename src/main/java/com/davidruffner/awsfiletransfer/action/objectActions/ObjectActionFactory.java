package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.objectActions.UploadObjectAction.Builder.OptionalStep;
import com.davidruffner.awsfiletransfer.action.objectActions.UploadObjectAction.Builder.StorageControllerStep;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.ActionType;
import com.davidruffner.awsfiletransfer.storage.metadata.S3Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.ActionType.*;

@Component
public class ObjectActionFactory {
    @Autowired
    ApplicationContext applicationContext;

    public ActionResponse callAction(FileTransferRequest ftr) {
        switch (ftr.getActionType()) {
            case UPLOAD -> {
                OptionalStep actionBuilder = ((UploadObjectAction.Builder) applicationContext
                    .getBean(ActionType.getBeanName(UPLOAD)))
                    .newBuilder()
                    .storageController(ftr.getControllerType())
                    .keyName(ftr.getKeyName())
                    .containerName(ftr.getContainerName())
                    .inputStream(ftr.getData().orElse(null));

                if (ftr.getMetadataMap().isPresent()) {
                    actionBuilder.withMetadata(new S3Metadata(ftr.getMetadataMap().get()));
                }

                return actionBuilder.doAction();
            }

            case GET -> {
                return ((GetObjectAction.Builder) applicationContext
                        .getBean(ActionType.getBeanName(GET)))
                        .newBuilder()
                        .storageController(ftr.getControllerType())
                        .keyName(ftr.getKeyName())
                        .containerName(ftr.getContainerName())
                        .doAction();
            }

            case MOVE -> {
                MoveObjectAction.MoveObjectActionBuilder.OptionalStep builder =
                    ((MoveObjectAction.MoveObjectActionBuilder) applicationContext
                    .getBean(ActionType.getBeanName(MOVE)))
                    .newBuilder()
                    .storageController(ftr.getControllerType())
                    .keyName(ftr.getKeyName())
                    .oldContainerName(ftr.getContainerName())
                    .newContainerName(ftr.getNewContainerName().orElse(null));

                if (ftr.getNewKeyName().isPresent())
                    builder.withNewKeyName(ftr.getNewKeyName().get());

                return builder.doAction();
            }
        }
        return null;
    }
}
