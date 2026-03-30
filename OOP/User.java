public class User extends Entity{
    protected String name;
    protected String email;
    protected String password;
    public User(String id, String name, String email, String password) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
    }
    // hàm lấy
    public String getName() {
        return name;
    }
    public String getEmail()    { return email; }
    // hàm đặt
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }
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
}
