package com.auction.user.model;
import com.auction.common.model.Entity;

public abstract class User extends Entity {
    protected String name;
    protected String email;
    protected String passwordHash;
    protected double balance;
    private int failedLoginAttempts = 0;
    private boolean isBanned = false;

    public User(String id, String name, String email, String passwordHash) {
        super(id);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.failedLoginAttempts = 0;
        this.isBanned = false;
    }

    // Getters
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance()      { return balance; }
    public boolean isBanned()       { return isBanned; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }

    // Setters
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String hash)     { this.passwordHash = hash; }
    public void incrementFailedLoginAttempts() { this.failedLoginAttempts++; }
    public void resetFailedLoginAttempts() { this.failedLoginAttempts = 0; }

    // quyền ban của admin
    public void setBanned(boolean banned)    { this.isBanned = banned; } 


    @Override
    public void printInfo() {
        System.out.println("ID   : " + id);
        System.out.println("Name : " + name);
        System.out.println("Email: " + email);
        System.out.println("Role : " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", name=" + name + "]";
    }

    // Khi chưa quá 5 lần đăng nhập thất bại mà login đúng -> reset số lần thất bại về 0
    public void successfulLogin(User user) {
        if (user.getFailedLoginAttempts() < 5) {
            user.resetFailedLoginAttempts();
        }
        else {
            user.setBanned(true);
            System.out.println("Tài khoản đã bị khóa do quá nhiều lần đăng nhập thất bại. Vui lòng liên hệ hỗ trợ để biết thêm chi tiết.");
        }
    }

}