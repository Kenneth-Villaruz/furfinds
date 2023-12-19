package appdev.technologies.furfindspetshop.recyclerviews;

public class FavoritesItem
{
    private int id;
    private String name;
    private String category;
    private String image;
    private String price;

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getPrice() {return price;}

    public void setPrice(String price) {this.price = price;}
}