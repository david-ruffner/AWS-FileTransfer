package com.davidruffner.awsfiletransfer.database.service;

import com.davidruffner.awsfiletransfer.configuration.FileStreamingConfiguration;
import com.davidruffner.awsfiletransfer.database.entities.FileStreamingChunkEntity;
import com.davidruffner.awsfiletransfer.database.entities.FileStreamingChunkEntity.FileChunkComparator;
import com.davidruffner.awsfiletransfer.database.entities.FileStreamingEntity;
import com.davidruffner.awsfiletransfer.database.repository.FileStreamingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class FileStreamingService {
    @Autowired
    FileStreamingRepository repo;

    @Autowired
    FileStreamingConfiguration config;

    public List<String> getSortedChunkPaths(String fileId) {
        Optional<FileStreamingEntity> fileEntityOpt = repo.findById(fileId);
        if (fileEntityOpt.isPresent()) {
            List<FileStreamingChunkEntity> fileChunks = fileEntityOpt.get().getFileChunks();
            fileChunks.sort(new FileChunkComparator());
            return fileChunks.stream().map(FileStreamingChunkEntity::getChunkPath)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | File ID '%s' doesn't exist",
                    fileId));
        }
    }

    public FileStreamingChunkEntity parseFileStreamHeader(byte[] headerBytes) {
        String headerStr = new String(headerBytes, StandardCharsets.UTF_8);
        Matcher matcher = config.getStreamPacketHeaderRegex().matcher(headerStr);
        if (matcher.find()) {
            String fileId = matcher.group(1);
            int chunkNumber = Integer.parseInt(matcher.group(2));
            String nextInstruction = matcher.group(3);

            Optional<FileStreamingEntity> fileEntityOpt = repo.findById(fileId);
            if (fileEntityOpt.isPresent()) {
                FileStreamingEntity fileEntity = fileEntityOpt.get();
                fileEntity.setFileComplete(nextInstruction.equals("s"));
                String chunkId = UUID.randomUUID().toString();

                FileStreamingChunkEntity chunkEntity = new FileStreamingChunkEntity();
                chunkEntity.setChunkId(chunkId);
                chunkEntity.setChunkPosition(chunkNumber);
                chunkEntity.setChunkPath(getChunkPath(fileEntity.getFilePath(), chunkNumber));
                chunkEntity.setFileStreamingEntity(fileEntity);
                fileEntity.addFileChunk(chunkEntity);
                repo.save(fileEntity);

                return chunkEntity;
            } else {
                throw new RuntimeException(String.format(
                        "File Transfer Exception | File ID '%s' doesn't exist",
                        fileId));
            }
        } else {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | FileStream header '%s' malformed",
                    headerStr));
        }
    }

    public void deleteFileStream(FileStreamingEntity fileEntity) {
        repo.delete(fileEntity);
    }

    public FileStreamingEntity createFileStream(String keyName) {
        FileStreamingEntity entity = new FileStreamingEntity();
        entity.setFileId(getStreamID());
        entity.setFilePath(getFilePath(keyName));
        entity.setFileComplete(false);

        return new FileStreamingEntity();
    }
    private String getChunkPath(String filePath, int chunkNumber) {
        return String.format("%s_%d", filePath, chunkNumber);
    }

    private String getFilePath(String keyName) {
        return String.format("%s%s", config.getTempPath(), keyName);
    }

    private String getStreamID() {
        return UUID.randomUUID().toString().substring(0, config.getStreamIdLength()) + "_";
    }
}
