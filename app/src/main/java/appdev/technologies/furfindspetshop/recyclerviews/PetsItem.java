package appdev.technologies.furfindspetshop.recyclerviews;

/**
 * Deserialized Model representation of animals table.
 */
public class PetsItem
{
    private int id;
    private int ownerFK;
    private String name;
    private String category;
    private String description;
    private boolean forSale;
    private boolean forAdoption;
    private float salePrice;
    private String healthCondition;
    private String image;

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isForSale() { return forSale; }
    public void setForSale(boolean forSale) { this.forSale = forSale; }

    public boolean isForAdoption() { return forAdoption; }
    public void setForAdoption(boolean forAdoption) { this.forAdoption = forAdoption; }

    public float getSalePrice() { return salePrice; }
    public void setSalePrice(float salePrice) { this.salePrice = salePrice; }

    public String getHealthCondition() { return healthCondition; }
    public void setHealthCondition(String healthCondition) { this.healthCondition = healthCondition; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getOwnerFK() {return ownerFK;}

    public void setOwnerFK(int ownerFK) {this.ownerFK = ownerFK;}
}