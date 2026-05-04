package com.auction.common.network;

import com.auction.auction.controller.AuctionController;
import com.auction.bid.controller.BidController;
import com.auction.common.protocol.Request;
import com.auction.common.protocol.Response;
import com.auction.item.controller.ItemController;
import com.auction.item.model.Product.Item;
import com.auction.user.controller.UserController;
import com.auction.user.model.User;

/**
 * [THÊM] RequestRouter - Định tuyến request tới đúng Controller & Method
 *
 * Chức năng:
 * - Nhận Request object từ ClientHandler
 * - Dựa vào request.action (LOGIN, REGISTER, CREATE_AUCTION, PLACE_BID, v.v.)
 * - Gọi đúng Controller method
 * - Bắt exception từ Service layer → tạo Response lỗi
 * - Trả về Response (success hoặc fail) cho ClientHandler
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
 *
 * Response format trả lại client:
 * {
 *   "success": true/false,
 *   "message": "...",
 *   "data": {...}
 * }
 */
public class RequestRouter {

    // [THÊM] Các controller để xử lý business logic
    private final UserController userController = new UserController();
    private final ItemController itemController = new ItemController();
    private final AuctionController auctionController = new AuctionController();
    private final BidController bidController = new BidController();

    /**
     * [THÊM] Định tuyến request tới controller phù hợp
     *
     * @param request - Request object chứa action và payload
     * @return Response - Kết quả của controller (success/fail)
     */
    public Response route(Request request) {
        if (request == null || request.getAction() == null) {
            return Response.fall("Request không hợp lệ.");
        }

        try {
            // [THÊM] Switch dựa trên action type để gọi controller method tương ứng
            switch (request.getAction().toUpperCase()) {

                // ===== USER MANAGEMENT =====
                case "REGISTER":
                    // [THÊM] Payload: {id, username, email, password, confirmPassword}
                    return handleRegister(request.getPayload());

                case "LOGIN":
                    // [THÊM] Payload: {username, password}
                    return handleLogin(request.getPayload());

                case "REGISTER_SELLER":
                    // [THÊM] Payload: {username, shopName}
                    return handleRegisterSeller(request.getPayload());

                // ===== PRODUCT MANAGEMENT =====
                case "CREATE_ITEM":
                    // [THÊM] Payload: {item object}
                    return handleCreateItem(request.getPayload());

                case "LIST_ITEMS":
                    // [THÊM] Lấy tất cả sản phẩm
                    return handleListItems();

                case "GET_ITEM":
                    // [THÊM] Payload: {id}
                    return handleGetItem(request.getPayload());

                case "DELETE_ITEM":
                    // [THÊM] Payload: {id}
                    return handleDeleteItem(request.getPayload());

                // ===== AUCTION MANAGEMENT =====
                case "CREATE_AUCTION":
                    // [THÊM] Payload: {itemId, sellerId, durationMillis}
                    return handleCreateAuction(request.getPayload());

                case "LIST_AUCTIONS":
                    // [THÊM] Lấy tất cả phiên đấu giá
                    return handleListAuctions();

                // ===== BID MANAGEMENT =====
                case "PLACE_BID":
                    // [THÊM] Payload: {bidderId, auctionId, bidAmount}
                    return handlePlaceBid(request.getPayload());

                case "WITHDRAW_BID":
                    // [THÊM] Payload: {bidderId, auctionId}
                    return handleWithdrawBid(request.getPayload());

                default:
                    return Response.fall("Action không tồn tại: " + request.getAction());
            }

        } catch (Exception e) {
            // [THÊM] Bắt tất cả exceptions từ controller layer
            System.err.println("[RequestRouter] Lỗi: " + e.getMessage());
            return Response.fall("Lỗi xử lý request: " + e.getMessage());
        }
    }

    // ========== PRIVATE HANDLERS ==========

    // [THÊM] Các handler method lấy data từ payload, gọi controller, format response

    private Response handleRegister(Object payload) {
        // Payload: {id, username, email, password, confirmPassword}
        if (!(payload instanceof java.util.Map)) {
            return Response.fall("Payload phải là JSON object.");
        }
        java.util.Map<String, Object> map = (java.util.Map) payload;
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
        java.util.Map<String, Object> map = (java.util.Map) payload;
        User user = userController.loginAccount((String) map.get("username"), (String) map.get("password"));
        return user != null ? Response.ok("Đăng nhập thành công!", user) : Response.fall("Đăng nhập thất bại.");
    }

    private Response handleRegisterSeller(Object payload) {
        java.util.Map<String, Object> map = (java.util.Map) payload;
        boolean success = userController.registerAsSeller((String) map.get("username"), (String) map.get("shopName"));
        return success ? Response.ok("Đăng ký Seller thành công!", null) : Response.fall("Đăng ký Seller thất bại.");
    }

    private Response handleCreateItem(Object payload) {
        // Payload: Item object - cần parse từ Map tùy loại (Art/Vehicle/Electronics)
        // Đơn giản hoá: gửi về lỗi nếu không thể parse
        return Response.fall("TODO: Implement item creation");
    }

    private Response handleListItems() {
        return Response.ok("Danh sách sản phẩm", itemController.listAllItems());
    }

    private Response handleGetItem(Object payload) {
        java.util.Map<String, Object> map = (java.util.Map) payload;
        Item item = itemController.getItem((String) map.get("id"));
        return item != null ? Response.ok("Sản phẩm", item) : Response.fall("Không tìm thấy sản phẩm.");
    }

    private Response handleDeleteItem(Object payload) {
        java.util.Map<String, Object> map = (java.util.Map) payload;
        String msg = itemController.deleteItem((String) map.get("id"));
        return msg.contains("success") ? Response.ok(msg, null) : Response.fall(msg);
    }

    private Response handleCreateAuction(Object payload) {
        // TODO: Implement
        return Response.fall("TODO: Implement auction creation");
    }

    private Response handleListAuctions() {
        // TODO: Implement
        return Response.fall("TODO: Implement list auctions");
    }

    private Response handlePlaceBid(Object payload) {
        // TODO: Implement
        return Response.fall("TODO: Implement place bid");
    }

    private Response handleWithdrawBid(Object payload) {
        // TODO: Implement
        return Response.fall("TODO: Implement withdraw bid");
    }
}