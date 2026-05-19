package com.auction.common.protocol;

public class Request {
    private String action;
    private Object payload;

    public Request() {}

    public Request(String action, Object payload  ) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction()  {return action;}
    public Object getPayload() {return payload;}

    public void setAction(String action) { this.action = action; }
    public void setData (Object payload) {this.payload = payload ;}
    
}