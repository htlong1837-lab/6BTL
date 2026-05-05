package com.auction.client.dto;

public class Request {
    private String action;
    private Object payload;

    public Request(String action, Object payload) {
        this.action  = action;
        this.payload = payload;
    }
}
