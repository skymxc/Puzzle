package com.skymxc.demo.puzzle.model;

/**
 * Created by sky-mxc
 * 图片实体类
 */
public class Image {
    public int id;
    public String path;
    public int level;
    public int type;    //0 是系统自带 ，1 是用户添加


    public Image(int id, String path, int level, int type) {
        this.id = id;
        this.path = path;
        this.level = level;
        this.type = type;
    }

    public Image(String path, int level, int type) {
        this.path = path;
        this.level = level;
        this.type = type;
    }

    public Image() {
    }
}
