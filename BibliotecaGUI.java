import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BibliotecaGUI extends JFrame {
    private SistemaBiblioteca sistema;
    private JTabbedPane tabbedPane;
    private JTable tablaLibros;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloLibros;
    private DefaultTableModel modeloUsuarios;
    
    public BibliotecaGUI() {
        sistema = new SistemaBiblioteca();
        inicializarInterfaz();
    }
    
    private void inicializarInterfaz() {
        setTitle("Sistema de Biblioteca - Universidad Antonio Nariño");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear el panel principal con pestañas
        tabbedPane = new JTabbedPane();
        
        // Inicializar las pestañas
        inicializarPestanaLibros();
        inicializarPestanaUsuarios();
        inicializarPestanaPrestamos();
        
        add(tabbedPane);
        
        // Agregar listener para guardar datos al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sistema.guardarDatos();
            }
        });
    }
    
    private void inicializarPestanaLibros() {
        JPanel panelLibros = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla para libros
        modeloLibros = new DefaultTableModel(
            new Object[]{"ISBN", "Título", "Autor", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaLibros = new JTable(modeloLibros);
        JScrollPane scrollLibros = new JScrollPane(tablaLibros);
        
        // Panel de botones para libros
        JPanel botonesLibros = new JPanel();
        JButton btnAgregarLibro = new JButton("Agregar Libro");
        JButton btnBuscarLibro = new JButton("Buscar");
        JTextField txtBusquedaLibro = new JTextField(20);
        
        botonesLibros.add(new JLabel("Búsqueda: "));
        botonesLibros.add(txtBusquedaLibro);
        botonesLibros.add(btnBuscarLibro);
        botonesLibros.add(btnAgregarLibro);
        
        panelLibros.add(botonesLibros, BorderLayout.NORTH);
        panelLibros.add(scrollLibros, BorderLayout.CENTER);
        
        // Eventos
        btnAgregarLibro.addActionListener(e -> mostrarDialogoAgregarLibro());
        btnBuscarLibro.addActionListener(e -> buscarLibros(txtBusquedaLibro.getText()));
        
        tabbedPane.addTab("Libros", panelLibros);
        actualizarTablaLibros();
    }
    
    private void inicializarPestanaUsuarios() {
        JPanel panelUsuarios = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla para usuarios
        modeloUsuarios = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Tipo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloUsuarios);
        JScrollPane scrollUsuarios = new JScrollPane(tablaUsuarios);
        
        // Panel de botones para usuarios
        JPanel botonesUsuarios = new JPanel();
        JButton btnAgregarUsuario = new JButton("Agregar Usuario");
        JButton btnBuscarUsuario = new JButton("Buscar");
        JTextField txtBusquedaUsuario = new JTextField(20);
        
        botonesUsuarios.add(new JLabel("Búsqueda: "));
        botonesUsuarios.add(txtBusquedaUsuario);
        botonesUsuarios.add(btnBuscarUsuario);
        botonesUsuarios.add(btnAgregarUsuario);
        
        panelUsuarios.add(botonesUsuarios, BorderLayout.NORTH);
        panelUsuarios.add(scrollUsuarios, BorderLayout.CENTER);
        
        // Eventos
        btnAgregarUsuario.addActionListener(e -> mostrarDialogoAgregarUsuario());
        btnBuscarUsuario.addActionListener(e -> buscarUsuarios(txtBusquedaUsuario.getText()));
        
        tabbedPane.addTab("Usuarios", panelUsuarios);
        actualizarTablaUsuarios();
    }
    
    private void inicializarPestanaPrestamos() {
        JPanel panelPrestamos = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla para préstamos
        DefaultTableModel modeloPrestamos = new DefaultTableModel(
            new Object[]{"ID Usuario", "Nombre", "ISBN", "Título", "Fecha Fin", "Estado", "Multa", "Días Restantes"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaPrestamos = new JTable(modeloPrestamos);
        JScrollPane scrollPrestamos = new JScrollPane(tablaPrestamos);
        
        // Panel de botones
        JPanel botonesPrestamos = new JPanel();
        JButton btnNuevoPrestamo = new JButton("Nuevo Préstamo");
        JButton btnDevolverLibro = new JButton("Devolver Libro");
        JButton btnRenovarPrestamo = new JButton("Renovar Préstamo");
        JButton btnActualizar = new JButton("Actualizar");
        
        botonesPrestamos.add(btnNuevoPrestamo);
        botonesPrestamos.add(btnDevolverLibro);
        botonesPrestamos.add(btnRenovarPrestamo);
        botonesPrestamos.add(btnActualizar);
        
        // Eventos
        btnNuevoPrestamo.addActionListener(e -> mostrarDialogoNuevoPrestamo());
        btnDevolverLibro.addActionListener(e -> mostrarDialogoDevolverLibro());
        btnRenovarPrestamo.addActionListener(e -> mostrarDialogoRenovarPrestamo());
        btnActualizar.addActionListener(e -> actualizarTablaPrestamos(modeloPrestamos));
        
        panelPrestamos.add(botonesPrestamos, BorderLayout.NORTH);
        panelPrestamos.add(scrollPrestamos, BorderLayout.CENTER);
        
        tabbedPane.addTab("Préstamos", panelPrestamos);
        
        // Actualizar la tabla inicialmente
        actualizarTablaPrestamos(modeloPrestamos);
        
        // Configurar un temporizador para actualizar las multas cada minuto
        Timer timer = new Timer(60000, e -> {
            sistema.verificarMultas();
            actualizarTablaPrestamos(modeloPrestamos);
        });
        timer.start();
    }

    private void actualizarTablaPrestamos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Usuario usuario : sistema.getUsuarios()) {
            for (Prestamo prestamo : usuario.getPrestamos()) {
                if (prestamo.isActivo()) {
                    modelo.addRow(new Object[]{
                        usuario.getId(),
                        usuario.getNombre(),
                        prestamo.getLibro().getIsbn(),
                        prestamo.getLibro().getTitulo(),
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(prestamo.getFechaFin()),
                        prestamo.getEstadoPrestamo(),
                        String.format("$%.2f", prestamo.getMulta()),
                        prestamo.getDiasRestantes()
                    });
                }
            }
        }
    }

    private void mostrarDialogoAgregarLibro() {
        JDialog dialogo = new JDialog(this, "Agregar Libro", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField txtIsbn = new JTextField();
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        
        dialogo.add(new JLabel("ISBN:"));
        dialogo.add(txtIsbn);
        dialogo.add(new JLabel("Título:"));
        dialogo.add(txtTitulo);
        dialogo.add(new JLabel("Autor:"));
        dialogo.add(txtAutor);
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            if (!txtIsbn.getText().isEmpty() && !txtTitulo.getText().isEmpty() && !txtAutor.getText().isEmpty()) {
                sistema.agregarLibro(txtIsbn.getText(), txtTitulo.getText(), txtAutor.getText());
                actualizarTablaLibros();
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Todos los campos son requeridos");
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.add(btnGuardar);
        dialogo.add(btnCancelar);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoAgregarUsuario() {
        JDialog dialogo = new JDialog(this, "Agregar Usuario", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField txtNombre = new JTextField();
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Estudiante", "Staff"});
        
        dialogo.add(new JLabel("Nombre:"));
        dialogo.add(txtNombre);
        dialogo.add(new JLabel("Tipo:"));
        dialogo.add(comboTipo);
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            if (!txtNombre.getText().isEmpty()) {
                sistema.agregarUsuario(txtNombre.getText(), (String)comboTipo.getSelectedItem());
                actualizarTablaUsuarios();
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "El nombre es requerido");
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.add(btnGuardar);
        dialogo.add(btnCancelar);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoNuevoPrestamo() {
        JDialog dialogo = new JDialog(this, "Nuevo Préstamo", true);
        dialogo.setLayout(new GridLayout(5, 2, 5, 5));
        
        // Obtener listas de usuarios y libros disponibles
        List<Usuario> usuarios = sistema.getUsuarios();
        List<Libro> librosDisponibles = sistema.getLibrosDisponibles();
        
        JComboBox<String> comboUsuarios = new JComboBox<>();
        JComboBox<String> comboLibros = new JComboBox<>();
        
        for (Usuario usuario : usuarios) {
            comboUsuarios.addItem(usuario.getId() + " - " + usuario.getNombre());
        }
        
        for (Libro libro : librosDisponibles) {
            comboLibros.addItem(libro.getIsbn() + " - " + libro.getTitulo());
        }
        
        dialogo.add(new JLabel("Usuario:"));
        dialogo.add(comboUsuarios);
        dialogo.add(new JLabel("Libro:"));
        dialogo.add(comboLibros);
        
        JButton btnPrestar = new JButton("Realizar Préstamo");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnPrestar.addActionListener(e -> {
            if (comboUsuarios.getSelectedIndex() != -1 && comboLibros.getSelectedIndex() != -1) {
                String usuarioSeleccionado = (String)comboUsuarios.getSelectedItem();
                String libroSeleccionado = (String)comboLibros.getSelectedItem();
                
                int userId = Integer.parseInt(usuarioSeleccionado.split(" - ")[0]);
                String isbn = libroSeleccionado.split(" - ")[0];
                
                if (sistema.realizarPrestamo(userId, isbn)) {
                    actualizarTablaLibros();
                    dialogo.dispose();
                    JOptionPane.showMessageDialog(this, "Préstamo realizado con éxito");
                } else {
                    JOptionPane.showMessageDialog(dialogo, "No se pudo realizar el préstamo");
                }
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.add(btnPrestar);
        dialogo.add(btnCancelar);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoDevolverLibro() {
        JDialog dialogo = new JDialog(this, "Devolución de Libro", true);
        dialogo.setLayout(new GridLayout(3, 2, 5, 5));
        
        List<Libro> librosPrestados = sistema.getLibrosPrestados();
        JComboBox<String> comboLibros = new JComboBox<>();
        
        for (Libro libro : librosPrestados) {
            comboLibros.addItem(libro.getIsbn() + " - " + libro.getTitulo());
        }
        
        dialogo.add(new JLabel("Libro a devolver:"));
        dialogo.add(comboLibros);
        
        JButton btnDevolver = new JButton("Devolver");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnDevolver.addActionListener(e -> {
            if (comboLibros.getSelectedIndex() != -1) {
                String libroSeleccionado = (String)comboLibros.getSelectedItem();
                String isbn = libroSeleccionado.split(" - ")[0];
                
                if (sistema.devolverLibro(isbn)) {
                    actualizarTablaLibros();
                    dialogo.dispose();
                    JOptionPane.showMessageDialog(this, "Libro devuelto con éxito");
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Error al devolver el libro");
                }
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.add(btnDevolver);
        dialogo.add(btnCancelar);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoRenovarPrestamo() {
        JDialog dialogo = new JDialog(this, "Renovar Préstamo", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        
        JComboBox<String> comboUsuarios = new JComboBox<>();
        JComboBox<String> comboLibros = new JComboBox<>();
        
        // Llenar combo de usuarios
        for (Usuario usuario : sistema.getUsuarios()) {
            for (Prestamo prestamo : usuario.getPrestamos()) {
                if (prestamo.isActivo() && prestamo.puedeRenovar()) {
                    comboUsuarios.addItem(usuario.getId() + " - " + usuario.getNombre());
                    break;
                }
            }
        }
        
        // Listener para actualizar libros cuando se selecciona un usuario
        comboUsuarios.addActionListener(e -> {
            comboLibros.removeAllItems();
            if (comboUsuarios.getSelectedItem() != null) {
                int userId = Integer.parseInt(((String)comboUsuarios.getSelectedItem()).split(" - ")[0]);
                Usuario usuario = sistema.getUsuarios().stream()
                    .filter(u -> u.getId() == userId)
                    .findFirst()
                    .orElse(null);
                    
                if (usuario != null) {
                    for (Prestamo prestamo : usuario.getPrestamos()) {
                        if (prestamo.isActivo() && prestamo.puedeRenovar()) {
                            comboLibros.addItem(prestamo.getLibro().getIsbn() + " - " + prestamo.getLibro().getTitulo());
                        }
                    }
                }
            }
        });
        
        dialogo.add(new JLabel("Usuario:"));
        dialogo.add(comboUsuarios);
        dialogo.add(new JLabel("Libro:"));
        dialogo.add(comboLibros);
        
        JButton btnRenovar = new JButton("Renovar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnRenovar.addActionListener(e -> {
            if (comboUsuarios.getSelectedItem() != null && comboLibros.getSelectedItem() != null) {
                int userId = Integer.parseInt(((String)comboUsuarios.getSelectedItem()).split(" - ")[0]);
                String isbn = ((String)comboLibros.getSelectedItem()).split(" - ")[0];
                
                if (sistema.renovarPrestamo(userId, isbn)) {
                    JOptionPane.showMessageDialog(this, "Préstamo renovado exitosamente");
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo renovar el préstamo");
                }
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.add(btnRenovar);
        dialogo.add(btnCancelar);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void actualizarTablaLibros() {
        modeloLibros.setRowCount(0);
        for (Libro libro : sistema.getLibros()) {
            modeloLibros.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getEstado()
            });
        }
    }
    
    private void actualizarTablaUsuarios() {
        modeloUsuarios.setRowCount(0);
        for (Usuario usuario : sistema.getUsuarios()) {
            modeloUsuarios.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getTipo()
            });
        }
    }
    
    private void buscarLibros(String texto) {
        modeloLibros.setRowCount(0);
        for (Libro libro : sistema.buscarLibros(texto)) {
            modeloLibros.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getEstado()
            });
        }
    }
    
    private void buscarUsuarios(String texto) {
        modeloUsuarios.setRowCount(0);
        for (Usuario usuario : sistema.buscarUsuarios(texto)) {
            modeloUsuarios.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getTipo()
            });
        }
    }
    
    public static void main(String[] args) {
        // Configurar codificación para caracteres especiales
        System.setProperty("file.encoding", "UTF-8");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Configurar fuentes para soportar caracteres especiales
                setUIFont(new javax.swing.plaf.FontUIResource("Dialog", Font.PLAIN, 12));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BibliotecaGUI().setVisible(true);
        });
    }

    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }
}
