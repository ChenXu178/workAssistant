package com.chenxu.workassistant.photoRecognition;

public class TextBean {
    private String text;
    private boolean isSelect;

    public TextBean() {
    }

    public TextBean(String text, boolean isSelect) {
        this.text = text;
        this.isSelect = isSelect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

}
