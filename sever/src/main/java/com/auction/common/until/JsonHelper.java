package com.auction.common.until;

// [THÊM] Toàn bộ file này là mới - wrapper cho Gson để serialize/deserialize JSON
// Cần thiết để ClientHandler đọc Request và ghi Response qua socket dưới dạng JSON

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHelper {

    // [THÊM] Dùng GsonBuilder với serializeNulls để null field vẫn xuất hiện trong JSON
    // Giúp client biết field nào là null thay vì bị bỏ qua hoàn toàn
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();

    // [THÊM] Chuyển object Java → chuỗi JSON để gửi qua socket
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    // [THÊM] Chuyển chuỗi JSON nhận từ socket → object Java theo class chỉ định
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
