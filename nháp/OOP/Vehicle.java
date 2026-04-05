public class Vehicle extends Item {
    private String vehicleType; // e.g. "Car", "Motorcycle", "Truck"
    private int year;
    private String licensePlate;
 
    public Vehicle(String id, String name, String description,
                   double startPrice, String vehicleType, int year, String licensePlate) {
        super(id, name, description, startPrice);
        this.vehicleType = vehicleType;
        this.year = year;
        this.licensePlate = licensePlate;
    }
 
    public String getVehicleType()  { return vehicleType; }
    public int getYear()            { return year; }
    public String getLicensePlate() { return licensePlate; }
 
    @Override
    public void printInfo() {
        System.out.println("[Vehicle]");
        System.out.println("ID          : " + id);
        System.out.println("Name        : " + name);
        System.out.println("Description : " + description);
        System.out.println("Start price : " + String.format("%,.0f", startPrice) + " VND");
        System.out.println("Type        : " + vehicleType);
        System.out.println("Year        : " + year);
        System.out.println("License     : " + licensePlate);
    }
}