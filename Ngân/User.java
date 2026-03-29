package Ngân;
import java.time.LocalDateTime;

public class User {
    private String userid, username, email, password;
    private double balance;
    private LocalDateTime CreateAt;
    private String role;

    public User(String id, String name, String email, String password){
        this.userid = id;
        this.username = name;
        this.email = email;
        this.password = password;
        this.CreateAt = LocalDateTime.now();
        this.balance = 0;
        this.role = "Bidder";
    }
    public String getID(){
        return userid;
    }
    public String getName(){
        return username;
    }
    public String getEmail(){
        return email;
    }
}
