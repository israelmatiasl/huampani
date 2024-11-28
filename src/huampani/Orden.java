package huampani;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


public class Orden implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private List<DetalleProducto> productos;
    private LocalDate date;
    private String status;

    public Orden(int id, List<DetalleProducto> productos, LocalDate date, String status) {
        this.id = id;
        this.productos = productos;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DetalleProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<DetalleProducto> productos) {
        this.productos = productos;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido [ID=").append(id)
                .append(", Fecha=").append(date)
                .append(", Estado=").append(status)
                .append(", Productos:\n");
        for (DetalleProducto detalle : productos) {
            sb.append("    ProductoID: ").append(detalle.getProductoId())
                    .append(", Caducidad: ").append(detalle.getCaducidad())
                    .append(", Cantidad: ").append(detalle.getCantidad())
                    .append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}