package com.auction.common.protocol;

public class Request {
    private String action;
    private String token;
    private Object payload;

    public Request() {}

    public Request(String action, Object payload  ) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction()  {return action;}
    public String getToken() {return token;}
    public Object getPayload() {return payload;}

    public void setAction(String action ) { this.action = action; }
    public void setToken(String token) {this.token = token ;}
    public void setData (Object payload) {this.payload = payload ;}
    
}