package com.auction.common.network;

import com.auction.auction.controller.AuctionController;
import com.auction.auction.dao.AuctionDAOImpl;
import com.auction.auction.model.Auction;
import com.auction.bid.controller.BidController;
import com.auction.bid.dao.BidDAOSQLiteImpl;
import com.auction.bid.service.BidLockManager;
import com.auction.bid.service.BidService;
import com.auction.common.protocol.Request;
import com.auction.common.protocol.Response;
import com.auction.item.controller.ItemController;
import com.auction.item.model.Product.Art;
import com.auction.item.model.Product.Electronics;
import com.auction.item.model.Product.Item;
import com.auction.item.model.Product.Vehicle;
import com.auction.user.controller.UserController;
import com.auction.user.dao.UserDAO;
import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.Bidder;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.util.List;
import java.util.Map;
public class RequestRouter {

    private final UserController userController = new UserController();
    private final ItemController itemController = new ItemController();
    private final AuctionController auctionController = new AuctionController();
    private final BidController bidController = new BidController(
        new BidService(new AuctionDAOImpl(), new BidDAOSQLiteImpl(), new BidLockManager())
    );
    // Dùng trực tiếp để tra cứu User theo ID khi xử lý auction/bid
    private final UserDAO userDAO = new UserDAOSQLiteImpl();

    public Response route(Request request) {
        if (request == null || request.getAction() == null) {
            return Response.fall("Request không hợp lệ.");
        }

        try {
            switch (request.getAction().toUpperCase()) {

                // ===== USER =====
                case "REGISTER":
                    return handleRegister(request.getPayload());
                case "LOGIN":
                    return handleLogin(request.getPayload());

                //[XOÁ REGISTER Seller]

                // ===== ITEM =====
                case "CREATE_ITEM":
                    return handleCreateItem(request.getPayload());
                case "LIST_ITEMS":
                    return handleListItems();
                case "GET_ITEM":
                    return handleGetItem(request.getPayload());
                case "DELETE_ITEM":
                    return handleDeleteItem(request.getPayload());

                // ===== AUCTION =====
                case "CREATE_AUCTION":
                    return handleCreateAuction(request.getPayload());
                case "LIST_AUCTIONS":
                    return handleListAuctions();

                // ===== BID =====
                case "PLACE_BID":
                    return handlePlaceBid(request.getPayload());
                case "WITHDRAW_BID":
                    return handleWithdrawBid(request.getPayload());
                case "DEPOSIT":
                    return handleDeposit(request.getPayload());

                // ===== ADMIN =====
                case "LIST_USERS":
                    return handleListUsers();
                case "BAN_USER":
                    return handleBanUser(request.getPayload());
                case "APPROVE_ITEM":
                    return handleApproveItem(request.getPayload());

                default:
                    return Response.fall("Action không tồn tại: " + request.getAction());
            }
        } catch (Exception e) {
            System.err.println("[RequestRouter] Lỗi: " + e.getMessage());
            return Response.fall("Lỗi xử lý request: " + e.getMessage());
        }
    }

