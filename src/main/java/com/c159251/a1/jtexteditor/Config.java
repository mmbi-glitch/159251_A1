package com.c159251.a1.jtexteditor;



import java.io.File;

public class Config {


    private String username;
    private double width;
    private double height;
    private double xPos;
    private double yPos;

    public Config() {

    }

    public Config(String username, double width, double height, double xPos, double yPos) {
        this.username = username;
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public String toString() {
        return username;
    }

}
