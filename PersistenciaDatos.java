import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class PersistenciaDatos {
    private static final String RUTA_LIBROS = "libros.csv";
    private static final String RUTA_USUARIOS = "usuarios.csv";
    private static final String RUTA_PRESTAMOS = "prestamos.csv";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

    // Guardar datos
    public static void guardarLibros(List<Libro> libros) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LIBROS))) {
            for (Libro libro : libros) {
                writer.println(String.format("%s,%s,%s,%s",
                    libro.getIsbn(),
                    libro.getTitulo().replace(",", ";"),
                    libro.getAutor().replace(",", ";"),
                    libro.getEstado()));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar libros: " + e.getMessage());
        }
    }

    public static void guardarUsuarios(List<Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_USUARIOS))) {
            for (Usuario usuario : usuarios) {
                writer.println(String.format("%d,%s,%s",
                    usuario.getId(),
                    usuario.getNombre().replace(",", ";"),
                    usuario.getTipo()));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static void guardarPrestamos(List<Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS))) {
            for (Usuario usuario : usuarios) {
                for (Prestamo prestamo : usuario.getPrestamos()) {
                    if (prestamo.isActivo()) {
                        writer.println(String.format("%d,%d,%s,%s,%s,%.2f",
                            prestamo.getId(),
                            usuario.getId(),
                            prestamo.getLibro().getIsbn(),
                            formatoFecha.format(prestamo.getFechaInicio()),
                            formatoFecha.format(prestamo.getFechaFin()),
                            prestamo.getMulta()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar prestamos: " + e.getMessage());
        }
    }

    // Cargar datos
    public static List<Libro> cargarLibros() {
        List<Libro> libros = new ArrayList<>();
        File archivo = new File(RUTA_LIBROS);
        
        if (!archivo.exists()) {
            return libros;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    Libro libro = new Libro(datos[0], datos[1].replace(";", ","), datos[2].replace(";", ","));
                    libro.actualizarEstado(datos[3]);
                    libros.add(libro);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar libros: " + e.getMessage());
        }
        return libros;
    }

    public static List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        File archivo = new File(RUTA_USUARIOS);
        
        if (!archivo.exists()) {
            return usuarios;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    usuarios.add(new Usuario(
                        Integer.parseInt(datos[0]),
                        datos[1].replace(";", ","),
                        datos[2]
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public static void cargarPrestamos(List<Usuario> usuarios, List<Libro> libros) {
        File archivo = new File(RUTA_PRESTAMOS);
        
        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 6) {
                    int usuarioId = Integer.parseInt(datos[1]);
                    String isbn = datos[2];
                    
                    Usuario usuario = encontrarUsuario(usuarios, usuarioId);
                    Libro libro = encontrarLibro(libros, isbn);
                    
                    if (usuario != null && libro != null) {
                        Prestamo prestamo = usuario.solicitarPrestamo(libro);
                        // Actualizar fechas y multa del préstamo cargado
                        if (prestamo != null) {
                            try {
                                actualizarPrestamo(prestamo, 
                                    formatoFecha.parse(datos[3]), 
                                    formatoFecha.parse(datos[4]), 
                                    Double.parseDouble(datos[5]));
                            } catch (ParseException e) {
                                System.err.println("Error al parsear fechas: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar prestamos: " + e.getMessage());
        }
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

    private static void actualizarPrestamo(Prestamo prestamo, Date fechaInicio, Date fechaFin, double multa) {
        // Necesitamos agregar métodos para actualizar estos valores en la clase Prestamo
        prestamo.actualizarFechas(fechaInicio, fechaFin);
        prestamo.actualizarMulta(multa);
    }
}