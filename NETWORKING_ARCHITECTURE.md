## NETWORKING ARCHITECTURE - CLIENT-SERVER COMMUNICATION

### 📋 TỔNG QUAN

Hệ thống gồm 3 lớp networking cho phép Client (JavaFX) giao tiếp với Server (Java Socket):

```
┌─ CLIENT (JavaFX App) ─────────────────────────────────────┐
│ (chưa implement - sẽ làm lần sau)                          │
│                                                              │
│ ServerConnection (Socket client kết nối tới server)        │
│ ↓ gửi JSON Request                                          │
└──────────────────────────────────────────────────────────┘
                            ↓ (socket stream)
┌─ SERVER (Java Socket) ────────────────────────────────────┐
│ SeverMain                                                   │
│  └─ ServerSocket:5000 (lắng nghe localhost:5000)           │
│      ↓ accept() → new Socket                               │
│      └─ spawn thread → ClientHandler                       │
│                                                              │
│ ClientHandler (xử lý 1 client connection)                   │
│  └─ BufferedReader in (đọc JSON từ socket)                 │
│  └─ PrintWriter out (ghi JSON vào socket)                  │
│      ↓ JsonHelper.fromJson() → Request object              │
│      └─ gọi RequestRouter.route(request)                   │
│                                                              │
│ RequestRouter (định tuyến request → controller)             │
│  └─ switch(request.getAction())                             │
│      ├─ REGISTER → UserController.createAccount()          │
│      ├─ LOGIN → UserController.loginAccount()              │
│      ├─ CREATE_ITEM → ItemController.createItem()          │
│      ├─ LIST_ITEMS → ItemController.listAllItems()         │
│      ├─ PLACE_BID → BidController.placeBid()               │
│      └─ ...                                                  │
│         ↓ gọi Service → DAO → SQLite                        │
│         → Response object                                   │
│      ↓ JsonHelper.toJson() → JSON string                    │
│      └─ ghi JSON trở lại client qua socket                 │
└──────────────────────────────────────────────────────────┘
```

---

### 🔧 THÀNH PHẦN 1: SeverMain.java

**Chức năng**: Là entry point của server - tạo ServerSocket lắng nghe client

**Key Points**:
- `ServerSocket(PORT=5000)` - Lắng nghe trên cổng 5000
- `serverSocket.accept()` - Blocking call chờ client kết nối
- Khi có client → tạo Socket mới → spawn thread từ ExecutorService
- `ExecutorService.newFixedThreadPool(10)` - Tối đa 10 client xử lý cùng lúc

**Thread Model**:
```
SeverMain (main thread)
  ├─ while(true) accept()  ← chờ client
  │    ↓ (client 1 kết nối)
  │    └─ spawn thread từ pool → ClientHandler.handle() (thread-1)
  │    ↓ (client 2 kết nối)
  │    └─ spawn thread từ pool → ClientHandler.handle() (thread-2)
  │    ... (tối đa 10 thread cùng xử lý)
  │
  └─ Thread 1: ClientHandler.handle()  ← đọc/ghi JSON
  └─ Thread 2: ClientHandler.handle()  ← đọc/ghi JSON
```

**Ưu điểm**: Thread pool ngăn chặn quá tải - nếu >10 client, client thứ 11 phải chờ queue

---

### 🔌 THÀNH PHẦN 2: ClientHandler.java

**Chức năng**: Xử lý 1 client connection từ lúc kết nối tới khi ngắt

**Key Points**:
- Nhận Socket từ SeverMain
- `PrintWriter out` - Ghi JSON Response
- `BufferedReader in` - Đọc JSON Request
- Lặp vô hạn: đọc request → xử lý → ghi response

**Flow xử lý 1 request**:
```
1. in.readLine() → "{"action":"LOGIN","payload":{...}}"
2. JsonHelper.fromJson() → Request object
3. router.route(request) → Response object
4. JsonHelper.toJson(response) → "{"success":true,"message":"...","data":{...}}"
5. out.println(jsonResponse) ← gửi lại client (autoFlush=true tự gửi ngay)
```

**Error Handling**:
- Nếu request invalid → return `Response.fall("error message")`
- Nếu controller throw exception → catch → return error response
- Nếu client đóng socket → readLine() trả null → thoát loop → cleanup

**Cleanup**:
```java
finally {
  out.close();
  in.close();
  clientSocket.close();  // ← QUAN TRỌNG: giải phóng socket resource
}
```

---

### 🛣️ THÀNH PHẦN 3: RequestRouter.java

