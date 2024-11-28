package huampani;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class Reporte {
    private GestorInventario gestorInventario;
    private GestorOrden gestorOrden;

    public Reporte(GestorInventario gestorInventario, GestorOrden gestorOrden) {
        this.gestorInventario = gestorInventario;
        this.gestorOrden = gestorOrden;
    }

    public void menu() {
        int option = 0;
        Scanner scanner = new Scanner(System.in);
        while (option != 6) {
            System.out.println("\n--- Generación de Reportes ---");
            System.out.println("1. Reporte de Inventario Actual");
            System.out.println("2. Reporte de Órdenes Registradas");
            System.out.println("3. Reporte de Productos con Bajo Stock");
            System.out.println("4. Reporte de Productos Próximos a Caducar");
            System.out.println("5. Reporte de Ventas Totales");
            System.out.println("6. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = 0;
            }

            switch (option) {
                case 1:
                    generarReporteInventarioActual();
                    break;
                case 2:
                    generarReporteOrdenesRegistradas();
                    break;
                case 3:
                    generarReporteProductosBajoStock();
                    break;
                case 4:
                    generarReporteProductosProximosACaducar();
                    break;
                case 5:
                    generarReporteVentasTotales();
                    break;
                case 6:
                    System.out.println("Regresando al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    // 1. Reporte de Inventario Actual
    private void generarReporteInventarioActual() {
        System.out.println("\n--- Reporte de Inventario Actual ---");
        List<Producto> productos = gestorInventario.getProductos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
        } else {
            for (Producto producto : productos) {
                System.out.println(producto);
                List<ProductoCaducidad> caducidades = gestorInventario.cargarProductoCaducidades(producto);
                if (caducidades.isEmpty()) {
                    System.out.println("  No hay lotes registrados para este producto.");
                } else {
                    System.out.println("  Lotes:");
                    for (ProductoCaducidad pc : caducidades) {
                        System.out.println("    " + pc);
                    }
                }
            }
        }
    }

    // 2. Reporte de Órdenes Registradas
    private void generarReporteOrdenesRegistradas() {
        System.out.println("\n--- Reporte de Órdenes Registradas ---");
        List<Orden> ordenes = gestorOrden.getOrdenes();
        if (ordenes.isEmpty()) {
            System.out.println("No hay órdenes registradas.");
        } else {
            for (Orden orden : ordenes) {
                System.out.println(orden);
            }
        }
    }

    // 3. Reporte de Productos con Bajo Stock
    private void generarReporteProductosBajoStock() {
        System.out.println("\n--- Reporte de Productos con Bajo Stock ---");
        List<Producto> productos = gestorInventario.getProductos();
        int umbral = solicitarUmbral();
        boolean hayProductosBajoStock = false;
        for (Producto producto : productos) {
            int cantidadTotal = 0;
            List<ProductoCaducidad> caducidades = gestorInventario.cargarProductoCaducidades(producto);
            for (ProductoCaducidad pc : caducidades) {
                cantidadTotal += pc.getCantidad();
            }
            if (cantidadTotal < umbral) {
                System.out.println(producto);
                System.out.println("  Cantidad total en stock: " + cantidadTotal);
                hayProductosBajoStock = true;
            }
        }
        if (!hayProductosBajoStock) {
            System.out.println("No hay productos con stock por debajo del umbral de " + umbral + ".");
        }
    }

    // 4. Reporte de Productos Próximos a Caducar
    private void generarReporteProductosProximosACaducar() {
        System.out.println("\n--- Reporte de Productos Próximos a Caducar ---");
        List<Producto> productos = gestorInventario.getProductos();
        LocalDate fechaLimite = LocalDate.now().plusDays(solicitarDias());
        boolean hayProductosProximosACaducar = false;
        for (Producto producto : productos) {
            List<ProductoCaducidad> caducidades = gestorInventario.cargarProductoCaducidades(producto);
            for (ProductoCaducidad pc : caducidades) {
                if (pc.getCaducidad().isBefore(fechaLimite)) {
                    System.out.println(producto);
                    System.out.println("  Lote próximo a caducar:");
                    System.out.println("    " + pc);
                    hayProductosProximosACaducar = true;
                }
            }
        }
        if (!hayProductosProximosACaducar) {
            System.out.println("No hay productos próximos a caducar en los próximos días especificados.");
        }
    }

    // 5. Reporte de Ventas Totales
    private void generarReporteVentasTotales() {
        System.out.println("\n--- Reporte de Ventas Totales ---");
        List<Orden> ordenes = gestorOrden.getOrdenes();
        double totalVentas = 0.0;
        for (Orden orden : ordenes) {
            double totalOrden = 0.0;
            for (DetalleProducto detalle : orden.getProductos()) {
                Producto producto = gestorInventario.findProductById(detalle.getProductoId());
                if (producto != null) {
                    totalOrden += producto.getPrice() * detalle.getCantidad();
                }
            }
            System.out.println("Orden ID: " + orden.getId() + ", Total: $" + totalOrden);
            totalVentas += totalOrden;
        }
        System.out.println("Ventas totales: $" + totalVentas);
    }

    // Métodos auxiliares para solicitar umbral y días
    private int solicitarUmbral() {
        Scanner scanner = new Scanner(System.in);
        int umbral = 0;
        do {
            System.out.print("Ingrese el umbral de stock mínimo: ");
            try {
                umbral = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                umbral = -1;
            }
        } while (umbral < 0);
        return umbral;
    }

    private int solicitarDias() {
        Scanner scanner = new Scanner(System.in);
        int dias = 0;
        do {
            System.out.print("Ingrese el número de días para considerar productos próximos a caducar: ");
            try {
                dias = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                dias = -1;
            }
        } while (dias < 0);
        return dias;
    }
}