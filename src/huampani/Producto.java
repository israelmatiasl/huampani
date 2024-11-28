package huampani;

import java.io.Serializable;

public class Producto implements Serializable {
    private int id;
    private String name;
    private String category;
    private double price;
    private String certification;

    public Producto(int id, String name, String category, double price, String certification) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.certification = certification;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCertification() {
        return certification;
    }

    @Override
    public String toString() {
        return "Producto [ID=" + id + ", Nombre=" + name + ", Categoría=" + category + ", Precio=" + price
                + ", Certificación=" + certification + "]";
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }
}