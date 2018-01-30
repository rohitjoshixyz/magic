package com.magic.rohit.smsing;

/**
 * Created by admin on 1/4/2018.
 */

public class SmsModel {
    private int id;

    public SmsModel() {
    }

    public SmsModel(String name, String number, String body,String time,String date,String deleteFlag) {
        this.name = name;
        this.number = number;
        this.body = body;
        this.time=time;
        this.date=date;
        this.deleteFlag=deleteFlag;
    }

    private String name;
    private String number;
    private String body;
    private String date;



    private String time;
    private String deleteFlag;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
