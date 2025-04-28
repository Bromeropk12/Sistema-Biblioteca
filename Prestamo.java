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
    private boolean renovado;
    private int numeroRenovaciones;
    private static final double MULTA_POR_DIA = 1000.0;
    private static final int DIAS_PRESTAMO = 14;
    private static final int DIAS_RENOVACION = 7;

    public Prestamo(Usuario usuario, Libro libro) {
        this.id = contadorId++;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaInicio = new Date();
        this.fechaFin = calcularFechaFin(DIAS_PRESTAMO);
        this.multa = 0.0;
        this.activo = true;
        this.renovado = false;
        this.numeroRenovaciones = 0;
    }

    private Date calcularFechaFin(int dias) {
        return new Date(new Date().getTime() + TimeUnit.DAYS.toMillis(dias));
    }

    public boolean puedeRenovar() {
        return activo && !renovado && new Date().before(fechaFin) && multa == 0;
    }

    public double calcularMulta() {
        if (!activo) return multa;
        
        Date hoy = new Date();
        if (hoy.after(fechaFin)) {
            long diferencia = hoy.getTime() - fechaFin.getTime();
            long diasRetraso = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS);
            multa = diasRetraso * MULTA_POR_DIA;
            return multa;
        }
        return 0.0;
    }

    public boolean renovar() {
        if (!puedeRenovar()) {
            return false;
        }
        fechaFin = calcularFechaFin(DIAS_RENOVACION);
        renovado = true;
        numeroRenovaciones++;
        return true;
    }

    public int getNumeroRenovaciones() {
        return numeroRenovaciones;
    }

    public void actualizarEstadoRenovacion(boolean renovado, int numeroRenovaciones) {
        this.renovado = renovado;
        this.numeroRenovaciones = numeroRenovaciones;
    }

    public void cerrar() {
        if (activo) {
            activo = false;
            libro.actualizarEstado("Disponible");
            calcularMulta();
        }
    }

    // Getters
    public int getId() { return id; }
    public Date getFechaInicio() { return new Date(fechaInicio.getTime()); }
    public Date getFechaFin() { return new Date(fechaFin.getTime()); }
    public double getMulta() { return multa; }
    public boolean isActivo() { return activo; }
    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public boolean isRenovado() { return renovado; }

    public void actualizarFechas(Date fechaInicio, Date fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public void actualizarMulta(double multa) {
        this.multa = multa;
    }

    public long getDiasRestantes() {
        Date hoy = new Date();
        if (hoy.after(fechaFin)) return 0;
        return TimeUnit.DAYS.convert(fechaFin.getTime() - hoy.getTime(), TimeUnit.MILLISECONDS);
    }

    public String getEstadoPrestamo() {
        if (!activo) return "Devuelto";
        if (new Date().after(fechaFin)) return "Vencido";
        return "Activo";
    }

    public static void actualizarContador(int nuevoContador) {
        contadorId = Math.max(contadorId, nuevoContador + 1);
    }

    public void setId(int id) {
        this.id = id;
        actualizarContador(id);
    }

    public void actualizarEstado(String estado) {
        if ("Devuelto".equals(estado)) {
            this.activo = false;
        } else {
            this.activo = true;
        }
    }
}