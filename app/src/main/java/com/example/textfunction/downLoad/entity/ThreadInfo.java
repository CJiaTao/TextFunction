package com.example.textfunction.downLoad.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ThreadInfo {

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileLength=" + fileLength +
                ", state=" + state +
                ", title='" + title + '\'' +
                '}';
    }

    @Id(autoincrement = true)
    private long id;
    private String url;
    private long fileLength;
    private int state;
    private String title;

    @Generated(hash = 1553433140)
    public ThreadInfo(long id, String url, long fileLength, int state,
                      String title) {
        this.id = id;
        this.url = url;
        this.fileLength = fileLength;
        this.state = state;
        this.title = title;
    }

    @Generated(hash = 930225280)
    public ThreadInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFileLength() {
        return this.fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
