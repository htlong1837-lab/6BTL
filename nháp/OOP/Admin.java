
// sai hết admin
import java.util.List;
 
public class Admin extends User {
 
    public Admin(String id, String name, String email, String password) {
        super(id, name, email, password);
    }
 
    /** Xóa một phiên đấu giá khỏi danh sách */
    public void removeAuction(List<Auction> auctions, Auction target) {
        if (auctions.remove(target)) {
            System.out.println("[Admin] Auction for \"" + target.getItem().getName() + "\" has been removed.");
        } else {
            System.out.println("[Admin] Auction not found.");
        }
    }
 
    /** Khóa tài khoản người dùng (ví dụ khi vi phạm) */
    public void banUser(User user) {
        System.out.println("[Admin] User \"" + user.getName() + "\" has been banned.");
        // TODO: thêm trường isActive vào User nếu cần xử lý thật
    }
    // Mở lại tài khoản người dùng
    public void isActive(User user) {
        System.out.println("[Admin] User \"" + user.getName() + "\" has been activated. You can now access");
    }
    // Duyệt sản phẩm
    public void approveItem(Item item) {
        if (item.isApproved()) {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is already approved. It's ready for auction right now");
        } else {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is invalid. Please check the details and edit it for approval.");
        }
    }
    // Ghi nhận hành vi đánh giá bất thường
    public void flagSuspiciousActivity(User user) {
        if (user.login(false))



    /** Xem toàn bộ phiên đấu giá */
    public void listAllAuctions(List<Auction> auctions) {
        System.out.println("[Admin] All auctions (" + auctions.size() + "):");
        for (Auction a : auctions) {
            System.out.println("  - " + a.getItem().getName()
                    + " | Status: " + a.getStatus()
                    + " | Current price: " + a.getCurrentPrice());
        }
    }
 
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("[Admin account – full system access]");
    }
}
 