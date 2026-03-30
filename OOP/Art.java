public class Art extends Item {
    private String artist;
    private int yearCreated;
 
    public Art(String id, String name, String description,
               double startPrice, String artist, int yearCreated) {
        super(id, name, description, startPrice);
        this.artist = artist;
        this.yearCreated = yearCreated;
    }
 
    public String getArtist()   { return artist; }
    public int getYearCreated() { return yearCreated; }
 
    @Override
    public void printInfo() {
        System.out.println("[Art]");
        System.out.println("ID         : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Description: " + description);
        System.out.println("Start price: " + String.format("%,.0f", startPrice) + " VND");
        System.out.println("Artist     : " + artist);
        System.out.println("Year       : " + yearCreated);
    }
}