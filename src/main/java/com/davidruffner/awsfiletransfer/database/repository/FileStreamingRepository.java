package com.davidruffner.awsfiletransfer.database.repository;

import com.davidruffner.awsfiletransfer.database.entities.FileStreamingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStreamingRepository extends JpaRepository<FileStreamingEntity, String> {
}
