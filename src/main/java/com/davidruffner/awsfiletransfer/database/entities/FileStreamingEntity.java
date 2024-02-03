package com.davidruffner.awsfiletransfer.database.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FileStreaming")
public class FileStreamingEntity {

    @Id
    @Column(name = "File_ID")
    private String fileId;

    @Column(name = "File_Path")
    private String filePath;

    @Column(name = "Is_File_Complete")
    private Boolean isFileComplete;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "fileStreamingEntity", cascade = CascadeType.ALL)
    private List<FileStreamingChunkEntity> fileChunks = new ArrayList<>();

    public List<FileStreamingChunkEntity> getFileChunks() {
        return fileChunks;
    }

    public void setFileChunks(List<FileStreamingChunkEntity> fileChunks) {
        this.fileChunks = fileChunks;
    }

    public void addFileChunk(FileStreamingChunkEntity fileChunk) {
        this.fileChunks.add(fileChunk);
    }

    public Boolean getFileComplete() {
        return isFileComplete;
    }

    public void setFileComplete(Boolean fileComplete) {
        isFileComplete = fileComplete;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
