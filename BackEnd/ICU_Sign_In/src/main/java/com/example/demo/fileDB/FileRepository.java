package com.example.demo.fileDB;

import com.example.demo.model.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileDocument, Long> {
    FileDocument findByFileName(String fileName);
}
