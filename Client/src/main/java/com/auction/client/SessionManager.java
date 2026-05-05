package com.auction.client;

public class SessionManager {
    private static SessionManager instance;
    private String userId, username, role;
    private double balance;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void init(String userId, String username, String role, double balance) {
        this.userId = userId; this.username = username;
        this.role = role;    this.balance = balance;
    }

    public String getUserId()   { return userId; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public double getBalance()  { return balance; }
    public void setBalance(double b) { this.balance = b; }
    public void clear() { userId = null; username = null; role = null; balance = 0; }
}
