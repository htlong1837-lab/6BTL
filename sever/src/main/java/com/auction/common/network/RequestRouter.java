package com.auction.common.network;

import com.auction.auction.controller.AuctionController;
import com.auction.auction.dao.AuctionSQLiteDAOImpl;
import com.auction.auction.model.Auction;
import com.auction.bid.controller.BidController;
import com.auction.bid.dao.BidDAOSQLiteImpl;
import com.auction.bid.service.BidLockManager;
import com.auction.bid.service.BidService;
import com.auction.common.protocol.Request;
import com.auction.common.protocol.Response;
import com.auction.item.controller.ItemController;
import com.auction.item.model.Factory.ArtFactory;
import com.auction.item.model.Factory.ElectronicFactory;
import com.auction.item.model.Factory.ItemFactory;
import com.auction.item.model.Factory.VehicleFactory;
import com.auction.item.model.Product.Item;
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
        new BidService(new AuctionSQLiteDAOImpl(), new BidDAOSQLiteImpl(), new BidLockManager())
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
                case "DELETE_ITEM":
                    return handleDeleteItem(request.getPayload());

                // ===== AUCTION =====
                case "CREATE_AUCTION":
                    return handleCreateAuction(request.getPayload());
                case "LIST_AUCTIONS":
                    return handleListAuctions();
                case "DELETE_AUCTION":
                    return handleDeleteAuction(request.getPayload());

                // ===== BID =====
                case "PLACE_BID":
                    return handlePlaceBid(request.getPayload());
                case "DEPOSIT":
                    return handleDeposit(request.getPayload());

                // ===== USER INFO =====
                case "GET_BALANCE":
                    return handleGetBalance(request.getPayload());

                // ===== ADMIN =====
                case "LIST_USERS":
                    return handleListUsers();
                case "BAN_USER":
                    return handleBanUser(request.getPayload());

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
        String error = userController.createAccount(
            (String) map.get("id"),
            (String) map.get("username"),
            (String) map.get("password"),
            (String) map.get("confirmPassword"),
            role
        );
        return error == null ? Response.ok("Đăng ký thành công!", null) : Response.fall(error);
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

    // Trả về Response lỗi nếu user đã bị ban, null nếu không bị ban
    private Response checkBanned(User user) {
        if (user != null && user.isBanned()) {
            return Response.fall("Tài khoản đã bị khóa và không thể thực hiện thao tác này.");
        }
        return null;
    }

    private Response handleCreateItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        String type     = (String) map.get("type");
        String id       = (String) map.get("id");
        String name     = (String) map.get("name");
        String des      = (String) map.get("des");
        double price    = ((Number) map.get("startPrice")).doubleValue();
        String category = (String) map.get("category");
        String sellerId = (String) map.get("sellerId");

        User sellerUser = userDAO.findById(sellerId);
        Response banCheck = checkBanned(sellerUser);
        if (banCheck != null) return banCheck;

        ItemFactory factory;
        switch (type.toUpperCase()) {
            case "ART":
                factory = new ArtFactory(id, name, des, price, category, sellerId,
                    (String) map.get("artist"),
                    (String) map.get("medium"));
                break;
            case "VEHICLE":
                factory = new VehicleFactory(id, name, des, price, category, sellerId,
                    (String) map.get("make"),
                    (String) map.get("model"),
                    ((Number) map.get("year")).intValue());
                break;
            case "ELECTRONICS":
                factory = new ElectronicFactory(id, name, des, price, category, sellerId,
                    (String) map.get("brand"),
                    ((Number) map.get("warrantyMonths")).intValue());
                break;
            default:
                return Response.fall("Loại sản phẩm không hợp lệ: " + type);
        }
        Item item = factory.createItem();
        String result = itemController.createItem(item);
        return result.contains("thành công") ? Response.ok(result, item) : Response.fall(result);
    }

    private Response handleListItems() {
        return Response.ok("Danh sách sản phẩm", itemController.listAllItems());
    }

    private Response handleDeleteItem(Object payload) {
        Map<String, Object> map = toMap(payload);
        String msg = itemController.deleteItem((String) map.get("id"));
        return msg.contains("Đã xóa") ? Response.ok(msg, null) : Response.fall(msg);
    }
    private Response handleCreateAuction(Object payload) {
        try {
            Map<String, Object> map = toMap(payload);
            String itemId   = (String) map.get("itemId");
            String sellerId = (String) map.get("sellerId");
            long duration   = ((Number) map.get("durationMillis")).longValue();

            Item item = itemController.getItem(itemId);
            if (item == null) return Response.fall("Không tìm thấy sản phẩm: " + itemId);

            User user = userDAO.findById(sellerId);
            if (!(user instanceof Seller)) return Response.fall("Người dùng không phải Seller.");
            Response banCheck = checkBanned(user);
            if (banCheck != null) return banCheck;

            Auction auction = auctionController.createAuction(item, (Seller) user, duration);
            return Response.ok("Tạo phiên đấu giá thành công!", auction);
        } catch (com.auction.exception.AutionException.InvalidAuctionDataException e) {
            return Response.fall(e.getMessage());
        } catch (Exception e) {
            return Response.fall("Lỗi tạo phiên đấu giá: " + e.getMessage());
        }
    }

    private Response handleListAuctions() {
        return Response.ok("Danh sách phiên đấu giá", auctionController.getAllAuctions());
    }

    private Response handleDeleteAuction(Object payload) {
        try {
            Map<String, Object> map = toMap(payload);
            String auctionId = (String) map.get("auctionId");
            auctionController.deleteAuction(auctionId);
            return Response.ok("Đã xóa phiên đấu giá.", null);
        } catch (com.auction.exception.AutionException.AuctionNotFoundException e) {
            return Response.fall(e.getMessage());
        }
    }

    private Response handlePlaceBid(Object payload) {
        try {
            Map<String, Object> map = toMap(payload);
            String bidderId  = (String) map.get("bidderId");
            String auctionId = (String) map.get("auctionId");
            double amount    = ((Number) map.get("bidAmount")).doubleValue();

            User user = userDAO.findById(bidderId);
            if (!(user instanceof Bidder)) return Response.fall("Người dùng không phải Bidder.");
            Response banCheck = checkBanned(user);
            if (banCheck != null) return banCheck;

            String result = bidController.handlePlaceBid((Bidder) user, auctionId, amount);
            return result.contains("thành công")
                ? Response.ok(result, null)
                : Response.fall(result);
        } catch (Exception e) {
            return Response.fall("Lỗi đặt giá: " + e.getMessage());
        }
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
        if (user instanceof com.auction.user.model.Admin)
            return Response.fall("Không thể khóa tài khoản Admin.");
        user.setBanned(banned);
        userDAO.update(user);

        String msg = (banned ? "Đã khóa: " : "Đã mở khóa: ") + user.getName();

        // Nếu ban seller → hủy toàn bộ phiên đấu giá OPEN/RUNNING ngay lập tức
        if (banned && user instanceof Seller) {
            int cancelled = auctionController.cancelAuctionsBySeller(userId);
            if (cancelled > 0) {
                msg += ". Đã hủy " + cancelled + " phiên đấu giá.";
            }
        }

        return Response.ok(msg, null);
    }

    private Response handleGetBalance(Object payload) {
        Map<String, Object> map = toMap(payload);
        String userId = (String) map.get("userId");
        User user = userDAO.findById(userId);
        if (user == null) return Response.fall("Không tìm thấy user: " + userId);
        return Response.ok("Số dư hiện tại", user.getBalance());
    }

    private Response handleDeposit(Object payload) {
        Map<String, Object> map = toMap(payload);
        String bidderId = (String) map.get("bidderId");
        double amount   = ((Number) map.get("amount")).doubleValue();

        User user = userDAO.findById(bidderId);
        if (!(user instanceof Bidder))
            return Response.fall("Không tìm thấy Bidder: " + bidderId);
        Response banCheck = checkBanned(user);
        if (banCheck != null) return banCheck;

        String result = bidController.handleDeposit((Bidder) user, amount);
        if (result.contains("thành công")) {
            userDAO.update(user);
        }
        return result.contains("thành công")
            ? Response.ok(result, user.getBalance())
            : Response.fall(result);
    }

}
