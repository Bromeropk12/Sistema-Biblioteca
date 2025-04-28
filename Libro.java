public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private String estado;

    public Libro(String isbn, String titulo, String autor) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = "Disponible";
    }

    // Getters y setters
    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEstado() {
        return estado;
    }

    // Métodos de la clase
    public boolean consultarDisponibilidad() {
        return estado.equals("Disponible");
    }

    public void actualizarEstado(String nuevoEstado) {
        if (nuevoEstado.equals("Disponible") || nuevoEstado.equals("Prestado")) {
            this.estado = nuevoEstado;
        }
    }
}