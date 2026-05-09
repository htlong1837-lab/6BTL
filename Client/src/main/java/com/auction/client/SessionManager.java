package com.auction.client;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SessionManager {
    private static SessionManager instance;
    private String userId, username, role;
    private final DoubleProperty balance = new SimpleDoubleProperty(0);

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void init(String userId, String username, String role, double balance) {
        this.userId = userId; this.username = username;
        this.role = role;    this.balance.set(balance);
    }

    public String getUserId()   { return userId; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public double getBalance()  { return balance.get(); }
    public void setBalance(double b) { balance.set(b); }
    public DoubleProperty balanceProperty() { return balance; }
    public void clear() { userId = null; username = null; role = null; balance.set(0); }
}
