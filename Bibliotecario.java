public class Bibliotecario {
    private int id;
    private String nombre;

    public Bibliotecario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public boolean validarPrestamo(Prestamo prestamo) {
        if (prestamo == null) return false;
        
        Usuario usuario = prestamo.getUsuario();
        
        // Verificar que el usuario no tenga multas pendientes
        if (usuario.tieneMultasPendientes()) {
            return false;
        }

        // Verificar límite de préstamos según tipo de usuario
        if (!usuario.puedeRealizarPrestamo()) {
            return false;
        }

        // Verificar disponibilidad del libro
        if (!prestamo.getLibro().consultarDisponibilidad()) {
            return false;
        }

        return true;
    }

    public String evaluarEstadoPrestamo(Prestamo prestamo) {
        if (prestamo == null) return "Préstamo inválido";
        
        StringBuilder estado = new StringBuilder();
        
        // Verificar estado del préstamo
        estado.append("Estado: ").append(prestamo.getEstadoPrestamo()).append("\n");
        
        // Verificar días restantes
        long diasRestantes = prestamo.getDiasRestantes();
        if (diasRestantes > 0) {
            estado.append("Días restantes: ").append(diasRestantes).append("\n");
        } else {
            estado.append("Préstamo vencido\n");
        }
        
        // Verificar multas
        double multa = prestamo.getMulta();
        if (multa > 0) {
            estado.append("Multa pendiente: $").append(String.format("%.2f", multa)).append("\n");
        }
        
        // Verificar si puede renovar
        if (prestamo.puedeRenovar()) {
            estado.append("Elegible para renovación");
        } else {
            estado.append("No elegible para renovación");
        }
        
        return estado.toString();
    }

    public void gestionarUsuario(Usuario usuario, String nuevoTipo) {
        if (usuario == null) throw new IllegalArgumentException("Usuario no puede ser null");
        
        if ("Estudiante".equals(nuevoTipo) || "Staff".equals(nuevoTipo)) {
            // Verificar si tiene préstamos activos que excedan el nuevo límite
            int prestamosActivos = usuario.getNumPrestamosActivos();
            int nuevoLimite = "Staff".equals(nuevoTipo) ? 5 : 3;
            
            if (prestamosActivos > nuevoLimite) {
                throw new IllegalStateException("El usuario tiene más préstamos activos que el permitido para el nuevo tipo");
            }
            
            // En una implementación real, aquí se actualizaría el tipo de usuario
            // en la base de datos o sistema de persistencia
        } else {
            throw new IllegalArgumentException("Tipo de usuario no válido");
        }
    }
}