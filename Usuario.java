import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nombre;
    private String tipo;
    private List<Prestamo> prestamos;
    private static final int MAX_PRESTAMOS_ESTUDIANTE = 3;
    private static final int MAX_PRESTAMOS_STAFF = 5;

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

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public List<Prestamo> getPrestamos() { 
        return new ArrayList<>(prestamos);
    }

    public void agregarPrestamoHistorico(Prestamo prestamo) {
        if (!prestamos.contains(prestamo)) {
            prestamos.add(prestamo);
        }
    }

    public boolean tieneMultasPendientes() {
        return prestamos.stream()
            .filter(Prestamo::isActivo)
            .anyMatch(p -> p.getMulta() > 0);
    }

    public int getNumPrestamosActivos() {
        return (int) prestamos.stream()
            .filter(Prestamo::isActivo)
            .count();
    }

    public boolean puedeRealizarPrestamo() {
        if (tieneMultasPendientes()) return false;
        
        int maxPrestamos = "Staff".equals(tipo) ? MAX_PRESTAMOS_STAFF : MAX_PRESTAMOS_ESTUDIANTE;
        return getNumPrestamosActivos() < maxPrestamos;
    }

    public Prestamo solicitarPrestamo(Libro libro) {
        if (!puedeRealizarPrestamo() || !libro.consultarDisponibilidad()) {
            return null;
        }

        Prestamo nuevoPrestamo = new Prestamo(this, libro);
        prestamos.add(nuevoPrestamo);
        libro.actualizarEstado("Prestado");
        return nuevoPrestamo;
    }

    public boolean renovarPrestamo(Prestamo prestamo) {
        if (!prestamos.contains(prestamo) || prestamo.getMulta() > 0 || !prestamo.puedeRenovar()) {
            return false;
        }
        return prestamo.renovar();
    }

    public double getMultaTotal() {
        return prestamos.stream()
            .mapToDouble(Prestamo::getMulta)
            .sum();
    }
}