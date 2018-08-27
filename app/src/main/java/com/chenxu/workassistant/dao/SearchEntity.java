package com.chenxu.workassistant.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SearchEntity {
    @Id(autoincrement = true)
    private Long id;
    private String text;
    private Long time;
    @Generated(hash = 1081364687)
    public SearchEntity(Long id, String text, Long time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }
    @Generated(hash = 1021466028)
    public SearchEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
}
