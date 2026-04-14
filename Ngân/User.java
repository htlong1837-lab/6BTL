package Ngân;
import java.time.LocalDateTime;

public class User {
    private String userid, username, email, passwordHash;
    private double balance;
    private LocalDateTime createAt;
    private Role role;

    public User(String userid, String username, String email, String passwordHash){
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createAt = LocalDateTime.now();
         // mặc định
        this.balance = 0;
        this.role = Role.BIDDER;
    }


    public String getID(){
        return userid;
    }
    public String getUsername(){
        return username;
    }
    public Role getRole() {
        return this.role; 
    }
    public String getEmail(){
        return email;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public void setRole(Role role) { 
        this.role = role; 
    }
    public void setBalance(double balance){
        this.balance = balance;
    }
    public double getBalance(){
        return this.balance;
    }

}
