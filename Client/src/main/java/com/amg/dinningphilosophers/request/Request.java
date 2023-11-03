package com.amg.dinningphilosophers.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
@JsonSerialize
public
class Request implements Serializable {

    RequestType type;
    String data;


    public
    RequestType getType() {
        return type;
    }

    public
    Request setType(RequestType type) {
        this.type = type;
        return this;
    }

    public
    String getData() {
        return data;
    }

    public
    Request setData(String data) {
        this.data = data;
        return this;
    }



    public
    Request(RequestType type, String data) {
        this.type = type;
        this.data = data;

    }


    public
    Request() {

    }


}
