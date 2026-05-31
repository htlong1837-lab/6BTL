## NETWORKING ARCHITECTURE - CLIENT-SERVER COMMUNICATION

### 📋 TỔNG QUAN

Hệ thống gồm 3 lớp networking cho phép Client (JavaFX) giao tiếp với Server (Java Socket):

```
┌─ CLIENT (JavaFX App)─────────────────────────────────────┐
│                                                          │
│ ServerConnection (Singleton - kết nối tới server)        │
│  └─ connect() → Socket tới localhost:5000                │
│  └─ send(action, payload) → gửi JSON, nhận JSON          │
│ ↓ gửi JSON Request                                       │
└──────────────────────────────────────────────────────────┘
                            ↓ (socket stream)
┌─ SERVER (Java Socket)────────────────────────────────────┐
│ SeverMain                                                │
│  └─ ServerSocket:5000 (lắng nghe localhost:5000)         │
│      ↓ accept() → new Socket                             │
│      └─ new Thread() → ClientHandler (1 thread/client)   │
│                                                          │
│ ClientHandler (xử lý 1 client connection)                │
│  └─ BufferedReader in (đọc JSON từ socket)               │
│  └─ PrintWriter out (ghi JSON vào socket)                │
│      ↓ JsonHelper.fromJson() → Request object            │
│      └─ gọi RequestRouter.route(request)                 │
│                                                          │
│ RequestRouter (định tuyến request → controller)          │
│  └─ switch(request.getAction())                          │
│      ├─ REGISTER → UserController.createAccount()        │
│      ├─ LOGIN → UserController.loginAccount()            │
│      ├─ CREATE_ITEM → ItemController.createItem()        │
│      ├─ LIST_ITEMS → ItemController.listAllItems()       │
│      ├─ PLACE_BID → BidController.placeBid()             │
│      └─ ...                                              │
│         ↓ gọi Service → DAO → SQLite                     │
│         → Response object                                │
│      ↓ JsonHelper.toJson() → JSON string                 │
│      └─ ghi JSON trở lại client qua socket               │
└──────────────────────────────────────────────────────────┘
```

---

### 🔧 THÀNH PHẦN 1: SeverMain.java

**Chức năng**: Là entry point của server - tạo ServerSocket lắng nghe client

**Key Points**:
- `ServerSocket(PORT=5000)` - Lắng nghe trên cổng 5000 (hoặc đọc từ env `PORT`)
- `serverSocket.accept()` - Blocking call chờ client kết nối
- Khi có client → tạo Socket mới → spawn `new Thread()` riêng cho mỗi client

**Thread Model**:
```
SeverMain (main thread)
  ├─ while(true) accept()  ← chờ client
  │    ↓ (client 1 kết nối)
  │    └─ new Thread() → ClientHandler.handle() (thread-1)
  │    ↓ (client 2 kết nối)
  │    └─ new Thread() → ClientHandler.handle() (thread-2)
  │    ... (không giới hạn số thread)
```

**Lưu ý**: Mỗi client được cấp 1 thread riêng, không dùng thread pool — số thread tăng theo số client kết nối đồng thời.

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

**Cleanup** (dùng try-with-resources):
```java
try (socket;
     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
     BufferedReader in = new BufferedReader(...)) {
    // xử lý
}
// socket, out, in tự đóng khi ra khỏi try block
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
    "username": "Tuan",
    "balance": 0.0,
    "role": "BIDDER"
  }
}
```

**Supported Actions**:

