package appdev.technologies.furfindspetshop.recyclerviews;

public class PostsItem
{
    private int id;
    private int ownerFk;
    private String textContent;
    private String imageContent;
    private String date;
    private String ownerName;

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImageContent() { return imageContent; }
    public void setImageContent(String image) { this.imageContent = image; }

    public int getOwnerFk() {return ownerFk;}
    public void setOwnerFk(int ownerFk) {this.ownerFk = ownerFk;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getTextContent() {return textContent;}
    public void setTextContent(String textContent) {this.textContent = textContent;}

    public String getOwnerName() {return ownerName;}
    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}
}