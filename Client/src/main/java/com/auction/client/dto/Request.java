package com.auction.client.dto;

public class Request {
    private String action;
    private Object payload; // dũ liệu di kèm action có thể là Map hoặc bất kỳ đối tượng nào khác tuỳ theo action

    public Request(String action, Object payload) {
        this.action  = action;
        this.payload = payload;
    }
}
