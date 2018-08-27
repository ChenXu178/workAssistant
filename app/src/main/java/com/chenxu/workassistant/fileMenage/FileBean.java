package com.chenxu.workassistant.fileMenage;

import java.io.File;

/**
 * Created by Android on 2018/3/26.
 */

public class FileBean {
    private File file;
    private int type;
    private boolean showCB;
    private boolean isChecked;

    public FileBean(File file, int type, boolean showCB, boolean isChecked) {
        this.file = file;
        this.type = type;
        this.showCB = showCB;
        this.isChecked = isChecked;
    }

    public FileBean() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShowCB() {
        return showCB;
    }

    public void setShowCB(boolean showCB) {
        this.showCB = showCB;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
