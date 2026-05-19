package com.auction.client;

import com.auction.client.dto.Request;
import com.auction.client.dto.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

public class ServerConnection {

    private static final String HOST;
    private static final int    PORT;

    static {
        Properties props = new Properties();
        try (InputStream is = ServerConnection.class
                .getResourceAsStream("/config.properties")) {
            if (is != null) props.load(is);
        } catch (IOException ignored) {}
        HOST = props.getProperty("server.host", "localhost");
        PORT = Integer.parseInt(props.getProperty("server.port", "5000"));
    }

    private static ServerConnection instance;

    private Socket         socket;
    private PrintWriter    out;
    private BufferedReader in;
    private final Gson     gson = new Gson();
    private ServerConnection() {}

    //1 kết nối dùng chung cho toàn client”
    // nên tránh lặp lại nhiều socket và giữ trạng thái kết nối ổn định.
    //static nghĩa là gọi trực tiếp bằng ServerConnection.getInstance(), không cần đối tượng.
    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }
    
    public void connect() throws IOException {
        try {
            socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            // Nếu kết nối tới HOST thất bại (ví dụ firewall chặn IP nội bộ),
            // thử lại với localhost — dành cho máy chạy cả server lẫn client.
            if (HOST.equals("localhost") || HOST.equals("127.0.0.1")) throw e;
            socket = new Socket("localhost", PORT);
            System.out.println("[Client] Fallback sang localhost:" + PORT);
        }
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        System.out.println("[Client] Đã kết nối server " + socket.getInetAddress() + ":" + PORT);
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
            connect();
        }

        String json = gson.toJson(new Request(action, payload));//chuyển request thành json để gửi đi
        out.println(json);//gửi request đến server

        //chờ server phản hồi

        String raw = in.readLine();//đọc response từ server
        if (raw == null) throw new IOException("Server đóng kết nối bất ngờ.");
        try{ return gson.fromJson(raw, Response.class);
        } catch (JsonSyntaxException e) {
            throw new IOException("Response không hợp lệ: " + raw);
        }
    }
}
