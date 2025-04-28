import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class PersistenciaDatos {
    private static final String RUTA_LIBROS = "libros.csv";
    private static final String RUTA_USUARIOS = "usuarios.csv";
    private static final String RUTA_PRESTAMOS = "prestamos.csv";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Guardar datos con mejor manejo de errores
    public static void guardarLibros(List<Libro> libros) {
        try {
            Path path = Paths.get(RUTA_LIBROS);
            List<String> lineas = new ArrayList<>();
            for (Libro libro : libros) {
                lineas.add(String.format("%s,%s,%s,%s",
                    libro.getIsbn(),
                    escaparCSV(libro.getTitulo()),
                    escaparCSV(libro.getAutor()),
                    libro.getEstado()));
            }
            Files.write(path, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            manejarError("Error al guardar libros", e);
        }
    }

    public static void guardarUsuarios(List<Usuario> usuarios) {
        try {
            Path path = Paths.get(RUTA_USUARIOS);
            List<String> lineas = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                lineas.add(String.format("%d,%s,%s",
                    usuario.getId(),
                    escaparCSV(usuario.getNombre()),
                    usuario.getTipo()));
            }
            Files.write(path, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            manejarError("Error al guardar usuarios", e);
        }
    }

    public static void guardarPrestamos(List<Usuario> usuarios) {
        try {
            Path path = Paths.get(RUTA_PRESTAMOS);
            List<String> lineas = new ArrayList<>();
            // Encabezado CSV para mejor legibilidad
            lineas.add("ID,UsuarioID,ISBN,FechaInicio,FechaFin,Multa,Estado,Renovado,HistorialRenovaciones");
            
            for (Usuario usuario : usuarios) {
                for (Prestamo prestamo : usuario.getPrestamos()) {
                    lineas.add(String.format("%d,%d,%s,%s,%s,%.2f,%s,%b,%d",
                        prestamo.getId(),
                        usuario.getId(),
                        prestamo.getLibro().getIsbn(),
                        formatoFecha.format(prestamo.getFechaInicio()),
                        formatoFecha.format(prestamo.getFechaFin()),
                        prestamo.getMulta(),
                        prestamo.getEstadoPrestamo(),
                        prestamo.isRenovado(),
                        prestamo.getNumeroRenovaciones()));
                }
            }
            Files.write(path, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error al guardar prestamos: " + e.getMessage());
            e.printStackTrace(); // Agregar traza de pila para depuración
        }
    }

    // Cargar datos con validación mejorada
    public static List<Libro> cargarLibros() {
        List<Libro> libros = new ArrayList<>();
        Path path = Paths.get(RUTA_LIBROS);
        
        if (!Files.exists(path)) {
            return libros;
        }

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                try {
                    String[] datos = parsearCSV(linea);
                    if (datos.length >= 4 && validarISBN(datos[0])) {
                        Libro libro = new Libro(datos[0], datos[1], datos[2]);
                        libro.actualizarEstado(datos[3]);
                        libros.add(libro);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error al procesar línea de libro: " + linea);
                }
            }
        } catch (IOException e) {
            manejarError("Error al cargar libros", e);
        }
        return libros;
    }

    public static List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Path path = Paths.get(RUTA_USUARIOS);
        
        if (!Files.exists(path)) {
            return usuarios;
        }

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                try {
                    String[] datos = parsearCSV(linea);
                    if (datos.length >= 3 && validarId(datos[0])) {
                        usuarios.add(new Usuario(
                            Integer.parseInt(datos[0]),
                            datos[1],
                            datos[2]
                        ));
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error al procesar línea de usuario: " + linea);
                }
            }
        } catch (IOException e) {
            manejarError("Error al cargar usuarios", e);
        }
        return usuarios;
    }

    public static void cargarPrestamos(List<Usuario> usuarios, List<Libro> libros) {
        Path path = Paths.get(RUTA_PRESTAMOS);
        
        if (!Files.exists(path)) {
            return;
        }

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
            boolean primeraLinea = true;
            int maxId = 0;
            
            for (String linea : lineas) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar el encabezado
                }
                
                try {
                    String[] datos = parsearCSV(linea);
                    if (datos.length >= 9 && validarDatosPrestamo(datos)) {
                        int prestamoId = Integer.parseInt(datos[0]);
                        maxId = Math.max(maxId, prestamoId);
                        procesarLineaPrestamo(datos, usuarios, libros);
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar línea de préstamo: " + linea);
                }
            }
            
            // Actualizar el contador de IDs
            Prestamo.actualizarContador(maxId);
            
        } catch (IOException e) {
            manejarError("Error al cargar préstamos", e);
        }
    }

    private static void procesarLineaPrestamo(String[] datos, List<Usuario> usuarios, List<Libro> libros) {
        try {
            int usuarioId = Integer.parseInt(datos[1]);
            String isbn = datos[2];
            
            Usuario usuario = encontrarUsuario(usuarios, usuarioId);
            Libro libro = encontrarLibro(libros, isbn);
            
            if (usuario != null && libro != null) {
                // Crear el préstamo sin cambiar el estado del libro
                Prestamo prestamo = new Prestamo(usuario, libro);
                
                Date fechaInicio = formatoFecha.parse(datos[3]);
                Date fechaFin = formatoFecha.parse(datos[4]);
                double multa = Double.parseDouble(datos[5]);
                boolean renovado = Boolean.parseBoolean(datos[7]);
                int numRenovaciones = Integer.parseInt(datos[8]);
                
                actualizarPrestamo(prestamo, fechaInicio, fechaFin, multa, renovado, numRenovaciones);
                prestamo.actualizarEstado(datos[6]); // Actualizar estado (Activo/Devuelto)
                
                // Si el préstamo está activo, actualizar el estado del libro
                if (prestamo.isActivo()) {
                    libro.actualizarEstado("Prestado");
                } else {
                    libro.actualizarEstado("Disponible");
                }
                
                // Agregar el préstamo al historial del usuario
                usuario.agregarPrestamoHistorico(prestamo);
            }
        } catch (ParseException | NumberFormatException e) {
            manejarError("Error al procesar datos del préstamo", e);
        }
    }

    private static String escaparCSV(String texto) {
        if (texto == null) return "";
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    private static String[] parsearCSV(String linea) {
        List<String> elementos = new ArrayList<>();
        boolean entreComillas = false;
        StringBuilder elemento = new StringBuilder();
        
        for (char c : linea.toCharArray()) {
            if (c == '\"') {
                entreComillas = !entreComillas;
            } else if (c == ',' && !entreComillas) {
                elementos.add(elemento.toString());
                elemento = new StringBuilder();
            } else {
                elemento.append(c);
            }
        }
        elementos.add(elemento.toString());
        
        return elementos.toArray(new String[0]);
    }

    private static boolean validarISBN(String isbn) {
        return isbn != null && !isbn.trim().isEmpty();
    }

    private static boolean validarId(String id) {
        try {
            return Integer.parseInt(id) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validarDatosPrestamo(String[] datos) {
        return datos.length >= 9 &&
               validarId(datos[0]) &&
               validarId(datos[1]) &&
               validarISBN(datos[2]);
    }

    private static void manejarError(String mensaje, Exception e) {
        System.err.println(mensaje + ": " + e.getMessage());
        // Aquí podrías agregar logging más sofisticado si es necesario
    }

    private static Usuario encontrarUsuario(List<Usuario> usuarios, int id) {
        return usuarios.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private static Libro encontrarLibro(List<Libro> libros, String isbn) {
        return libros.stream()
            .filter(l -> l.getIsbn().equals(isbn))
            .findFirst()
            .orElse(null);
    }

    private static void actualizarPrestamo(Prestamo prestamo, Date fechaInicio, Date fechaFin, 
                                         double multa, boolean renovado, int numRenovaciones) {
        prestamo.actualizarFechas(fechaInicio, fechaFin);
        prestamo.actualizarMulta(multa);
        prestamo.actualizarEstadoRenovacion(renovado, numRenovaciones);
    }
}