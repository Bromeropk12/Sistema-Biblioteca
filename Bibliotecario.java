public class Bibliotecario {
    private int id;
    private String nombre;

    public Bibliotecario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean validarPrestamo(Prestamo prestamo) {
        // Validar que el préstamo cumpla con las reglas de la biblioteca
        if (prestamo == null) return false;
        
        // Verificar que el usuario no tenga multas pendientes
        if (prestamo.getMulta() > 0) {
            return false;
        }

        return true;
    }

    public void gestionarUsuario(Usuario usuario, String nuevoTipo) {
        // Validar que el nuevo tipo sea válido
        if ("Estudiante".equals(nuevoTipo) || "Staff".equals(nuevoTipo)) {
            // En una implementación real, aquí se actualizaría el tipo de usuario
            // en la base de datos o sistema de persistencia
        } else {
            throw new IllegalArgumentException("Tipo de usuario no válido");
        }
    }
}