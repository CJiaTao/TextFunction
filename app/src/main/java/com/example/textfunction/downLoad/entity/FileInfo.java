package com.example.textfunction.downLoad.entity;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private long id;
    private String url;
    private String fileName;
    private int length;

    public FileInfo() {
    }

    public FileInfo(long id, String url, String fileName, int length) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
