import java.util.ArrayList;
import java.util.List;
 
public class Seller extends User {
    private List<Item> listedItems;
 
    public Seller(String id, String name, String email, String password) {
        super(id, name, email, password);
        this.listedItems = new ArrayList<>();
    }
 
    public List<Item> getListedItems() { return listedItems; }
 
    public void addItem(Item item) {
        listedItems.add(item);
        System.out.println("Item \"" + item.getName() + "\" listed by " + name);
    }
 
    public void removeItem(Item item) {
        listedItems.remove(item);
        System.out.println("Item \"" + item.getName() + "\" removed by " + name);
    }
 
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Items listed: " + listedItems.size());
    }
}
 