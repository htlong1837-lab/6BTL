package Ngân;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidderService {
// ----------- Kho lưu trữ(String = userId)-------------------
    // Ktra user đang đấu giá ở phiên nào 
    private Map<String, List<String>> activeBids  = new HashMap<>();
    // Lịch sử các phiên đấu giá đã thành công
    private Map<String, List<String>> wonAuctions = new HashMap<>();
    // Lịch sử đặt bid
    private Map<String, List<String>> bidHistories = new HashMap<>();

    //kiểm tra role trước mọi thao tác
    private boolean checkBidder(User user) {
        if (user.getRole() != Role.BIDDER) {
            System.out.println("Tài khoản không có quyền Bidder!");
            return false;
        }
        return true;
    }

    //Lấy list theo userId, tạo mới nếu chưa có 
    private List<String> getList(Map<String, List<String>> map, String userId) {
        return map.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    // Nạp tiền
    public String deposit(User user, double amount) {
        if (!checkBidder(user)) return "Không có quyền thực hiện.";
        if (amount <= 0)        return "Số tiền nạp phải lớn hơn 0.";

        user.setBalance(user.getBalance() + amount);
        return "Nạp tiền thành công! Số dư hiện tại: " + user.getBalance();
    }

    // Đặt giá
    public String placeBid(User user, String auctionId, double bidAmount) {
        if (!checkBidder(user))           return "Không có quyền thực hiện.";
        if (bidAmount <= 0)               return "Số tiền đặt giá phải lớn hơn 0.";
        if (user.getBalance() < bidAmount) return "Số dư không đủ. Hiện có: " + user.getBalance();

        String record = "AuctionID: " + auctionId + " | Bid: " + bidAmount;
        getList(bidHistories, user.getID()).add(record);

        List<String> active = getList(activeBids, user.getID());
        if (!active.contains(auctionId)) active.add(auctionId);

        return "Đặt giá thành công! " + record;
    }

    // Rút khỏi phiên đấu giá
    public String withdrawBid(User user, String auctionId) {
        if (!checkBidder(user)) return "Không có quyền thực hiện.";

        List<String> active = getList(activeBids, user.getID());
        if (!active.contains(auctionId)) return "Bạn không tham gia phiên: " + auctionId;

        active.remove(auctionId);
        return "Đã rút khỏi phiên: " + auctionId;
    }

    // Nhận thông báo thắng đấu giá 
    public String receiveWin(User user, String auctionId) {
        if (!checkBidder(user)) return "Không có quyền thực hiện.";

        getList(wonAuctions, user.getID()).add(auctionId);
        getList(activeBids,  user.getID()).remove(auctionId);

        return "Chúc mừng " + user.getUsername() + " thắng phiên: " + auctionId;
    }

    // Xem phiên đang tham gia 
    public String viewActiveBids(User user) {
        if (!checkBidder(user)) return "Không có quyền thực hiện.";

        List<String> active = getList(activeBids, user.getID());
        if (active.isEmpty()) return "Bạn chưa tham gia phiên nào.";

        StringBuilder sb = new StringBuilder("=== Phiên đang tham gia ===\n");
        active.forEach(a -> sb.append("  • ").append(a).append("\n"));
        return sb.toString();
    }

    //Xem lịch sử đấu giá 
    public String viewBidHistory(User user) {
        if (!checkBidder(user)) return "Không có quyền thực hiện.";

        List<String> history = getList(bidHistories, user.getID());
        if (history.isEmpty()) return "Chưa có lịch sử đấu giá.";

        StringBuilder sb = new StringBuilder("=== Lịch sử đấu giá ===\n");
        history.forEach(r -> sb.append("  • ").append(r).append("\n"));
        return sb.toString();
    }
}