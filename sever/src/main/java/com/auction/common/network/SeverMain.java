package com.auction.common.network;

import java.io.*;
import java.net.*;

public class SeverMain {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("[Server] Đang lắng nghe trên port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Client kết nối: " + clientSocket.getInetAddress().getHostAddress());

                // Mỗi client được xử lý trên 1 thread riêng
                new Thread(() -> {
                    try {
                        new ClientHandler(clientSocket).handle();
                    } catch (IOException e) {
                        System.err.println("[ClientHandler] Lỗi: " + e.getMessage());
                    }
                }).start();
            }

        } catch (IOException e) {
            System.err.println("[Server] Lỗi: " + e.getMessage());
        }
    }
}