**Chức năng**: Định tuyến request tới đúng Controller method

**Request Protocol**:
```json
{
  "action": "LOGIN",                    ← tên hành động
  "payload": {                          ← tham số cho hành động
    "username": "Tuan",
    "password": "Password1"
  }
}
```

**Response Protocol**:
```json
{
  "success": true,                      ← thành công/thất bại
  "message": "Đăng nhập thành công!",  ← thông báo
  "data": {                             ← kết quả (nếu có)
    "id": "user-001",
    "name": "Tuan",
    "email": "tuan@example.com",
    "role": "BIDDER"
  }
}
```

**Supported Actions**:

| Action | Payload | Response Data | Controller |
|--------|---------|---------------|-----------|
| REGISTER | {id, username, email, password, confirmPassword} | null | UserController.createAccount() |
| LOGIN | {username, password} | User object | UserController.loginAccount() |
| REGISTER_SELLER | {username, shopName} | null | UserController.registerAsSeller() |
| CREATE_ITEM | {item object} | null | ItemController.createItem() |
| LIST_ITEMS | {} | List\<Item\> | ItemController.listAllItems() |
| GET_ITEM | {id} | Item object | ItemController.getItem() |
| DELETE_ITEM | {id} | null | ItemController.deleteItem() |
| CREATE_AUCTION | {itemId, sellerId, durationMillis} | Auction | AuctionController.createAuction() |
| LIST_AUCTIONS | {} | List\<Auction\> | AuctionController.listAuctions() |
| PLACE_BID | {bidderId, auctionId, bidAmount} | string result | BidController.placeBid() |
| WITHDRAW_BID | {bidderId, auctionId} | string result | BidController.withdrawBid() |

**Error Handling**:
```java
try {
  switch(action) { ... }
} catch(Exception e) {
  return Response.fall("Lỗi: " + e.getMessage());
}
```

---

### 🔄 COMPLETE FLOW EXAMPLE: Đăng nhập

```
CLIENT sends:
→ {"action":"LOGIN","payload":{"username":"Tuan","password":"Password1"}}

SERVER:
1. SeverMain.accept() nhận Socket từ client
2. spawn ClientHandler thread
3. ClientHandler.handle() gọi in.readLine()
4. JsonHelper.fromJson() → Request(action=LOGIN, payload={...})
5. router.route(request)
6. RequestRouter:
   switch("LOGIN") → handleLogin()
   → userController.loginAccount("Tuan", "Password1")
   → userService.login(...)
   → userDAO.findByUsername(...)
   → SQLite query
   → return User object (nếu password đúng) / throw exception
7. Response(success=true, message="...", data=User)
8. JsonHelper.toJson(response)
9. out.println(jsonResponse)
10. Client nhận JSON response → parse → hiển thị trên UI

CLIENT receives:
← {"success":true,"message":"Đăng nhập thành công!","data":{"id":"user-001","name":"Tuan",...}}
```

---

### 🚨 IMPORTANT NOTES

**1. Thread Safety**:
- UserDAOSQLiteImpl/ItemDAOSQLiteImpl dùng PreparedStatement → thread-safe
- SQLite file-level locking tự động xử lý multiple threads

**2. Socket Handling**:
- Mỗi client có riêng socket → không share state giữa clients
- Socket tự động close khi client disconnect

**3. Scalability**:
- Hiện tại: Fixed thread pool 10 clients
- Nếu cần scale: dùng Netty, NIO, hoặc virtual threads (Java 21+)

**4. Protocol**:
- JSON qua socket text streams
- Mỗi request = 1 dòng JSON (terminates with \n)
- in.readLine() automatically handles \n

---

### 📝 NEXT STEPS (Client-side)

Để client có thể test networking, cần implement:

1. **ServerConnection.java** (Singleton)
   - Tạo Socket kết nối tới localhost:5000
   - Send request JSON
   - Receive response JSON

2. **Sửa Client Controllers** (LoginController, BiddingController, etc.)
   - Thay vì println + TODO
   - Gọi ServerConnection.send(request) → nhận response → update UI

---

### 🧪 Testing Networking

Server:
```bash
java -cp ... com.auction.common.network.SeverMain
# [Server] Đang lắng nghe trên port 5000
# [Server] Chờ client kết nối...
```

Client (tạm thời dùng telnet để test):
```bash
telnet localhost 5000
# Kết nối → gõ JSON request
{"action":"LOGIN","payload":{"username":"Tuan","password":"Password1"}}
# Server trả lại response JSON
{"success":true,"message":"...","data":{...}}
```
