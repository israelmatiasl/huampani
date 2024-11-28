package huampani;

import java.util.Scanner;

public class Huampani {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== Bienvenido al Sistema de Gestión de Inventarios ===");

        // Proceso de inicio de sesión
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            System.out.print("Ingrese el usuario: ");
            String username = scanner.nextLine();
            System.out.print("Ingrese la contraseña: ");
            String password = scanner.nextLine();

            if (login.ingresar(username, password)) {
                isAuthenticated = true;
                System.out.println("¡Inicio de sesión exitoso!\n");
            } else {
                System.out.println("Credenciales incorrectas. Intente nuevamente.\n");
            }
        }

        // Instanciar los gestores necesarios
        GestorInventario inventoryManager = new GestorInventario();
        GestorOrden orderManager = new GestorOrden();
        Reporte reporte = new Reporte(inventoryManager, orderManager);

        // Menú principal
        int option = 0;
        while (option != 5) {
            System.out.println("=== Menú Principal ===");
            System.out.println("1. Gestión de Inventario y Productos");
            System.out.println("2. Gestión de Pedidos y Ventas");
            System.out.println("3. Generar Reportes");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = 0;
            }

            switch (option) {
                case 1:
                    inventoryManager.menu();
                    break;
                case 2:
                    orderManager.menu(inventoryManager);
                    break;
                case 3:
                    reporte.menu();
                    break;
                case 4:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.\n");
                    break;
            }
        }
        
        scanner.close();
    }
}