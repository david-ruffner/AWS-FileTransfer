package com.davidruffner.awsfiletransfer.database.entities;

import jakarta.persistence.*;

import java.util.Comparator;

@Entity
@Table(name = "FileStreamingChunk")
public class FileStreamingChunkEntity {

    @Id
    @Column(name = "Chunk_ID")
    private String chunkId;

    @ManyToOne
    @JoinColumn(name = "File_ID")
    private FileStreamingEntity fileStreamingEntity;

    @Column(name = "Chunk_Position")
    private Integer chunkPosition;

    @Column(name = "Chunk_Path")
    private String chunkPath;

    public String getChunkPath() {
        return chunkPath;
    }

    public void setChunkPath(String chunkPath) {
        this.chunkPath = chunkPath;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public FileStreamingEntity getFileStreamingEntity() {
        return fileStreamingEntity;
    }

    public void setFileStreamingEntity(FileStreamingEntity fileStreamingEntity) {
        this.fileStreamingEntity = fileStreamingEntity;
    }

    public Integer getChunkPosition() {
        return chunkPosition;
    }

    public void setChunkPosition(Integer chunkPosition) {
        this.chunkPosition = chunkPosition;
    }

    public static class FileChunkComparator implements Comparator<FileStreamingChunkEntity> {
        @Override
        public int compare(FileStreamingChunkEntity o1, FileStreamingChunkEntity o2) {
            return o1.chunkPosition - o2.chunkPosition;
        }
    }
}