    // ========== PRIVATE HANDLERS ==========

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object payload) {
        if (!(payload instanceof Map)) throw new IllegalArgumentException("Payload phải là JSON object.");
        return (Map<String, Object>) payload;
    }

    private Response handleRegister(Object payload) {
        Map<String, Object> map = toMap(payload);
        String role = (String) map.get("role");
        if (role == null || role.isBlank()) role = "Bidder";
        boolean success = userController.createAccount(
            (String) map.get("id"),
            (String) map.get("username"),
            (String) map.get("password"),
            (String) map.get("confirmPassword"),
            role
        );
        return success ? Response.ok("Đăng ký thành công!", null) : Response.fall("Đăng ký thất bại.");
    }

    private Response handleLogin(Object payload) {
        Map<String, Object> map = toMap(payload);
        User user = userController.loginAccount(
            (String) map.get("username"),
            (String) map.get("password")
        );
        if (user == null) return Response.fall("Đăng nhập thất bại.");

        // Chỉ gửi các field cần thiết — không gửi passwordHash về client
        Map<String, Object> safeUser = new java.util.HashMap<>();
        safeUser.put("id",      user.getId());
        safeUser.put("username", user.getName());
        safeUser.put("balance",  user.getBalance());
        safeUser.put("role",     user instanceof com.auction.user.model.Admin  ? "ADMIN"
                               : user instanceof com.auction.user.model.Seller ? "SELLER"
                               : "BIDDER");
        return Response.ok("Đăng nhập thành công!", safeUser);
    }

    //[Xoá handleRegister]

    private Response handleCreateItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        String type     = (String) map.get("type");
        String id       = (String) map.get("id");
        String name     = (String) map.get("name");
        String des      = (String) map.get("des");
        double price    = ((Number) map.get("startPrice")).doubleValue();
        String category = (String) map.get("category");
        String sellerId = (String) map.get("sellerId");

        Item item;
        switch (type.toUpperCase()) {
            case "ART":
                item = new Art(id, name, des, price, category, sellerId,
                    (String) map.get("artist"),
                    (String) map.get("medium"));
                break;
            case "VEHICLE":
                item = new Vehicle(id, name, des, price, category, sellerId,
                    (String) map.get("make"),
                    (String) map.get("model"),
                    ((Number) map.get("year")).intValue());
                break;
            case "ELECTRONICS":
                item = new Electronics(id, name, des, price, category, sellerId,
                    (String) map.get("brand"),
                    ((Number) map.get("warrantyMonths")).intValue());
                break;
            default:
                return Response.fall("Loại sản phẩm không hợp lệ: " + type);
        }
        String result = itemController.createItem(item);
        return result.contains("thành công") ? Response.ok(result, item) : Response.fall(result);
    }

    private Response handleListItems() {
        return Response.ok("Danh sách sản phẩm", itemController.listAllItems());
    }

    private Response handleGetItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        Item item = itemController.getItem((String) map.get("id"));
        return item != null
            ? Response.ok("Sản phẩm", item)
            : Response.fall("Không tìm thấy sản phẩm.");
    }

    private Response handleDeleteItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        String msg = itemController.deleteItem((String) map.get("id"));
        return msg.contains("Đã xóa") ? Response.ok(msg, null) : Response.fall(msg);
    }
    private Response handleCreateAuction(Object payload) {
        Map<String, Object> map = toMap(payload);
        String itemId       = (String) map.get("itemId");
        String sellerId     = (String) map.get("sellerId");
        long duration       = ((Number) map.get("durationMillis")).longValue();

        Item item = itemController.getItem(itemId);
        if (item == null) return Response.fall("Không tìm thấy sản phẩm: " + itemId);
        if (!item.getApproved()) return Response.fall("Sản phẩm chưa được duyệt ");

        User user = userDAO.findById(sellerId);
        if (!(user instanceof Seller)) return Response.fall("Người dùng không phải Seller.");

        Auction auction = auctionController.createAuction(item, (Seller) user, duration);
        return auction != null
            ? Response.ok("Tạo phiên đấu giá thành công!", auction)
            : Response.fall("Tạo phiên đấu giá thất bại.");
    }

    private Response handleListAuctions() {
        return Response.ok("Danh sách phiên đấu giá", auctionController.getAllAuctions());
    }

    private Response handlePlaceBid(Object payload) {
        Map<String, Object> map = toMap(payload);
        String bidderId  = (String) map.get("bidderId");
        String auctionId = (String) map.get("auctionId");
        double amount    = ((Number) map.get("bidAmount")).doubleValue();

        User user = userDAO.findById(bidderId);
        if (!(user instanceof Bidder)) return Response.fall("Người dùng không phải Bidder.");

        String result = bidController.handlePlaceBid((Bidder) user, auctionId, amount);
        return result.contains("thành công")
            ? Response.ok(result, null)
            : Response.fall(result);
    }

    // Payload: {bidderId, auctionId}
    private Response handleWithdrawBid(Object payload) {
        Map<String, Object> map = toMap(payload);
        String bidderId  = (String) map.get("bidderId");
        String auctionId = (String) map.get("auctionId");

        User user = userDAO.findById(bidderId);
        if (!(user instanceof Bidder)) return Response.fall("Người dùng không phải Bidder.");

        String result = bidController.handleWithdraw((Bidder) user, auctionId);
        return result.startsWith("Đã rút")
            ? Response.ok(result, null)
            : Response.fall(result);
    }

    private Response handleListUsers() {
        List<User> users = userDAO.findAll();   // cần thêm findAll() vào UserDAO
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id",       u.getId());
            m.put("username", u.getName());
            m.put("banned",   u.isBanned());
            m.put("role",     u instanceof com.auction.user.model.Admin  ? "ADMIN"
                        : u instanceof com.auction.user.model.Seller ? "SELLER"
                        : "BIDDER");
        result.add(m);
    }
        return Response.ok("Danh sách người dùng", result);
    }

    private Response handleBanUser(Object payload) {
        Map<String, Object> map = toMap(payload);
        String userId = (String) map.get("userId");
        boolean banned = Boolean.parseBoolean(map.get("banned").toString());
        User user = userDAO.findById(userId);
        if (user == null) return Response.fall("Không tìm thấy user: " + userId);
        user.setBanned(banned);
        userDAO.update(user);
        return Response.ok((banned ? "Đã khóa: " : "Đã mở khóa: ") + user.getName(), null);
    }

    private Response handleApproveItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        String itemId    = (String) map.get("itemId");
        boolean approved = Boolean.parseBoolean(map.get("approved").toString());
        Item item = itemController.getItem(itemId);
        if (item == null) return Response.fall("Không tìm thấy sản phẩm: " + itemId);
        item.setApproved(approved);
        itemController.editItem(item);
        return Response.ok((approved ? "Đã duyệt: " : "Đã từ chối: ") + item.getName(), null);
    }

    private Response handleDeposit(Object payload) {
        Map<String, Object> map = toMap(payload);
        String bidderId = (String) map.get("bidderId");
        double amount   = ((Number) map.get("amount")).doubleValue();

        User user = userDAO.findById(bidderId);
        if (!(user instanceof Bidder))
            return Response.fall("Không tìm thấy Bidder: " + bidderId);

        String result = bidController.handleDeposit((Bidder) user, amount);
        if (result.contains("thành công")) {
            userDAO.update(user);
        }
        return result.contains("thành công")
            ? Response.ok(result, user.getBalance())
            : Response.fall(result);
    }

}
