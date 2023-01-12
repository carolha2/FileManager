package com.example.demo.fileDB;

import com.example.demo.model.FileDocument;
import com.example.demo.model.FileHistory;
import com.example.demo.report.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UploadDownloadWithDatabaseController {

    Logger logger = LoggerFactory.getLogger(UploadDownloadWithDatabaseController.class);

    private FileRepository FileRepository;
    @Autowired
    private ReportRepository reportRepository;


    public UploadDownloadWithDatabaseController(FileRepository FileRepository) {
        this.FileRepository = FileRepository;
    }

    @CachePut(cacheNames = "FileList", key = "#StringUtils.cleanPath(file.getOriginalFilename())")
    @PostMapping("single/uploadDb")
    FileUploadResponse singleFileUplaod(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info(String.valueOf(file));
        logger.info(file.getOriginalFilename());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String name = StringUtils.cleanPath(file.getOriginalFilename());
        FileDocument fileDocument = new FileDocument();
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFromDB/")
                .path(name)
                .toUriString();

        fileDocument.setUri(url);
        String contentType = file.getContentType();
        fileDocument.setContentType(contentType);
        fileDocument.setFileName(name);
        fileDocument.setDocFile(file.getBytes());
        fileDocument.setUsername(currentPrincipalName);
        fileDocument.setLock(false);
        FileDocument doc = FileRepository.findByFileName(name);
        if(doc == null ) {
            FileRepository.save(fileDocument);
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(name);
            fileHistory.setUsername(currentPrincipalName);
            fileHistory.setActionName("Add File");
            fileHistory.setDate(LocalTime.now());
            reportRepository.save(fileHistory);
        }
        else if(doc.getLockedBy().equals(currentPrincipalName)) {
            FileRepository.delete(FileRepository.findByFileName(name));
            fileDocument.setLock(true);
            fileDocument.setLockedBy(currentPrincipalName);
            FileRepository.save(fileDocument);
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(name);
            fileHistory.setUsername(currentPrincipalName);
            fileHistory.setActionName("Update File");
            fileHistory.setDate(LocalTime.now());
            reportRepository.save(fileHistory);
        }
        else
            throw new RuntimeException("File Exists!");

        System.out.println(fileDocument.getUri());
        System.out.println(fileDocument.getContentType());

        FileUploadResponse response = new FileUploadResponse(name, contentType, url,currentPrincipalName,false);

        logger.info(String.valueOf(response));


        return response;

    }

    @GetMapping("/downloadFromDB/{fileName}")
    ResponseEntity<byte[]> downLoadSingleFile(@PathVariable String fileName, HttpServletRequest request) {

        logger.info("hi");
        logger.info(String.valueOf(request));
        FileDocument doc = FileRepository.findByFileName(fileName);

        String mimeType = request.getServletContext().getMimeType(doc.getFileName());

        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName="+ doc.getFileName())
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + doc.getFileName())
                .body(doc.getDocFile());
        logger.info(responseEntity.toString());
        return responseEntity;
    }

}


