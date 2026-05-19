# Hệ thống Đấu Giá Trực Tuyến (Online Auction System)

Ứng dụng đấu giá trực tuyến client–server viết bằng Java 21, giao diện JavaFX, giao tiếp qua Socket TCP, lưu trữ dữ liệu bằng SQLite.

---

## Mục lục

1. [Mô tả dự án](#1-mô-tả-dự-án)
2. [Công nghệ sử dụng](#2-công-nghệ-sử-dụng)
3. [Kiến trúc hệ thống](#3-kiến-trúc-hệ-thống)
4. [Design Patterns](#4-design-patterns)
5. [Tính năng](#5-tính-năng)
6. [Cấu trúc thư mục](#6-cấu-trúc-thư-mục)
7. [Hướng dẫn cài đặt & chạy](#7-hướng-dẫn-cài-đặt--chạy)
8. [Giao thức Client–Server](#8-giao-thức-clientserver)

---

## 1. Mô tả dự án

Hệ thống cho phép nhiều người dùng đồng thời tham gia đấu giá sản phẩm qua mạng LAN hoặc Internet (Railway TCP Proxy). Hệ thống gồm hai module độc lập:

- **Server** (`sever/`): Xử lý nghiệp vụ, quản lý phiên đấu giá, lưu dữ liệu SQLite, chạy nền liên tục.
- **Client** (`Client/`): Giao diện JavaFX cho người dùng đăng nhập, đặt giá, quản lý sản phẩm.

---

## 2. Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| Giao diện | JavaFX 21.0.5 (FXML) |
| Giao tiếp mạng | Java Socket TCP |
| Serialize dữ liệu | Gson 2.10.1 (JSON) |
| Cơ sở dữ liệu | SQLite 3.45.1.0 |
| Build tool | Apache Maven |
| Unit test | JUnit 5 + Mockito |
| Đóng gói | maven-shade-plugin (fat JAR) |
| Deploy server | Railway.com (TCP Proxy) |

---

## 3. Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (JavaFX)                      │
│  LoginController  SellerController  BiddingController   │
│         │                │                │             │
│      SessionManager  ServerConnection  (Singleton)      │
└──────────────────────────┬──────────────────────────────┘
                           │  Socket TCP (JSON)
                           │  port 5000 / Railway
┌──────────────────────────┴──────────────────────────────┐
│                      SERVER                             │
│  SeverMain → ClientHandler(thread) → RequestRouter      │
│       ↓                                                 │
│  UserController  AuctionController  BidController       │
│       ↓                                                 │
│  UserService  AuctionService  BidService                │
│       ↓                                                 │
│  DAO (SQLite)  ←→  auction.db                           │
│       ↓                                                 │
│  AuctionEventManager (Singleton + Observer)             │
└─────────────────────────────────────────────────────────┘
```

### Luồng xử lý request

1. Client gửi JSON `{ "action": "LOGIN", "payload": {...} }` qua Socket.
2. `ClientHandler` nhận, `RequestRouter` điều hướng đến Controller tương ứng.
3. Controller gọi Service → DAO → SQLite.
4. Server trả về JSON `{ "success": true/false, "data": {...} }`.
5. Client cập nhật giao diện trên JavaFX thread (`Platform.runLater`).

---

## 4. Design Patterns

### 4.1 Singleton
Đảm bảo chỉ tồn tại một instance duy nhất trong suốt vòng đời ứng dụng.

| Class | Mô tả |
|---|---|
| `AuctionEventManager` (Server) | Quản lý event Observer toàn cục |
| `SessionManager` (Client) | Lưu thông tin người dùng đang đăng nhập |
| `ServerConnection` (Client) | Kết nối Socket duy nhất đến server |

### 4.2 Observer
Server broadcast sự kiện phiên đấu giá (mở/đóng/có bid mới) đến tất cả listener đang đăng ký.

```
AuctionEventManager.notify(EventType, Auction)
        ↓
  List<AuctionListener>.forEach(l -> l.onEvent(...))
```

### 4.3 Factory Method
Tạo đối tượng `Item` theo danh mục mà không cần biết class cụ thể.

```
ItemFactory (interface)
├── ArtFactory        → Art item
├── ElectronicFactory → Electronics item
└── VehicleFactory    → Vehicle item
```

### 4.4 Builder
Xây dựng đối tượng `Item` phức tạp theo từng bước, tránh constructor quá nhiều tham số.

```
ItemBuilder (interface)
├── ArtBuilder
├── ElectronicsBuilder
└── VehicleBuilder
```

### 4.5 DAO (Data Access Object)
Tách biệt logic nghiệp vụ khỏi tầng truy cập dữ liệu.

```
UserDAO (interface) ← UserDAOSQLiteImpl
ItemDAO (interface) ← ItemDAOSQLiteImpl
BidDAO  (interface) ← BidDAOSQLiteImpl
AuctionDAO (interface) ← AuctionDAOSQLiteImpl
```

### 4.6 MVC (Model–View–Controller)
- **Model**: `User`, `Item`, `Auction`, `BidTransaction`
- **View**: FXML files (`LoginView.fxml`, `SellerViewfinal.fxml`, …)
- **Controller**: `LoginController`, `SellerController`, `BiddingController`, …

---

## 5. Tính năng

### 5.1 Quản lý người dùng

| Vai trò | Quyền hạn |
|---|---|
| **Admin** | Khóa/mở khóa tài khoản người dùng |
| **Seller** | Đăng sản phẩm, tạo phiên đấu giá, xem doanh thu |
| **Bidder** | Nạp tiền, tham gia đấu giá, đặt giá |

### 5.2 Vòng đời phiên đấu giá

```
OPEN → RUNNING → FINISHED
```

- **OPEN**: Phiên đã tạo, chưa bắt đầu.
- **RUNNING**: Đang trong thời gian đấu giá.
- **FINISHED**: Kết thúc — tự động chuyển tiền từ người thắng sang người bán.

**Anti-sniping**: Nếu có bid đặt trong vòng 20 giây cuối, thời gian phiên tự động gia hạn thêm 60 giây.

### 5.3 Đặt giá (Bidding)

- Kiểm tra số dư đủ trước khi đặt.
- Thread-safe: dùng `ReentrantLock` per auction (`BidLockManager`).
- Giá mới phải cao hơn giá hiện tại.
- Client tự động làm mới kết quả mỗi 3 giây (poll timer).

### 5.4 Ví điện tử

- Bidder: nạp tiền (`deposit`), số dư tự động trừ khi thắng đấu giá.
- Seller: doanh thu cộng dồn khi phiên kết thúc.

### 5.5 Danh mục sản phẩm

- Nghệ thuật (Art)
- Điện tử (Electronics)
- Phương tiện (Vehicle)

---

## 6. Cấu trúc thư mục

```
6BTL/
├── sever/                          ← Module Server
│   ├── src/main/java/com/auction/
│   │   ├── auction/                ← Auction model, service, DAO
│   │   ├── bid/                    ← Bid model, service, DAO
│   │   ├── item/                   ← Item model, factory, builder, DAO
│   │   ├── user/                   ← User model, service, DAO (Admin/Seller/Bidder)
│   │   ├── common/
│   │   │   ├── network/            ← SeverMain, ClientHandler, RequestRouter
│   │   │   ├── observer/           ← AuctionEventManager, AuctionListener
│   │   │   ├── protocol/           ← Request, Response (JSON)
│   │   │   └── util/               ← JsonHelper, DatabaseConnection, PasswordUtil
│   │   └── exception/              ← Custom exceptions
│   └── pom.xml
│
├── Client/                         ← Module Client (JavaFX)
│   ├── src/main/java/com/auction/
│   │   ├── client/                 ← App, Launcher, ServerConnection, SessionManager
│   │   └── controller/             ← JavaFX Controllers
│   ├── src/main/resources/
│   │   ├── com/client/view/        ← FXML files
│   │   └── config.properties       ← server.host / server.port
│   └── pom.xml
│
├── Dockerfile                      ← Docker build cho Railway deploy
├── auction.db                      ← SQLite database file
└── README.md
```

---

## 7. Hướng dẫn cài đặt & chạy

### Yêu cầu

- JDK 21+
- Apache Maven 3.8+

### Bước 1: Build Server

```bash
cd sever
mvn clean package -DskipTests
```

Output: `sever/target/server.jar`

### Bước 2: Build Client

```bash
cd Client
mvn clean package -DskipTests
```

Output: `Client/target/client.jar`

### Bước 3: Chạy Client

```bash
java -jar Client/target/client.jar
```

Server đang chạy trên Railway.com — client tự kết nối, không cần chạy server local.

### Chạy bằng Maven (không cần build JAR)

```bash
cd Client && mvn javafx:run
```

---

## 8. Giao thức Client–Server

### Định dạng Request (Client → Server)

```json
{
  "action": "LOGIN",
  "payload": {
    "username": "user1",
    "password": "Pass123"
  }
}
```

### Định dạng Response (Server → Client)

```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "id": "U001",
    "username": "user1",
    "role": "Bidder",
    "balance": 500000.0
  }
}
```

### Danh sách Action

| Action | Mô tả |
|---|---|
| `LOGIN` | Đăng nhập |
| `REGISTER` | Đăng ký tài khoản mới |
| `LIST_ITEMS` | Lấy danh sách sản phẩm |
| `CREATE_ITEM` | Tạo sản phẩm mới (Seller) |
| `CREATE_AUCTION` | Tạo phiên đấu giá mới |
| `LIST_AUCTIONS` | Lấy danh sách phiên đấu giá |
| `PLACE_BID` | Đặt giá trong phiên đấu giá |
| `GET_AUCTION` | Lấy thông tin chi tiết phiên |
| `DEPOSIT` | Nạp tiền vào ví (Bidder) |
| `GET_BALANCE` | Lấy số dư hiện tại |
| `BAN_USER` | Khóa/mở khóa tài khoản (Admin) |
| `LIST_USERS` | Danh sách người dùng (Admin) |
