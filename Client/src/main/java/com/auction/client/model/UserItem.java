package com.auction.client.model;

public class UserItem {
    private String id;
    private String username;
    private String email;
    private String role;
    private String status;

    public UserItem(String id, String username, String email, String role, String status) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.role     = role;
        this.status   = status;
    }

    public String getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }
    public String getStatus()   { return status; }

    public void setStatus(String status) { this.status = status; }
}
