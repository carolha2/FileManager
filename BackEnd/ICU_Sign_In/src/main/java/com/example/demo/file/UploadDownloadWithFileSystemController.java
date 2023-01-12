package com.example.demo.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "")
public class UploadDownloadWithFileSystemController {
    private static final Logger LOGGER = LogManager.getLogger(UploadDownloadWithFileSystemController.class.getSimpleName());

    @Autowired
    private FileStorageService fileStorageService;

    public UploadDownloadWithFileSystemController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("single/upload")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file){
        String fileName = fileStorageService.storeFile(file);
        String contentType = file.getContentType();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println("name "+currentPrincipalName);
        String url = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/single/download/")
                .path(fileName)
                .toUriString();
        FileUploadResponse response = new FileUploadResponse(fileName,contentType , url , currentPrincipalName , false);

        return response;

    }
    @PostMapping("multi/upload")
    public List<FileUploadResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        if(files.length >7){
            throw new RuntimeException("Too many files");
        }
        return Arrays.asList(files)
                .stream()
                .map(file -> singleFileUpload(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/single/download/{fileName}")
    ResponseEntity<Resource>singleFileDownload(@PathVariable String fileName, HttpServletRequest httpServletRequest){
        Resource resource= fileStorageService.downloadFile(fileName);
        String mimeType;
        try {
            mimeType = httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mimeType= MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName="+resource.getFilename())
                .body(resource);

    }
    @GetMapping("/single/render/{fileName}")
    ResponseEntity<Resource>singleFileRender(@PathVariable String fileName, HttpServletRequest httpServletRequest){
        Resource resource= fileStorageService.downloadFile(fileName);
        String mimeType;
        try {
            mimeType = httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mimeType= MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        MediaType contentType= MediaType.TEXT_PLAIN;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName="+resource.getFilename())
                .body(resource);

    }
    @GetMapping("zipDownload")
    void zipDownload(@RequestParam("fileName") String[] files , HttpServletResponse response) throws IOException {
        try(ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())){
            Arrays.asList(files)
                    .stream()
                    .forEach(file-> {
                        Resource resource = fileStorageService.downloadFile(file);
                        ZipEntry zipEntry = new ZipEntry(resource.getFilename());
                        try {
                            zipEntry.setSize(resource.contentLength());
                            zos.putNextEntry(zipEntry);
                            StreamUtils.copy(resource.getInputStream() , zos);
                            zos.closeEntry();;
                        } catch (IOException e) {
                            System.out.println("exception with ZIP");
                        }
                    });
            zos.finish();
        }
        response.setStatus(200);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=zipfile ");
    }

}
