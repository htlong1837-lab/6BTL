
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
 