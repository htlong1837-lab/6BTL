import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class User extends Entity{
    protected String id, name, email, passwordHash;
    protected double balance;
    public User(String id, String name, String email, String password) {
        super(id);
        this.name = name;
        this.email = email;
        this.passwordHash = password;
    }
    // hàm lấy
    public String getName() {
        return name;
    }
    public String getEmail()        { return email; }
    public String getPasswordHash()  { return passwordHash;}
    // hàm đặt
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.passwordHash = password; }

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

    // Tìm kiếm User theo tên
    private Map<String, User> usersByUsername = new HashMap<>();
    
    // Tránh email trùng lặp
    private Set<String> emails = new HashSet<>();

    //Mật khẩu hợp lệ
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

    // mã hoá mật khẩu: String -> code(số) -> mã lạ
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    // ------- Đăng kí người dùng + kiểm tra tính hợp lệ --------
    public String signUp(String username, String email, String password, String confirmPassword){
        
        // Tên hợp lệ, tránh để trống và nhập chuỗi rỗng
        if(username == null || username.isBlank()){
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

            // Hash password
            String passwordHash = hashPassword(password);

            // Tạo user
            User user = new User(userid, username, email, passwordHash);

            // Lưu
            usersByUsername.put(username, user);
            emails.add(email);
            
            return "Đăng ký thành công!";
    }
    
    //------------- Đăng nhập ----------------
    public String login(String username, String password) {

        // Kiểm tra tồn tại
        if (!usersByUsername.containsKey(username)) {
            return "Tài khoản không tồn tại";
        }

        User user = usersByUsername.get(username);

        //Hash password nhập vào
        String inputHash = hashPassword(password);

        // So sánh
        if (!user.getPasswordHash().equals(inputHash)) {
            return "Sai mật khẩu";
        }

        return "Đăng nhập thành công!";
    }
}
