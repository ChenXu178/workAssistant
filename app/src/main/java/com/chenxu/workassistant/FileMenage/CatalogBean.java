package com.chenxu.workassistant.FileMenage;

import java.io.File;

public class CatalogBean {
    private File file;
    private int showIndex;

    public CatalogBean(File file, int showIndex) {
        this.file = file;
        this.showIndex = showIndex;
    }

    public CatalogBean() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(int showIndex) {
        this.showIndex = showIndex;
    }
}

