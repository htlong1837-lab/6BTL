public class Electronics extends Item {
    private String brand;
    private String warrantyInfo;
 
    public Electronics(String id, String name, String description,
                       double startPrice, String brand, String warrantyInfo) {
        super(id, name, description, startPrice);
        this.brand = brand;
        this.warrantyInfo = warrantyInfo;
    }
 
    public String getBrand()       { return brand; }
    public String getWarrantyInfo(){ return warrantyInfo; }
 
    @Override
    public void printInfo() {
        System.out.println("[Electronics]");
        System.out.println("ID         : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Description: " + description);
        System.out.println("Start price: " + String.format("%,.0f", startPrice) + " VND");
        System.out.println("Brand      : " + brand);
        System.out.println("Warranty   : " + warrantyInfo);
    }
}
 