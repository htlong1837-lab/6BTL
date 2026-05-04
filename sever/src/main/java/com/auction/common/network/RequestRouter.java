package com.auction.common.network;

import com.auction.auction.controller.AuctionController;
import com.auction.auction.dao.AuctionDAOImpl;
import com.auction.auction.model.Auction;
import com.auction.bid.controller.BidController;
import com.auction.bid.dao.BidDAOImpl;
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

import java.util.Map;

/**
 * RequestRouter - Định tuyến request tới đúng Controller & Method
 *
 * Flow:
 * 1. ClientHandler parse JSON → Request object
 * 2. Gọi router.route(request)
 * 3. RequestRouter switch(request.getAction())
 * 4. Gọi controller method phù hợp
 * 5. Trả Response success/fail
 *
 * Request format từ client:
 * {
 *   "action": "LOGIN",
 *   "payload": {username: "...", password: "..."}
 * }
 */
public class RequestRouter {

    private final UserController userController = new UserController();
    private final ItemController itemController = new ItemController();
    private final AuctionController auctionController = new AuctionController();
    private final BidController bidController = new BidController(
        new BidService(new AuctionDAOImpl(), new BidDAOImpl(), new BidLockManager())
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
                case "REGISTER_SELLER":
                    return handleRegisterSeller(request.getPayload());

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
        boolean success = userController.createAccount(
            (String) map.get("id"),
            (String) map.get("username"),
            (String) map.get("email"),
            (String) map.get("password"),
            (String) map.get("confirmPassword")
        );
        return success ? Response.ok("Đăng ký thành công!", null) : Response.fall("Đăng ký thất bại.");
    }

    private Response handleLogin(Object payload) {
        Map<String, Object> map = toMap(payload);
        User user = userController.loginAccount(
            (String) map.get("username"),
            (String) map.get("password")
        );
        return user != null
            ? Response.ok("Đăng nhập thành công!", user)
            : Response.fall("Đăng nhập thất bại.");
    }

    private Response handleRegisterSeller(Object payload) {
        Map<String, Object> map = toMap(payload);
        boolean success = userController.registerAsSeller(
            (String) map.get("username"),
            (String) map.get("shopName")
        );
        return success
            ? Response.ok("Đăng ký Seller thành công!", null)
            : Response.fall("Đăng ký Seller thất bại.");
    }

    // Payload: {type, id, name, des, startPrice, category, sellerId, ...type-specific fields}
    // ART:         artist, medium
    // VEHICLE:     make, model, year
    // ELECTRONICS: brand, warrantyMonths
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
        return Response.ok(result, item);
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
        return msg.contains("success") ? Response.ok(msg, null) : Response.fall(msg);
    }

    // Payload: {itemId, sellerId, durationMillis}
    private Response handleCreateAuction(Object payload) {
        Map<String, Object> map = toMap(payload);
        String itemId       = (String) map.get("itemId");
        String sellerId     = (String) map.get("sellerId");
        long duration       = ((Number) map.get("durationMillis")).longValue();

        Item item = itemController.getItem(itemId);
        if (item == null) return Response.fall("Không tìm thấy sản phẩm: " + itemId);

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

    // Payload: {bidderId, auctionId, bidAmount}
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
}
