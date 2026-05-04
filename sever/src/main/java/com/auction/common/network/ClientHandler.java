package com.auction.common.network;

import java.io.*;
import java.net.Socket;

import com.auction.common.protocol.Request;
import com.auction.common.protocol.Response;
import com.auction.common.until.JsonHelper;

/**
 * - Đọc JSON Request từ socket input stream
 * - Gửi JSON Request tới RequestRouter để lấy kết quả
 * - Ghi JSON Response trở lại socket output stream
 * - Đóng socket khi client ngắt kết nối
 *
 * Flow:
 * 1. SeverMain spawn thread mới chạy handle()
 * 2. handle() lặp vô hạn: đọc Request JSON → parse thành Request object
 * 3. Gửi Request tới RequestRouter.route() → nhận lại Response
 * 4. Viết Response JSON trở lại client qua socket
 * 5. Nếu client đóng kết nối → socket.isClosed() = true → thoát loop
 */
public class ClientHandler {

    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private final RequestRouter router = new RequestRouter();

    public ClientHandler(Socket socket) {
        //mỗi instance của cái này xử lý 1 client
        this.clientSocket = socket;
    }

    /**
     * [THÊM] Xử lý request/response từ client
     * - Khởi tạo input/output streams
     * - Lặp đọc JSON requests từ client
     * - Gửi JSON responses trở lại
     */
    public void handle() throws IOException {
        try {
            // [THÊM] Khởi tạo streams để đọc/ghi dữ liệu từ/tới client socket
            out = new PrintWriter(clientSocket.getOutputStream(), true); // true = autoFlush
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String jsonRequest;
            // [THÊM] Lặp vô hạn đọc từ client cho tới khi socket đóng (readLine() trả về null)
            while ((jsonRequest = in.readLine()) != null) {
                try {
                    System.out.println("[ClientHandler] Nhận: " + jsonRequest);

                    // [THÊM] Parse JSON string → Request object dùng Gson
                    Request request = JsonHelper.fromJson(jsonRequest, Request.class);

                    // [THÊM] Gửi Request tới Router để xử lý
                    // Router sẽ lựa chọn đúng Controller & Method dựa trên request.action
                    Response response = router.route(request);

                    // [THÊM] Chuyển Response object thành JSON string
                    String jsonResponse = JsonHelper.toJson(response);
                    System.out.println("[ClientHandler] Gửi: " + jsonResponse);

                    // [THÊM] Ghi JSON response trở lại client qua socket
                    // out là PrintWriter với autoFlush=true, nên println() tự động flush
                    out.println(jsonResponse);

                } catch (Exception e) {
                    // [THÊM] Nếu lỗi xử lý request → gửi lại error response cho client
                    System.err.println("[ClientHandler] Lỗi xử lý request: " + e.getMessage());
                    Response errorResponse = Response.fall("Lỗi server: " + e.getMessage());
                    out.println(JsonHelper.toJson(errorResponse));
                }
            }

            System.out.println("[ClientHandler] Client đóng kết nối.");

        } finally {
            // [THÊM] Cleanup resources khi client ngắt kết nối
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}