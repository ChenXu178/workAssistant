package com.chenxu.workassistant.collection;

import java.io.File;

public class FileBean {
    private long id;
    private File file;
    private String time;

    public FileBean(long id, File file, String time) {
        this.id = id;
        this.file = file;
        this.time = time;
    }

    public FileBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
