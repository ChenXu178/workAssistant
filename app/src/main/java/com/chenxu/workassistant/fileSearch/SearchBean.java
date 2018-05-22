package com.chenxu.workassistant.fileSearch;

import java.io.File;

public class SearchBean {
    private File file;
    private int index;

    public SearchBean() {
    }

    public SearchBean(File file, int index) {
        this.file = file;
        this.index = index;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