| Action | Payload | Response Data | Controller |
|--------|---------|---------------|-----------|
| REGISTER | {id, username, password, confirmPassword, role} | null | UserController.createAccount() |
| LOGIN | {username, password} | {id, username, balance, role} | UserController.loginAccount() |
| CREATE_ITEM | {type, id, name, des, startPrice, category, sellerId, ...type-fields} | Item object | ItemController.createItem() |
| LIST_ITEMS | {} | List\<Item\> | ItemController.listAllItems() |
| DELETE_ITEM | {id} | null | ItemController.deleteItem() |
| CREATE_AUCTION | {itemId, sellerId, durationMillis} | Auction | AuctionController.createAuction() |
| LIST_AUCTIONS | {} | List\<Auction\> | AuctionController.getAllAuctions() |
| DELETE_AUCTION | {auctionId} | null | AuctionController.deleteAuction() |
| PLACE_BID | {bidderId, auctionId, bidAmount} | null | BidController.handlePlaceBid() |
| DEPOSIT | {bidderId, amount} | balance (số dư mới) | BidController.handleDeposit() |
| GET_BALANCE | {userId} | balance (double) | userDAO.findById() |
| LIST_USERS | {} | List\<{id, username, banned, role}\> | userDAO.findAll() |
| BAN_USER | {userId, banned} | null | userDAO.update() |

**Error Handling**:
```java
try {
  switch(action) { ... }
} catch(Exception e) {
  return Response.fall("Lỗi xử lý request: " + e.getMessage());
}
```

---

### 🔌 THÀNH PHẦN 4: ServerConnection.java (Client-side)

**Chức năng**: Singleton quản lý kết nối từ client tới server

**Key Points**:
- `getInstance()` - Lấy instance duy nhất (Singleton)
- `connect()` - Tạo Socket kết nối tới server (timeout 5s), đọc host/port từ `config.properties`
- `send(action, payload)` - Gửi JSON request, đọc JSON response (timeout 10s)
- `isConnected()` / `disconnect()` - Kiểm tra và đóng kết nối

**Config** (`config.properties`):
```properties
server.host=localhost
server.port=5000
```

---

### 🔄 COMPLETE FLOW EXAMPLE: Đăng nhập

```
CLIENT sends:
→ {"action":"LOGIN","payload":{"username":"Tuan","password":"Password1"}}

SERVER:
1. SeverMain.accept() nhận Socket từ client
2. new Thread() → ClientHandler
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
7. Tạo safeUser map (không gửi passwordHash về client)
8. Response(success=true, message="...", data=safeUser)
9. JsonHelper.toJson(response)
10. out.println(jsonResponse)

CLIENT receives:
← {"success":true,"message":"Đăng nhập thành công!","data":{"id":"user-001","username":"Tuan","balance":0.0,"role":"BIDDER"}}
```

---

### 🚨 IMPORTANT NOTES

**1. Thread Safety**:
- UserDAOSQLiteImpl/ItemDAOSQLiteImpl dùng PreparedStatement → thread-safe
- SQLite file-level locking tự động xử lý multiple threads

**2. Socket Handling**:
- Mỗi client có riêng socket → không share state giữa clients
- try-with-resources đảm bảo socket luôn được đóng khi client disconnect

**3. Scalability**:
- Hiện tại: 1 thread/client, không giới hạn
- Nếu cần scale: dùng ExecutorService fixed pool, Netty, NIO, hoặc virtual threads (Java 21+)

**4. Protocol**:
- JSON qua socket text streams
- Mỗi request = 1 dòng JSON (terminates with \n)
- in.readLine() automatically handles \n

**5. Ban Check**:
- Các action CREATE_ITEM, CREATE_AUCTION, PLACE_BID, DEPOSIT đều kiểm tra `user.isBanned()` trước khi xử lý
- Ban Seller → tự động hủy toàn bộ phiên đấu giá OPEN/RUNNING

---

### 🧪 Testing Networking

Server:
```bash
java -cp ... com.auction.common.network.SeverMain
# [Server] Đang lắng nghe trên port 5000
# [Server] Client kết nối: 127.0.0.1   ← in khi có client connect
```

Client (tạm thời dùng telnet để test):
```bash
telnet localhost 5000
# Kết nối → gõ JSON request
{"action":"LOGIN","payload":{"username":"Tuan","password":"Password1"}}
# Server trả lại response JSON
{"success":true,"message":"Đăng nhập thành công!","data":{"id":"user-001","username":"Tuan","balance":0.0,"role":"BIDDER"}}
```
