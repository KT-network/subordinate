package com.kt.whose.subordinate.HttpEntity;

public class Response<T> {

    private int    code;
    private String msg;
    private T      data;
    private String beFrom;
    private String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBeFrom() {
        return beFrom;
    }

    public void setBeFrom(String beFrom) {
        this.beFrom = beFrom;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
