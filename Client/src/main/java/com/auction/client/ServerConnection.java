package com.auction.client;

import com.auction.client.dto.Request;
import com.auction.client.dto.Response;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    private static ServerConnection instance;

    private Socket         socket;
    private PrintWriter    out;
    private BufferedReader in;
    private final Gson     gson = new Gson();

    private ServerConnection() {}

    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }
    
    public void connect() throws IOException {
        socket = new Socket(HOST, PORT);
        out    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        in     = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        System.out.println("[Client] Đã kết nối server " + HOST + ":" + PORT);
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void disconnect() {
        try { if (socket != null) socket.close(); }
        catch (IOException ignored) {}
        System.out.println("[Client] Đã ngắt kết nối.");
    }
    public Response send(String action, Map<String, Object> payload) throws IOException {
        if (!isConnected()) {
            throw new IOException("Chưa kết nối server. Gọi connect() trước.");
        }

        String json = gson.toJson(new Request(action, payload));
        out.println(json);

        String raw = in.readLine();
        if (raw == null) throw new IOException("Server đóng kết nối bất ngờ.");
        return gson.fromJson(raw, Response.class);
    }
}
