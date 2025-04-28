import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Prestamo {
    private static int contadorId = 1;
    private int id;
    private Usuario usuario;
    private Libro libro;
    private Date fechaInicio;
    private Date fechaFin;
    private double multa;
    private boolean activo;

    public Prestamo(Usuario usuario, Libro libro) {
        this.id = contadorId++;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaInicio = new Date();
        // Por defecto, el préstamo es por 14 días
        this.fechaFin = new Date(fechaInicio.getTime() + TimeUnit.DAYS.toMillis(14));
        this.multa = 0.0;
        this.activo = true;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Date getFechaInicio() {
        return new Date(fechaInicio.getTime());
    }

    public Date getFechaFin() {
        return new Date(fechaFin.getTime());
    }

    public double getMulta() {
        return multa;
    }

    public boolean isActivo() {
        return activo;
    }

    public double calcularMulta() {
        if (!activo) return multa;
        
        Date hoy = new Date();
        if (hoy.after(fechaFin)) {
            long diferencia = hoy.getTime() - fechaFin.getTime();
            long diasRetraso = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS);
            // Multa de $1000 por día de retraso
            multa = diasRetraso * 1000.0;
        }
        return multa;
    }

    public void cerrar() {
        if (activo) {
            activo = false;
            libro.actualizarEstado("Disponible");
            calcularMulta();
        }
    }

    public boolean renovar() {
        if (!activo || new Date().after(fechaFin)) {
            return false;
        }
        // Extender por 7 días más
        fechaFin = new Date(fechaFin.getTime() + TimeUnit.DAYS.toMillis(7));
        return true;
    }
}