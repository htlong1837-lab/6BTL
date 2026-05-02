package com.auction.common.protocol;

public class Response {
    private boolean success;
    private String message;
    private Object data;

    public Response() {}
    

    // tạo nhanh response thành công 
    public static Response ok(String message,Object data) {
        Response r = new Response();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    // Tạo nhanh response thất bại 
    public static Response fall (String message) {
        Response r = new Response();
        r.success = false ;
        r.data = null;
        r.message = message;
        return r;
    }

    // getter settter thông thường 

    public boolean isSuccess() {return success;}
    public String getMessage() {return message;}
    public Object getdata() {return data;}

    public void setSuccess(Boolean success) {this.success = success ;}
    public void setMessage(String message) {this.message = message ;}
    public void setData(Object data) {this.data = data;}
}
