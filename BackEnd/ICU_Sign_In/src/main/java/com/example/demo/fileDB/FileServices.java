package com.example.demo.fileDB;

import com.example.demo.model.FileDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServices {

    @Autowired
    FileRepository fileRepository;

    @Transactional
    List<FileDocument> lockMultipleById(String fileIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        List<FileDocument> returns = new ArrayList<>();
        String[] split = fileIds.split(",");
        for(String fileId : split){
            FileDocument fileDocument = fileRepository.findById(Long.parseLong(fileId))
                    .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + fileId));
            if(!fileDocument.getLock()) {
                System.out.println(fileDocument.getLock());
                fileDocument.setLock(true);
                fileDocument.setLockedBy(currentPrincipalName);
                fileRepository.save(fileDocument);
                returns.add(fileDocument);
            }
            else{
                throw new RuntimeException("File Is Already Locked");
            }
        }
        return returns;
    }
}
