package com.cloudkibo.kiboengage.model;

/**
 * Created by sojharo on 18/10/2016.
 */
public class BulkSMS
{

    private String title;
    private String msg;
    private String date;
    private int image;


    public BulkSMS(String title, String msg, String date, int image) {
        this.title = title;
        this.msg = msg;
        this.date = date;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
