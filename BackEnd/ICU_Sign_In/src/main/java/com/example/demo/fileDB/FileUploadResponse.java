package com.example.demo.fileDB;

import lombok.ToString;

@ToString
public class FileUploadResponse {

    private String fileName;
    private String  contentType;
    private String  url;
    private String username;
    private boolean lock;

    public FileUploadResponse(String fileName, String contentType, String url , String username , boolean lock) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.url = url;
        this.username = username;
        this.lock = lock;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLock() { return lock; }

    public void setLock(boolean lock) { this.lock = lock; }
}

