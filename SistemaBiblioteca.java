import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaBiblioteca {
    private static List<Libro> libros = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<Bibliotecario> bibliotecarios = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDatos();
        
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    consultarCatalogo();
                    break;
                case 2:
                    realizarPrestamo();
                    break;
                case 3:
                    devolverLibro();
                    break;
                case 4:
                    agregarLibro();
                    break;
                case 5:
                    agregarUsuario();
                    break;
                case 6:
                    System.out.println("Desea continuar con la iteracion? (s/n)");
                    continuar = scanner.nextLine().toLowerCase().equals("s");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
        System.out.println("Gracias por usar el Sistema de Biblioteca!");
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE BIBLIOTECA ===");
        System.out.println("1. Consultar catalogo");
        System.out.println("2. Realizar prestamo");
        System.out.println("3. Devolver libro");
        System.out.println("4. Agregar libro");
        System.out.println("5. Agregar usuario");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void inicializarDatos() {
        // Agregar algunos datos de prueba
        libros.add(new Libro("123", "Don Quijote", "Cervantes"));
        libros.add(new Libro("456", "Cien anos de soledad", "Garcia Marquez"));
        
        usuarios.add(new Usuario(1, "Juan Perez", "Estudiante"));
        usuarios.add(new Usuario(2, "Maria Lopez", "Staff"));
        
        bibliotecarios.add(new Bibliotecario(1, "Carlos Ruiz"));
    }

    private static void consultarCatalogo() {
        System.out.println("\n=== CATALOGO DE LIBROS ===");
        for (Libro libro : libros) {
            System.out.printf("ISBN: %s | Titulo: %s | Autor: %s | Estado: %s%n",
                libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getEstado());
        }
    }

    private static void realizarPrestamo() {
        System.out.println("\n=== REALIZAR PRESTAMO ===");
        
        // Mostrar usuarios disponibles
        System.out.println("Usuarios disponibles:");
        for (Usuario usuario : usuarios) {
            System.out.printf("%d - %s (%s)%n", usuario.getId(), usuario.getNombre(), usuario.getTipo());
        }
        System.out.print("Seleccione ID de usuario: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        // Buscar usuario
        Usuario usuario = usuarios.stream()
            .filter(u -> u.getId() == userId)
            .findFirst()
            .orElse(null);

        if (usuario == null) {
            System.out.println("Usuario no encontrado");
            return;
        }

        // Mostrar libros disponibles
        System.out.println("Libros disponibles:");
        for (Libro libro : libros) {
            if (libro.consultarDisponibilidad()) {
                System.out.printf("ISBN: %s - %s%n", libro.getIsbn(), libro.getTitulo());
            }
        }
        
        System.out.print("Ingrese ISBN del libro: ");
        String isbn = scanner.nextLine();

        // Buscar libro
        Libro libro = libros.stream()
            .filter(l -> l.getIsbn().equals(isbn))
            .findFirst()
            .orElse(null);

        if (libro == null) {
            System.out.println("Libro no encontrado");
            return;
        }

        // Realizar prestamo
        Prestamo prestamo = usuario.solicitarPrestamo(libro);
        if (prestamo != null) {
            System.out.println("Prestamo realizado con exito");
        } else {
            System.out.println("No se pudo realizar el prestamo");
        }
    }

    private static void devolverLibro() {
        System.out.println("\n=== DEVOLVER LIBRO ===");
        System.out.print("Ingrese ISBN del libro a devolver: ");
        String isbn = scanner.nextLine();

        Libro libro = libros.stream()
            .filter(l -> l.getIsbn().equals(isbn))
            .findFirst()
            .orElse(null);

        if (libro == null || libro.consultarDisponibilidad()) {
            System.out.println("Libro no encontrado o ya esta disponible");
            return;
        }

        libro.actualizarEstado("Disponible");
        System.out.println("Libro devuelto con exito");
    }

    private static void agregarLibro() {
        System.out.println("\n=== AGREGAR LIBRO ===");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        libros.add(new Libro(isbn, titulo, autor));
        System.out.println("Libro agregado con exito");
    }

    private static void agregarUsuario() {
        System.out.println("\n=== AGREGAR USUARIO ===");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Tipo (Estudiante/Staff): ");
        String tipo = scanner.nextLine();

        try {
            int nuevoId = usuarios.stream().mapToInt(Usuario::getId).max().orElse(0) + 1;
            usuarios.add(new Usuario(nuevoId, nombre, tipo));
            System.out.println("Usuario agregado con exito");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}