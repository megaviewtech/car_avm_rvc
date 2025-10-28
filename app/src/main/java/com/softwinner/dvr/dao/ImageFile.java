package com.softwinner.dvr.dao;

//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Id;
//import org.greenrobot.greendao.annotation.NotNull;
//import org.greenrobot.greendao.annotation.Generated;


public class ImageFile {
    private Long id;
    String name;
    String path;
    public ImageFile(Long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }
    public ImageFile() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
