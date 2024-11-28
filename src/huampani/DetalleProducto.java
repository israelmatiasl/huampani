package huampani;

import java.io.Serializable;
import java.time.LocalDate;

public class DetalleProducto implements Serializable {
    private static final long serialVersionUID = 1L; 

    private int productoId;
    private LocalDate caducidad;
    private int cantidad;

    public DetalleProducto(int productoId, LocalDate caducidad, int cantidad) {
        this.productoId = productoId;
        this.caducidad = caducidad;
        this.cantidad = cantidad;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public LocalDate getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(LocalDate caducidad) {
        this.caducidad = caducidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "DetalleProducto{" +
                "productoId=" + productoId +
                ", caducidad=" + caducidad +
                ", cantidad=" + cantidad +
                '}';
    }
}
