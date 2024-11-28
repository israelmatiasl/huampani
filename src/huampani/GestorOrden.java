package huampani;

import java.io.*;
import java.time.LocalDate;
import java.util.*;


public class GestorOrden {
    private final String ORDERS_DIR = "ordenes";
    private List<Orden> ordenes;
    private Scanner scanner;
    private static int contadorIdOrden; // Contador para IDs de órdenes

    public GestorOrden() {
        scanner = new Scanner(System.in);
        ordenes = new ArrayList<>();

        // Crear el directorio principal si no existe
        File ordersDir = new File(ORDERS_DIR);
        if (!ordersDir.exists()) {
            ordersDir.mkdir();
        }

        // Cargar órdenes existentes
        cargarOrdenes();
    }
    
    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void menu(GestorInventario gestorInventario) {
        int option = 0;
        while (option != 3) {
            System.out.println("\n--- Gestión de Pedidos y Ventas ---");
            System.out.println("1. Registrar Pedido");
            System.out.println("2. Ver Pedidos");
            System.out.println("3. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = 0;
            }

            switch (option) {
                case 1:
                    addOrder(gestorInventario);
                    break;
                case 2:
                    viewOrders();
                    break;
                case 3:
                    System.out.println("Regresando al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    private void addOrder(GestorInventario gestorInventario) {
        try {
            int id = obtenerSiguienteIdOrden();
            List<DetalleProducto> ordenProductos = new ArrayList<>();
            String addMore;
            do {
                System.out.print("Ingrese el ID del producto a agregar al pedido: ");
                int productId = Integer.parseInt(scanner.nextLine());
                Producto producto = gestorInventario.findProductById(productId);
                if (producto != null) {
                    System.out.print("Se refiere al producto: " + producto.getName() + " (S o N): ");
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("S")) {
                        // Obtener las caducidades (ProductoCaducidad) del producto
                        List<ProductoCaducidad> caducidades = gestorInventario.cargarProductoCaducidades(producto);
                        if (caducidades.isEmpty()) {
                            System.out.println("No hay lotes disponibles para este producto.");
                        } else {
                            // Ordenar las caducidades por fecha de caducidad ascendente
                            caducidades.sort(Comparator.comparing(ProductoCaducidad::getCaducidad));
                            // Mostrar las caducidades enumeradas
                            System.out.println("Desde qué fecha de caducidad desea adquirir:");
                            for (int i = 0; i < caducidades.size(); i++) {
                                ProductoCaducidad pc = caducidades.get(i);
                                System.out.println((i + 1) + ". " + pc.getCodigoProducto() + " (" + pc.getCantidad() + " unidades)");
                            }
                            int caducidadIndex = -1;
                            do {
                                System.out.print("Seleccione el número de la fecha de caducidad: ");
                                try {
                                    caducidadIndex = Integer.parseInt(scanner.nextLine()) - 1;
                                } catch (NumberFormatException e) {
                                    caducidadIndex = -1;
                                }
                                if (caducidadIndex < 0 || caducidadIndex >= caducidades.size()) {
                                    System.out.println("Selección inválida. Intente nuevamente.");
                                    caducidadIndex = -1;
                                }
                            } while (caducidadIndex == -1);
                            System.out.print("Cuántas unidades del producto desea adquirir? ");
                            int cantidadDeseada = Integer.parseInt(scanner.nextLine());
                            int cantidadRestante = cantidadDeseada;
                            List<DetalleProducto> detalles = new ArrayList<>();
                            // Intentar cumplir la cantidad desde el lote seleccionado en adelante
                            for (int i = caducidadIndex; i < caducidades.size() && cantidadRestante > 0; i++) {
                                ProductoCaducidad pc = caducidades.get(i);
                                int cantidadDisponible = pc.getCantidad();
                                if (cantidadDisponible > 0) {
                                    int cantidadTomar = Math.min(cantidadDisponible, cantidadRestante);
                                    // NO modificar el inventario
                                    // pc.setCantidad(cantidadDisponible - cantidadTomar);
                                    // gestorInventario.guardarProductoCaducidad(pc);
                                    // Crear DetalleProducto
                                    DetalleProducto detalle = new DetalleProducto(productId, pc.getCaducidad(), cantidadTomar);
                                    detalles.add(detalle);
                                    cantidadRestante -= cantidadTomar;
                                }
                            }
                            if (cantidadRestante > 0) {
                                System.out.println("No hay stock suficiente para cumplir con la cantidad solicitada.");
                                // Opción de agregar la cantidad disponible o cancelar
                                System.out.print("¿Desea agregar la cantidad disponible (" + (cantidadDeseada - cantidadRestante) + " unidades)? (S/N): ");
                                String opcion = scanner.nextLine();
                                if (opcion.equalsIgnoreCase("S")) {
                                    // Agregar los DetalleProducto con la cantidad disponible
                                    ordenProductos.addAll(detalles);
                                    System.out.println("Producto agregado al pedido con cantidad disponible.");
                                } else {
                                    // No es necesario revertir cambios en el inventario
                                    System.out.println("Operación cancelada.");
                                }
                            } else {
                                // Se pudo cumplir con la cantidad solicitada
                                ordenProductos.addAll(detalles);
                                System.out.println("Producto agregado al pedido.");
                            }
                        }
                    } else {
                        System.out.println("Por favor, ingrese nuevamente el ID del producto.");
                    }
                } else {
                    System.out.println("Producto no encontrado.");
                }
                System.out.print("¿Desea agregar otro producto al pedido? (s/n): ");
                addMore = scanner.nextLine();
            } while (addMore.equalsIgnoreCase("s"));
            Orden orden = new Orden(id, ordenProductos, LocalDate.now(), "Pendiente");
            ordenes.add(orden);
            guardarOrdenes();
            guardarDetalleOrden(orden);
            System.out.println("Pedido registrado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al registrar el pedido. Verifique los datos ingresados.");
            e.printStackTrace();
        }
    }

    private int obtenerSiguienteIdOrden() {
        int maxId = 0;
        for (Orden orden : ordenes) {
            if (orden.getId() > maxId) {
                maxId = orden.getId();
            }
        }
        return maxId + 1;
    }

    private void guardarOrdenes() {
        String ordenesFilePath = ORDERS_DIR + File.separator + "ordenes.txt";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ordenesFilePath))) {
            oos.writeObject(ordenes);
        } catch (IOException e) {
            System.out.println("Error al guardar las órdenes.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarOrdenes() {
        String ordenesFilePath = ORDERS_DIR + File.separator + "ordenes.txt";
        File ordenesFile = new File(ordenesFilePath);
        if (ordenesFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ordenesFile))) {
                ordenes = (List<Orden>) ois.readObject();
                // Cargar los detalles de cada orden
                for (Orden orden : ordenes) {
                    cargarDetalleOrden(orden);
                }
                // Actualizar contadorIdOrden
                contadorIdOrden = obtenerSiguienteIdOrden();
            } catch (Exception e) {
                System.out.println("Error al cargar las órdenes.");
                e.printStackTrace();
            }
        } else {
            ordenes = new ArrayList<>();
            contadorIdOrden = 1;
        }
    }

    private void guardarDetalleOrden(Orden orden) {
        String detalleFilePath = ORDERS_DIR + File.separator + "orden_" + orden.getId() + ".txt";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(detalleFilePath))) {
            oos.writeObject(orden.getProductos());
        } catch (IOException e) {
            System.out.println("Error al guardar los detalles de la orden.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDetalleOrden(Orden orden) {
        String detalleFilePath = ORDERS_DIR + File.separator + "orden_" + orden.getId() + ".txt";
        File detalleFile = new File(detalleFilePath);
        if (detalleFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(detalleFile))) {
                List<DetalleProducto> detalles = (List<DetalleProducto>) ois.readObject();
                orden.setProductos(detalles);
            } catch (Exception e) {
                System.out.println("Error al cargar los detalles de la orden ID: " + orden.getId());
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontraron detalles para la orden ID: " + orden.getId());
        }
    }

    private void viewOrders() {
        if (ordenes.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }

        System.out.println("\nLista de Pedidos:");
        for (Orden orden : ordenes) {
            System.out.println(orden);
        }
    }
}