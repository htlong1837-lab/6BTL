package Ngân;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserService {
    // Tìm kiếm User theo tên
    private Map<String, User> usersByUsername = new HashMap<>();
    
    // Tránh email trùng lặp
    private Set<String> emails = new HashSet<>();

    //Mật khảu hợp lệ
    private boolean isStrongPassword(String password){
        if(password == null || password.length() < 8){
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for(char c: password.toCharArray()){
            if(Character.isUpperCase(c)){
                hasUpper = true;
            }
            if(Character.isLowerCase(c)){
                hasLower = true;
            }
            if(Character.isDigit(c)){
                hasDigit = true;
            }
        }
         return hasDigit && hasLower && hasUpper;
    }


    // Đăng kí người dùng + kiểm tra tính hợp lệ
    public String signUp(String username, String email, String password, String confirmPassword){
        
        // Tên hợp lệ
        if(username == null){
            return "Tên người dùng không được để trống.";
        }

        if(usersByUsername.containsKey(username)){
            return "Tên người dùng đã tồn tại.";
        }

        // Email hợp lệ
        if(email == null || !email.contains("@")){
            return "Email ko hợp lệ";
        }
        
        if (emails.contains(email)){
            return "Email đã tồn tại";
        }

        // Check password
        if(!isStrongPassword(password)){
            return "Mật khẩu phải chứa ít nhất 8 ký (gồm: chữ cái in thường, in hoa và số)";
        }
        if(!password.equals(confirmPassword)){
            return "Mật khẩu xác nhận không khớp";
        }
        // Tạo ID
        String userid = UUID.randomUUID().toString();

        // 5. Hash password
        String passwordHash = hashPassword(password);

        // 6. Tạo user
        User user = new User(userId, username, email, passwordHash);

        // 7. Lưu
        usersByUsername.put(username, user);
        emails.add(email);

        return "Đăng ký thành công!";
    }


    }
    
}
