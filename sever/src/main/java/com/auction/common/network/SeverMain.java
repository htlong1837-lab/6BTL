package com.auction.common.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [THÊM] SeverMain - Server chính, lắng nghe kết nối từ client
 * Chức năng:
 * - Tạo ServerSocket lắng nghe trên port 5000
 * - Chấp nhận kết nối từ client
 * - Spawn một thread (ClientHandler) cho mỗi client để xử lý độc lập
 * - ThreadPoolExecutor để quản lý nhiều client cùng lúc mà không cạn tài nguyên
 * Flow:
 * 1. ServerSocket.accept() chờ client kết nối (blocking)
 * 2. Khi có client kết nối → tạo Socket mới
 * 3. Gửi Socket vào ExecutorService → spawn thread chạy ClientHandler
 * 4. ClientHandler thread đọc Request JSON, xử lý, trả Response JSON
 */
public class SeverMain {

    //  Port server lắng nghe - client sẽ kết nối tới localhost:5000
    private static final int PORT = 5000;

    //  Thread pool: tối đa 10 client cùng lúc, queue unlimited client chờ
    // Nếu quá 10 client → xếp hàng chờ
    private static final int MAX_THREADS = 10;

    private static ServerSocket serverSocket;
    private static ExecutorService executorService;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(MAX_THREADS);

            System.out.println("[Server] Đang lắng nghe trên port " + PORT);
            System.out.println("[Server] Chờ client kết nối...");

            //  Infinite loop: lắng nghe incoming connections
            // Mỗi khi có client kết nối → accept() trả về Socket mới
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                System.out.println("[Server] Client mới kết nối: " + clientIP);

                // Gửi Socket vào thread pool
                // ExecutorService sẽ tạo thread từ pool (hoặc queue nếu hết thread)
                // Chạy ClientHandler.handle(socket) trong thread đó
                executorService.execute(() -> {
                    try {
                        new ClientHandler(clientSocket).handle();
                    } catch (IOException e) {
                        System.err.println("[ClientHandler] Lỗi: " + e.getMessage());
                    }
                });
            }

        } catch (IOException e) {
            System.err.println("[Server] Lỗi tạo ServerSocket: " + e.getMessage());
        } finally {
            //Cleanup khi server shutdown
            if (executorService != null) {
                executorService.shutdown();
            }
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("[Server] Lỗi đóng ServerSocket: " + e.getMessage());
            }
        }
    }
}