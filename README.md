# Hệ thống Đấu Giá Trực Tuyến (Online Auction System)

Ứng dụng đấu giá trực tuyến client–server viết bằng Java 21, giao diện JavaFX, giao tiếp qua Socket TCP, lưu trữ dữ liệu bằng SQLite.

---

## Mục lục

1. [Mô tả dự án](#1-mô-tả-dự-án)
2. [Công nghệ sử dụng](#2-công-nghệ-sử-dụng)
3. [Kiến trúc hệ thống](#3-kiến-trúc-hệ-thống)
4. [Design Patterns](#4-design-patterns)
5. [Chức năng đã hoàn thành](#5-chức-năng-đã-hoàn-thành)
6. [Cấu trúc thư mục](#6-cấu-trúc-thư-mục)
7. [Vị trí file JAR](#7-vị-trí-file-jar)
8. [Hướng dẫn cài đặt & chạy](#8-hướng-dẫn-cài-đặt--chạy)
9. [Giao thức Client–Server](#9-giao-thức-clientserver)
10. [Báo cáo & Demo](#10-báo-cáo--demo)

---

## 1. Mô tả dự án

Hệ thống cho phép nhiều người dùng đồng thời tham gia đấu giá sản phẩm qua mạng LAN hoặc Internet (Railway TCP Proxy). Hệ thống gồm hai module độc lập:

- **Server** (`sever/`): Xử lý nghiệp vụ, quản lý phiên đấu giá, lưu dữ liệu SQLite, deploy trên Railway — chạy nền liên tục, không cần chạy local.
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

**Yêu cầu môi trường:**
- JDK 21+
- Apache Maven 3.8+

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
UserDAO    (interface) ← UserDAOSQLiteImpl
ItemDAO    (interface) ← ItemDAOSQLiteImpl
BidDAO     (interface) ← BidDAOSQLiteImpl
AuctionDAO (interface) ← AuctionDAOSQLiteImpl
```

### 4.6 MVC (Model–View–Controller)
- **Model**: `User`, `Item`, `Auction`, `BidTransaction`
- **View**: FXML files (`LoginView.fxml`, `SellerViewfinal.fxml`, …)
- **Controller**: `LoginController`, `SellerController`, `BiddingController`, …

---

## 5. Chức năng đã hoàn thành

### Quản lý người dùng
- [x] Đăng ký tài khoản (Bidder / Seller)
- [x] Đăng nhập / Đăng xuất
- [x] Phân quyền theo vai trò (Admin / Seller / Bidder)
- [x] Khóa / mở khóa tài khoản (Admin)
- [x] Chặn mọi thao tác ngay khi tài khoản bị khóa

### Quản lý sản phẩm (Seller)
- [x] Tạo sản phẩm theo danh mục: Art, Electronics, Vehicle
- [x] Xem danh sách sản phẩm
- [x] Xóa sản phẩm

### Phiên đấu giá
- [x] Tạo phiên đấu giá với thời gian tùy chỉnh
- [x] Vòng đời phiên: `OPEN → RUNNING → FINISHED`
- [x] Tự động kết thúc phiên theo thời gian
- [x] Anti-sniping: gia hạn thêm 60s nếu có bid trong 60s cuối
- [x] Hủy toàn bộ phiên OPEN/RUNNING khi seller bị ban

### Đặt giá (Bidder)
- [x] Đặt giá — phải cao hơn giá hiện tại
- [x] Kiểm tra số dư đủ trước khi đặt
- [x] Thread-safe: khóa per-auction bằng `ReentrantLock`
- [x] Rút khỏi phiên đấu giá

### Ví điện tử
- [x] Nạp tiền vào ví (Bidder)
- [x] Tự động trừ tiền người thắng, cộng doanh thu Seller khi phiên kết thúc

### Hệ thống
- [x] Giao tiếp Client–Server qua Socket TCP (JSON)
- [x] Phục hồi phiên đang chạy sau khi server restart
- [x] Deploy server lên Railway (không cần chạy local)

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

## 7. Vị trí file JAR

> Server chạy trên Railway — **không cần JAR server khi dùng.**
> Chỉ cần build và chạy file JAR phía Client.

| File | Đường dẫn | Mô tả |
|---|---|---|
| `client.jar` | `Client/target/client.jar` | File chạy giao diện người dùng |

Build để tạo file JAR:

```bash
cd Client
mvn clean package -DskipTests
```

---

## 8. Hướng dẫn cài đặt & chạy

> Server đang chạy trên Railway — **chỉ cần chạy Client.**

### Bước 1: Build Client

```bash
cd Client
mvn clean package -DskipTests
```

Output: `Client/target/client.jar`

### Bước 2: Chạy Client

```bash
java -jar Client/target/client.jar
```

Hoặc dùng Maven trực tiếp (không cần build JAR trước):

```bash
cd Client
mvn javafx:run
```

Client sẽ tự động kết nối đến server Railway theo cấu hình trong `Client/src/main/resources/config.properties`.

---

## 9. Giao thức Client–Server

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

| Action | Vai trò | Mô tả |
|---|---|---|
| `REGISTER` | Tất cả | Đăng ký tài khoản mới |
| `LOGIN` | Tất cả | Đăng nhập |
| `LIST_ITEMS` | Tất cả | Lấy danh sách sản phẩm |
| `CREATE_ITEM` | Seller | Tạo sản phẩm mới |
| `DELETE_ITEM` | Seller | Xóa sản phẩm |
| `CREATE_AUCTION` | Seller | Tạo phiên đấu giá |
| `LIST_AUCTIONS` | Tất cả | Lấy danh sách phiên đấu giá |
| `DELETE_AUCTION` | Seller/Admin | Xóa phiên đấu giá |
| `PLACE_BID` | Bidder | Đặt giá |
| `DEPOSIT` | Bidder | Nạp tiền vào ví |
| `GET_BALANCE` | Bidder/Seller | Lấy số dư hiện tại |
| `BAN_USER` | Admin | Khóa/mở khóa tài khoản |
| `LIST_USERS` | Admin | Danh sách người dùng |

---

## 10. Báo cáo & Demo

| Tài liệu | Link |
|---|---|
| Báo cáo PDF | _[Chèn link tại đây]_ |
| Video demo | _[Chèn link tại đây]_ |
