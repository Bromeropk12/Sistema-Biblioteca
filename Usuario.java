import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nombre;
    private String tipo;
    private List<Prestamo> prestamos;

    public Usuario(int id, String nombre, String tipo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (!"Estudiante".equals(tipo) && !"Staff".equals(tipo)) {
            throw new IllegalArgumentException("El tipo debe ser 'Estudiante' o 'Staff'");
        }
        
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.prestamos = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public List<Prestamo> getPrestamos() {
        return new ArrayList<>(prestamos);
    }

    public Prestamo solicitarPrestamo(Libro libro) {
        if (libro.consultarDisponibilidad()) {
            Prestamo nuevoPrestamo = new Prestamo(this, libro);
            prestamos.add(nuevoPrestamo);
            libro.actualizarEstado("Prestado");
            return nuevoPrestamo;
        }
        return null;
    }

    public boolean renovarPrestamo(Prestamo prestamo) {
        if (prestamos.contains(prestamo) && prestamo.getMulta() == 0) {
            return prestamo.renovar();
        }
        return false;
    }
}