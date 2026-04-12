package com.auction.user.model;

public class Admin extends User {
    private int adminLevel;
    public Admin(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.adminLevel = 1;
    }
    public int getAdminLevel() {
        return adminLevel;
    }
    public void setAdminLevel(int level) {
        this.adminLevel = level;

    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("ADMIN này có cấp độ :" + adminLevel);
    }


    
}
