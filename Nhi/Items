import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Item {
    private int id;
    private String name;
    private String description;
    private double startingPrice;
    private double currentPrice;
    private String owner;
    private boolean isAvailable;

    public Item(int id, String name, String description, double startingPrice, String owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.owner = owner;
        this.isAvailable = true;
    }

    // Getter methods
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getStartingPrice() { return startingPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public String getOwner() { return owner; }
    public boolean isAvailable() { return isAvailable; }

    // Setter methods
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setAvailable(boolean available) { isAvailable = available; }

    // Hiển thị thông tin item
    public void displayInfo() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Mã số: " + id);
        System.out.println("Tên vật phẩm: " + name);
        System.out.println("Mô tả: " + description);
        System.out.println("Giá khởi điểm: " + startingPrice + " VND");
        System.out.println("Giá hiện tại: " + currentPrice + " VND");
        System.out.println("Chủ sở hữu: " + owner);
        System.out.println("Trạng thái: " + (isAvailable ? "Đang đấu giá" : "Đã bán"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    // Phương thức đặt giá mới
    public boolean placeBid(double bidAmount) {
        if (bidAmount > currentPrice && isAvailable) {
            currentPrice = bidAmount;
            return true;
        }
        return false;
    }
}

// ========== LỚP QUẢN LÝ ITEM ==========
class ItemManager {
    private List<Item> items;
    private int nextId;
    private Scanner scanner;

    public ItemManager(Scanner scanner) {
        items = new ArrayList<>();
        nextId = 1;
        this.scanner = scanner;
    }

    // NHẬP VẬT PHẨM TÙY Ý TỪ BÀN PHÍM
    public void addNewItem() {
        System.out.println("\n=== THÊM VẬT PHẨM ĐẤU GIÁ MỚI ===");

        System.out.print("Tên vật phẩm: ");
        String name = scanner.nextLine();

        System.out.print("Mô tả chi tiết: ");
        String description = scanner.nextLine();

        System.out.print("Giá khởi điểm (VND): ");
        double startingPrice = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        System.out.print("Chủ sở hữu: ");
        String owner = scanner.nextLine();

        Item item = new Item(nextId++, name, description, startingPrice, owner);
        items.add(item);

        System.out.println("\n✅ ĐÃ THÊM THÀNH CÔNG!");
        item.displayInfo();
    }
}
public class Items {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ItemManager manager = new ItemManager(scanner);

        manager.addNewItem();  // Thêm 1 vật phẩm
        manager.showAllItems(); // Xem danh sách

        scanner.close();
    }
}
