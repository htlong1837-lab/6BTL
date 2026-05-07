package com.auction.common.network;

import java.io.*;
import java.net.Socket;

import com.auction.common.protocol.Request;
import com.auction.common.protocol.Response;
import com.auction.common.util.JsonHelper;

public class ClientHandler {

    private final Socket socket;
    private final RequestRouter router = new RequestRouter();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void handle() throws IOException {
        try (socket;
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                try {
                    Request request   = JsonHelper.fromJson(line, Request.class);
                    Response response = router.route(request);
                    out.println(JsonHelper.toJson(response));
                } catch (Exception e) {
                    out.println(JsonHelper.toJson(Response.fall("Lỗi server: " + e.getMessage())));
                }
            }
        }
    }
}