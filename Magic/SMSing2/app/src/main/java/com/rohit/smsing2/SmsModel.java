package com.rohit.smsing2;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rohit on 1/15/2018.
 */

public class SmsModel { private int id;

    public SmsModel() {
    }



    public SmsModel(String _id, String name, String number, String body, String time, String date,String deleteFlag) {
        this._id = _id;
        this.name = name;
        this.number = number;
        this.body = body;
        this.time=time;
this.deleteFlag=deleteFlag;
        this.date=date;
    }
    private String _id;
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

    public String get_id() {     return _id;   }

    public void set_id(String _id) {       this._id = _id;   }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }




    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_id", _id);
        result.put("name", name);
        result.put("number", number);
        result.put("body", body);
        result.put("time", time);
        result.put("date", date);
        result.put("deleteFlag",deleteFlag);

        return result;
    }




}
