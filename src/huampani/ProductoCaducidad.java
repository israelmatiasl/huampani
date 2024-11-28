package huampani;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductoCaducidad implements Serializable {
    private int productoId;
    private String nombreProducto;
    private String codigoProducto; // Formato: NombreProducto_FechaCaducidad
    private int cantidad;
    private LocalDate caducidad;

    public ProductoCaducidad(int productoId, String nombreProducto, int cantidad, LocalDate caducidad) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.caducidad = caducidad;
        this.codigoProducto = nombreProducto + "_" + caducidad.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }

    // Getters y setters

    public int getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getCaducidad() {
        return caducidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setCaducidad(LocalDate caducidad) {
        this.caducidad = caducidad;
    }

    @Override
    public String toString() {
        return "ProductoCaducidad [ProductoID=" + productoId + ", NombreProducto=" + nombreProducto
                + ", CódigoProducto=" + codigoProducto + ", Cantidad=" + cantidad + ", Fecha de Caducidad=" + caducidad
                + "]";
    }
}

