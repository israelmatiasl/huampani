package huampani;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestorInventario {
    private List<Producto> productos;
    private final String PRODUCTS_DIR = "productos"; // Directorio para guardar los archivos
    private final String PRODUCTS_FILE = PRODUCTS_DIR + File.separator + "productos.txt";
    private Scanner scanner;
    private static int contadorIdProducto = 1; // Contador para IDs de productos

    public GestorInventario() {
        productos = new ArrayList<>();
        scanner = new Scanner(System.in);

        // Crear el directorio principal si no existe
        File productsDir = new File(PRODUCTS_DIR);
        if (!productsDir.exists()) {
            productsDir.mkdir();
        }

        loadProducts();

        // Actualizar el contadorIdProducto al máximo existente + 1
        if (!productos.isEmpty()) {
            int maxId = productos.stream().mapToInt(Producto::getId).max().orElse(0);
            contadorIdProducto = maxId + 1;
        }
    }
    
    public List<Producto> getProductos() {
        return productos;
    }

    public void menu() {
        int option = 0;
        while (option != 5) {
            System.out.println("\n--- Gestión de Inventario y Productos ---");
            System.out.println("1. Registrar Producto");
            System.out.println("2. Ver Productos");
            System.out.println("3. Actualizar Producto");
            System.out.println("4. Eliminar Producto");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = 0; // Opción inválida
            }

            switch (option) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("Regresando al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    private void addProduct() {
        try {
            int id = contadorIdProducto++; // Asignar ID automáticamente
            System.out.print("Ingrese el nombre del producto: ");
            String name = scanner.nextLine();
            System.out.print("Ingrese la categoría del producto: ");
            String category = scanner.nextLine();
            System.out.print("Ingrese el precio del producto: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Ingrese la certificación del producto: ");
            String certification = scanner.nextLine();

            Producto producto = new Producto(id, name, category, price, certification);
            productos.add(producto);
            saveProducts();

            // Crear directorio para el producto dentro de "productos"
            String directorioProducto = PRODUCTS_DIR + File.separator + name;
            File directory = new File(directorioProducto);
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Agregar ProductoCaducidad
            String addMore;
            do {
                addExpirationProduct(producto);
                System.out.print("¿Desea agregar otro lote de este producto? (s/n): ");
                addMore = scanner.nextLine();
            } while (addMore.equalsIgnoreCase("s"));

            System.out.println("Producto agregado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al agregar el producto. Verifique los datos ingresados.");
        }
    }

    private void addExpirationProduct(Producto producto) {
        try {
            int productoId = producto.getId();
            String nombreProducto = producto.getName();
            System.out.print("Ingrese la cantidad del lote: ");
            int cantidad = Integer.parseInt(scanner.nextLine());
            System.out.print("Ingrese la fecha de caducidad del lote (YYYY-MM-DD): ");
            LocalDate caducidad = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

            ProductoCaducidad productoCaducidad = new ProductoCaducidad(productoId, nombreProducto, cantidad, caducidad);
            guardarProductoCaducidad(productoCaducidad);

            System.out.println("Lote agregado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al agregar el lote. Verifique los datos ingresados.");
        }
    }

    public void guardarProductoCaducidad(ProductoCaducidad productoCaducidad) {
        String nombreProducto = productoCaducidad.getNombreProducto();
        String directorioProducto = PRODUCTS_DIR + File.separator + nombreProducto;
        File directorio = new File(directorioProducto);
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        String nombreArchivo = directorioProducto + File.separator + productoCaducidad.getCodigoProducto() + ".txt";
        File archivo = new File(nombreArchivo);

        if (archivo.exists()) {
            // Leer el ProductoCaducidad existente y sumar cantidades
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                ProductoCaducidad existente = (ProductoCaducidad) ois.readObject();
                int nuevaCantidad = existente.getCantidad() + productoCaducidad.getCantidad();
                productoCaducidad.setCantidad(nuevaCantidad);
            } catch (Exception e) {
                System.out.println("Error al leer el lote existente. Se sobrescribirá con el nuevo lote.");
            }
        }

        // Guardar el ProductoCaducidad actualizado
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(productoCaducidad);
        } catch (IOException e) {
            System.out.println("Error al guardar el lote.");
        }
    }

    private void viewProducts() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
        } else {
            System.out.println("\nLista de Productos:");
            for (Producto producto : productos) {
                System.out.println(producto);
                List<ProductoCaducidad> productoCaducidades = cargarProductoCaducidades(producto);
                if (!productoCaducidades.isEmpty()) {
                    System.out.println("  Lotes:");
                    for (ProductoCaducidad productoCaducidad : productoCaducidades) {
                        System.out.println("    " + productoCaducidad);
                    }
                } else {
                    System.out.println("  No hay lotes registrados para este producto.");
                }
            }
        }
    }

    public List<ProductoCaducidad> cargarProductoCaducidades(Producto producto) {
        List<ProductoCaducidad> productoCaducidades = new ArrayList<>();
        String directorioProducto = PRODUCTS_DIR + File.separator + producto.getName();
        File directorio = new File(directorioProducto);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".txt"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        ProductoCaducidad productoCaducidad = (ProductoCaducidad) ois.readObject();
                        productoCaducidades.add(productoCaducidad);
                    } catch (Exception e) {
                        System.out.println("Error al cargar el lote " + archivo.getName());
                    }
                }
            }
        }

        return productoCaducidades;
    }

    private void updateProduct() {
        System.out.print("Ingrese el ID del producto a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine());
        Producto producto = findProductById(id);
        if (producto != null) {
            try {
                System.out.print("¿Desea actualizar la información del producto? (s/n): ");
                String updateInfo = scanner.nextLine();
                if (updateInfo.equalsIgnoreCase("s")) {
                    String nombreAnterior = producto.getName();
                    System.out.print("Ingrese el nuevo nombre del producto: ");
                    String name = scanner.nextLine();
                    System.out.print("Ingrese la nueva categoría del producto: ");
                    String category = scanner.nextLine();
                    System.out.print("Ingrese el nuevo precio del producto: ");
                    double price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Ingrese la nueva certificación del producto: ");
                    String certification = scanner.nextLine();

                    producto.setName(name);
                    producto.setCategory(category);
                    producto.setPrice(price);
                    producto.setCertification(certification);

                    // Renombrar el directorio si el nombre cambió
                    if (!nombreAnterior.equals(name)) {
                        String directorioAnterior = PRODUCTS_DIR + File.separator + nombreAnterior;
                        String directorioNuevo = PRODUCTS_DIR + File.separator + name;
                        File dirAnterior = new File(directorioAnterior);
                        File dirNuevo = new File(directorioNuevo);
                        if (dirAnterior.exists()) {
                            boolean exito = dirAnterior.renameTo(dirNuevo);
                            if (!exito) {
                                System.out.println("Error al renombrar el directorio del producto.");
                            }
                        }
                    }

                    saveProducts();
                }

                System.out.print("¿Desea agregar más lotes a este producto? (s/n): ");
                String addMoreLots = scanner.nextLine();
                if (addMoreLots.equalsIgnoreCase("s")) {
                    String addMore;
                    do {
                        addExpirationProduct(producto);
                        System.out.print("¿Desea agregar otro lote de este producto? (s/n): ");
                        addMore = scanner.nextLine();
                    } while (addMore.equalsIgnoreCase("s"));
                }

                System.out.println("Producto actualizado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al actualizar el producto. Verifique los datos ingresados.");
            }
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    private void deleteProduct() {
        System.out.print("Ingrese el ID del producto a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());
        Producto producto = findProductById(id);
        if (producto != null) {
            productos.remove(producto);
            saveProducts();

            // Eliminar el directorio y los archivos de ProductoCaducidad
            String directorioProducto = PRODUCTS_DIR + File.separator + producto.getName();
            File directorio = new File(directorioProducto);
            if (directorio.exists() && directorio.isDirectory()) {
                File[] archivos = directorio.listFiles();
                if (archivos != null) {
                    for (File archivo : archivos) {
                        archivo.delete();
                    }
                }
                directorio.delete();
            }

            System.out.println("Producto eliminado exitosamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    private void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCTS_FILE))) {
            oos.writeObject(productos);
        } catch (IOException e) {
            System.out.println("Error al guardar los productos.");
        }
    }

    private void loadProducts() {
        File productsFile = new File(PRODUCTS_FILE);
        if (productsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(productsFile))) {
                productos = (List<Producto>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error al cargar los productos.");
                productos = new ArrayList<>();
            }
        } else {
            productos = new ArrayList<>();
        }
    }

    public Producto findProductById(int id) {
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null;
    }

    public ProductoCaducidad obtenerProductoCaducidadPorCaducidad(Producto producto, LocalDate caducidad) {
        List<ProductoCaducidad> caducidades = cargarProductoCaducidades(producto);
        for (ProductoCaducidad pc : caducidades) {
            if (pc.getCaducidad().equals(caducidad)) {
                return pc;
            }
        }
        return null;
    }
}