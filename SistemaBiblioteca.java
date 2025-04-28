import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaBiblioteca {
    private List<Libro> libros;
    private List<Usuario> usuarios;
    private List<Bibliotecario> bibliotecarios;

    public SistemaBiblioteca() {
        libros = new ArrayList<>();
        usuarios = new ArrayList<>();
        bibliotecarios = new ArrayList<>();
        cargarDatos();
    }

    // Métodos públicos para la interfaz gráfica
    public void cargarDatos() {
        libros = PersistenciaDatos.cargarLibros();
        usuarios = PersistenciaDatos.cargarUsuarios();
        
        if (libros.isEmpty() && usuarios.isEmpty()) {
            inicializarDatos();
        } else {
            PersistenciaDatos.cargarPrestamos(usuarios, libros);
        }
    }

    public void guardarDatos() {
        PersistenciaDatos.guardarLibros(libros);
        PersistenciaDatos.guardarUsuarios(usuarios);
        PersistenciaDatos.guardarPrestamos(usuarios);
    }

    private void inicializarDatos() {
        // Agregar libros
        libros.add(new Libro("123", "Don Quijote", "Miguel de Cervantes"));
        libros.add(new Libro("456", "Cien años de soledad", "Gabriel Garcia Marquez"));
        libros.add(new Libro("457", "1984", "George Orwell"));
        libros.add(new Libro("458", "El Señor de los Anillos", "J.R.R. Tolkien"));
        libros.add(new Libro("459", "Harry Potter y la Piedra Filosofal", "J.K. Rowling"));
        libros.add(new Libro("460", "El Principito", "Antoine de Saint-Exupery"));
        libros.add(new Libro("461", "Crimen y castigo", "Fiodor Dostoievski"));
        libros.add(new Libro("462", "Orgullo y prejuicio", "Jane Austen"));
        libros.add(new Libro("463", "La Odisea", "Homero"));
        libros.add(new Libro("464", "El Alquimista", "Paulo Coelho"));
        libros.add(new Libro("465", "Rayuela", "Julio Cortazar"));
        libros.add(new Libro("466", "Los juegos del hambre", "Suzanne Collins"));
        libros.add(new Libro("467", "El codigo Da Vinci", "Dan Brown"));
        libros.add(new Libro("468", "Las cronicas de Narnia", "C.S. Lewis"));
        libros.add(new Libro("469", "La sombra del viento", "Carlos Ruiz Zafon"));
        libros.add(new Libro("470", "El nombre del viento", "Patrick Rothfuss"));
        libros.add(new Libro("471", "Fahrenheit 451", "Ray Bradbury"));
        libros.add(new Libro("472", "La metamorfosis", "Franz Kafka"));
        libros.add(new Libro("473", "El Hobbit", "J.R.R. Tolkien"));
        libros.add(new Libro("474", "Dracula", "Bram Stoker"));
        libros.add(new Libro("475", "Los miserables", "Victor Hugo"));
        libros.add(new Libro("476", "La casa de los espiritus", "Isabel Allende"));

        // Agregar estudiantes
        usuarios.add(new Usuario(1, "Ana Martinez", "Estudiante"));
        usuarios.add(new Usuario(2, "Carlos Rodriguez", "Estudiante"));
        usuarios.add(new Usuario(3, "Sofia Garcia", "Estudiante"));
        usuarios.add(new Usuario(4, "Diego Lopez", "Estudiante"));
        usuarios.add(new Usuario(5, "Valentina Torres", "Estudiante"));
        usuarios.add(new Usuario(6, "Sebastian Ramirez", "Estudiante"));
        usuarios.add(new Usuario(7, "Camila Flores", "Estudiante"));
        usuarios.add(new Usuario(8, "Andres Herrera", "Estudiante"));
        usuarios.add(new Usuario(9, "Isabella Morales", "Estudiante"));
        usuarios.add(new Usuario(10, "Lucas Jimenez", "Estudiante"));
        usuarios.add(new Usuario(11, "Emma Vargas", "Estudiante"));
        usuarios.add(new Usuario(12, "Mateo Castro", "Estudiante"));
        usuarios.add(new Usuario(13, "Victoria Rios", "Estudiante"));
        usuarios.add(new Usuario(14, "Nicolas Silva", "Estudiante"));
        usuarios.add(new Usuario(15, "Lucia Mendoza", "Estudiante"));
        usuarios.add(new Usuario(16, "Daniel Ortiz", "Estudiante"));
        usuarios.add(new Usuario(17, "Paula Guerrero", "Estudiante"));
        usuarios.add(new Usuario(18, "Gabriel Sanchez", "Estudiante"));
        usuarios.add(new Usuario(19, "Mariana Perez", "Estudiante"));
        usuarios.add(new Usuario(20, "Samuel Romero", "Estudiante"));

        // Agregar personal Staff
        usuarios.add(new Usuario(21, "Carmen Molina", "Staff"));
        usuarios.add(new Usuario(22, "Jorge Navarro", "Staff"));
        usuarios.add(new Usuario(23, "Patricia Delgado", "Staff"));
        usuarios.add(new Usuario(24, "Roberto Medina", "Staff"));
        usuarios.add(new Usuario(25, "Elena Fuentes", "Staff"));
        usuarios.add(new Usuario(26, "Fernando Cruz", "Staff"));
        usuarios.add(new Usuario(27, "Laura Vega", "Staff"));
        usuarios.add(new Usuario(28, "Miguel Angel Rojas", "Staff"));
        usuarios.add(new Usuario(29, "Beatriz Soto", "Staff"));
        usuarios.add(new Usuario(30, "Ricardo Aguirre", "Staff"));
        
        // Agregar bibliotecario
        bibliotecarios.add(new Bibliotecario(1, "Carlos Ruiz"));
    }

    public List<Libro> getLibros() {
        return new ArrayList<>(libros);
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Libro> getLibrosDisponibles() {
        return libros.stream()
            .filter(Libro::consultarDisponibilidad)
            .collect(Collectors.toList());
    }

    public List<Libro> getLibrosPrestados() {
        return libros.stream()
            .filter(l -> !l.consultarDisponibilidad())
            .collect(Collectors.toList());
    }

    public boolean realizarPrestamo(int userId, String isbn) {
        Usuario usuario = encontrarUsuario(userId);
        Libro libro = encontrarLibro(isbn);
        
        if (usuario != null && libro != null && libro.consultarDisponibilidad()) {
            Prestamo prestamo = usuario.solicitarPrestamo(libro);
            if (prestamo != null) {
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    public void verificarMultas() {
        for (Usuario usuario : usuarios) {
            for (Prestamo prestamo : usuario.getPrestamos()) {
                if (prestamo.isActivo()) {
                    prestamo.calcularMulta();
                }
            }
        }
    }

    public boolean devolverLibro(String isbn) {
        Libro libro = encontrarLibro(isbn);
        if (libro != null && !libro.consultarDisponibilidad()) {
            Usuario usuario = encontrarUsuarioPorLibro(isbn);
            if (usuario != null) {
                Prestamo prestamo = encontrarPrestamoPorLibro(usuario, isbn);
                if (prestamo != null) {
                    prestamo.calcularMulta();  // Calculamos la multa final
                    prestamo.cerrar();
                    libro.actualizarEstado("Disponible");
                    guardarDatos();
                    return true;
                }
            }
        }
        return false;
    }

    private Usuario encontrarUsuarioPorLibro(String isbn) {
        for (Usuario usuario : usuarios) {
            for (Prestamo prestamo : usuario.getPrestamos()) {
                if (prestamo.isActivo() && prestamo.getLibro().getIsbn().equals(isbn)) {
                    return usuario;
                }
            }
        }
        return null;
    }

    private Prestamo encontrarPrestamoPorLibro(Usuario usuario, String isbn) {
        for (Prestamo prestamo : usuario.getPrestamos()) {
            if (prestamo.isActivo() && prestamo.getLibro().getIsbn().equals(isbn)) {
                return prestamo;
            }
        }
        return null;
    }

    public boolean renovarPrestamo(int userId, String isbn) {
        Usuario usuario = encontrarUsuario(userId);
        if (usuario != null) {
            Prestamo prestamo = encontrarPrestamoPorLibro(usuario, isbn);
            if (prestamo != null && prestamo.puedeRenovar()) {
                boolean renovado = prestamo.renovar();
                if (renovado) {
                    guardarDatos();
                }
                return renovado;
            }
        }
        return false;
    }

    public double consultarMulta(int userId, String isbn) {
        Usuario usuario = encontrarUsuario(userId);
        if (usuario != null) {
            Prestamo prestamo = encontrarPrestamoPorLibro(usuario, isbn);
            if (prestamo != null) {
                return prestamo.getMulta();
            }
        }
        return 0.0;
    }

    public List<Libro> buscarLibros(String texto) {
        final String busqueda = texto.toLowerCase();
        return libros.stream()
            .filter(l -> l.getTitulo().toLowerCase().contains(busqueda) ||
                        l.getAutor().toLowerCase().contains(busqueda) ||
                        l.getIsbn().toLowerCase().contains(busqueda))
            .collect(Collectors.toList());
    }

    public List<Usuario> buscarUsuarios(String texto) {
        final String busqueda = texto.toLowerCase();
        return usuarios.stream()
            .filter(u -> u.getNombre().toLowerCase().contains(busqueda) ||
                        String.valueOf(u.getId()).contains(busqueda))
            .collect(Collectors.toList());
    }

    private Usuario encontrarUsuario(int id) {
        return usuarios.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private Libro encontrarLibro(String isbn) {
        return libros.stream()
            .filter(l -> l.getIsbn().equals(isbn))
            .findFirst()
            .orElse(null);
    }

    public void agregarLibro(String isbn, String titulo, String autor) {
        Libro nuevoLibro = new Libro(isbn, titulo, autor);
        libros.add(nuevoLibro);
        guardarDatos();
    }

    public void agregarUsuario(String nombre, String tipo) {
        int nuevoId = usuarios.stream().mapToInt(Usuario::getId).max().orElse(0) + 1;
        Usuario nuevoUsuario = new Usuario(nuevoId, nombre, tipo);
        usuarios.add(nuevoUsuario);
        guardarDatos();
    }
}