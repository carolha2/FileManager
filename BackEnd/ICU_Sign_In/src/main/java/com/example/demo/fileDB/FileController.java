package com.example.demo.fileDB;

import com.example.demo.JWT.AuthController;
import com.example.demo.User.UserRepository;
import com.example.demo.User.UserServices;
import com.example.demo.model.FileDocument;
import com.example.demo.model.FileHistory;
import com.example.demo.model.Role;
import com.example.demo.report.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
public class FileController {

    Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    FileServices fileServices;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServices userServices;

    @Autowired
    private ReportRepository reportRepository;



    @GetMapping("/list")
    public List<FileDocument> getAllfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        List<FileDocument> filesOfUser = new ArrayList<>();
        List<FileDocument> returned = new ArrayList<>();
        filesOfUser = fileRepository.findAll();
        if (userServices.isAdmin(userServices.getRole(currentPrincipalName))) {
            logger.info(String.valueOf(filesOfUser));
            return filesOfUser;
        } else {
            for (int i = 0; i < filesOfUser.size(); i++) {
                if (filesOfUser.get(i).getUsername().equalsIgnoreCase(currentPrincipalName)) {
                    returned.add(filesOfUser.get(i));
                }
            }
            logger.info(String.valueOf(returned));
            return returned;
        }

    }


    @CacheEvict(cacheNames = "FileList", key = "#fileName")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Map<String, Boolean>> deleteFile(@PathVariable String fileName) {

        logger.info(fileName);
        FileDocument file = null;
        try {
            file = fileRepository.findByFileName(fileName);
        } catch (Exception e) {
            throw new ResourceNotFoundException("file with name " + fileName + " not found");
        }
        if (file.getLock() == false)
            fileRepository.delete(file);
        else
            throw new RuntimeException("File is Locked!");
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        ResponseEntity responseEntity = ResponseEntity.ok(response);
        logger.info(String.valueOf(responseEntity));
        return responseEntity;
    }
//    @GetMapping("readFile/{fileId}")
//    public ResponseEntity<FileDocument> readFile(@PathVariable Long fileId) {
//        FileDocument fileDocument = fileRepository.findById(fileId)
//                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + fileId));
//        return ResponseEntity.ok(fileDocument);
//    }

    @PostMapping("/lockFile/{fileId}")
    public ResponseEntity<FileDocument> lockFile(@PathVariable Long fileId) {
        logger.info(String.valueOf(fileId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        FileDocument fileDocument = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + fileId));
        if (!fileDocument.getLock()) {
            System.out.println(fileDocument.getLock());
            fileDocument.setLock(true);
            fileDocument.setLockedBy(currentPrincipalName);
            fileRepository.save(fileDocument);
            logger.info(String.valueOf(fileDocument));
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(fileDocument.getFileName());
            fileHistory.setUsername(currentPrincipalName);
            fileHistory.setActionName("Lock File");
            fileHistory.setDate(LocalTime.now());
            reportRepository.save(fileHistory);
            return ResponseEntity.ok(fileDocument);
        } else {
            throw new RuntimeException("File Is Already Locked");
        }

    }

    @PostMapping("/unlockFile/{fileId}")
    public ResponseEntity<FileDocument> unlockFile(@PathVariable Long fileId) {
        logger.info(String.valueOf(fileId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        FileDocument fileDocument = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not exist with id :" + fileId));
        if (fileDocument.getLock() && fileDocument.getLockedBy().equals(currentPrincipalName)) {
            fileDocument.setLock(false);
            fileDocument.setLockedBy(null);
            fileRepository.save(fileDocument);
            logger.info(String.valueOf(fileDocument));
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(fileDocument.getFileName() );
            fileHistory.setUsername(currentPrincipalName);
            fileHistory.setActionName("Unlock File");
            fileHistory.setDate(LocalTime.now());
            reportRepository.save(fileHistory);
            return ResponseEntity.ok(fileDocument);
        } else {
            throw new RuntimeException("File Is Already Locked");
        }

    }

    @PostMapping("/bulkLockFile/{fileIds}")
    public ResponseEntity<List<FileDocument>> bulkLockFile(@PathVariable String fileIds) {
        List<FileDocument> returns = fileServices.lockMultipleById(fileIds);
        return ResponseEntity.ok(returns);
    }

}
