import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;

public class BanqueroGUI extends JFrame {

    // Paleta Institucional (Inspirada en Banrural)
    private static final Color VERDE_PRINCIPAL = new Color(11, 74, 45);     // Verde oscuro corporativo
    private static final Color VERDE_MEDIO     = new Color(19, 102, 63);    // Verde secundario
    private static final Color AMARILLO_ACCENT = new Color(235, 126, 0);    // Mostaza / Naranja
    private static final Color FONDO_APP       = new Color(241, 245, 241);   // Gris verdoso muy tenue
    private static final Color BLANCO_CARD     = Color.WHITE;
    private static final Color BORDE_SUAVE     = new Color(205, 220, 210);
    private static final Color COLOR_TEXTO     = new Color(30, 40, 35);
    private static final Color COLOR_SAFE      = new Color(21, 128, 61);     // Verde éxito
    private static final Color COLOR_DANGER    = new Color(220, 38, 38);     // Rojo alerta deadlock

    private JTable tablaMaximo;
    private JTable tablaAsignacion;
    private JTable tablaNecesidad;
    private JTextField txtR1, txtR2, txtR3;
    private JTextArea txtLog;
    private JLabel lblBadgeEstado;
    private JLabel lblDetalleResultado;

    public BanqueroGUI() {
        super("Simulador Algoritmo del Banquero - Sistemas Operativos");
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setSize(1040, 800);
        setMinimumSize(new Dimension(960, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO_APP);
        setLayout(new BorderLayout(15, 15));
    }

    private void inicializarComponentes() {
        // --- 1. ENCABEZADO INSTITUCIONAL ---
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setBackground(FONDO_APP);
        panelNorte.setBorder(new EmptyBorder(12, 20, 5, 20));

        // Barra superior tipo banner
        JPanel panelBanner = new JPanel(new BorderLayout());
        panelBanner.setBackground(VERDE_PRINCIPAL);
        panelBanner.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel lblLogoTexto = new JLabel("BANQUERO RURAL | SISTEMAS OPERATIVOS");
        lblLogoTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogoTexto.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Control de Concurrencia • Detección y Prevención de Interbloqueos (Deadlock)");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(230, 245, 235));

        JPanel panelTextosHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTextosHeader.setOpaque(false);
        panelTextosHeader.add(lblLogoTexto);
        panelTextosHeader.add(lblSub);

        panelBanner.add(panelTextosHeader, BorderLayout.WEST);

        // Barra de recursos disponibles (Tarjeta)
        JPanel cardDisponibles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        cardDisponibles.setBackground(BLANCO_CARD);
        cardDisponibles.setBorder(new CompoundBorder(
                new LineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblDispTitulo = new JLabel("RECURSOS DISPONIBLES (Vector W):");
        lblDispTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDispTitulo.setForeground(VERDE_PRINCIPAL);

        txtR1 = crearInputRecurso("0");
        txtR2 = crearInputRecurso("1");
        txtR3 = crearInputRecurso("1");

        cardDisponibles.add(lblDispTitulo);
        cardDisponibles.add(new JLabel("R1:"));
        cardDisponibles.add(txtR1);
        cardDisponibles.add(new JLabel("R2:"));
        cardDisponibles.add(txtR2);
        cardDisponibles.add(new JLabel("R3:"));
        cardDisponibles.add(txtR3);

        panelNorte.add(panelBanner, BorderLayout.NORTH);
        panelNorte.add(cardDisponibles, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // --- 2. MATRICES (TABLAS) ---
        JPanel panelTablas = new JPanel(new GridLayout(1, 3, 15, 0));
        panelTablas.setBackground(FONDO_APP);
        panelTablas.setBorder(new EmptyBorder(0, 20, 0, 20));

        String[] columnas = {"Proceso", "R1", "R2", "R3"};

        Object[][] datosMaximo = {
                {"P1", 3, 2, 2},
                {"P2", 6, 1, 3},
                {"P3", 3, 1, 4},
                {"P4", 4, 2, 2}
        };

        Object[][] datosAsignacion = {
                {"P1", 2, 0, 1},
                {"P2", 5, 1, 1},
                {"P3", 2, 1, 1},
                {"P4", 0, 0, 2}
        };

        Object[][] datosNecesidad = {
                {"P1", 1, 2, 1},
                {"P2", 1, 0, 2},
                {"P3", 1, 0, 3},
                {"P4", 4, 2, 0}
        };

        tablaMaximo = crearTablaEstilizada(new DefaultTableModel(datosMaximo, columnas));
        tablaAsignacion = crearTablaEstilizada(new DefaultTableModel(datosAsignacion, columnas));

        DefaultTableModel modNec = new DefaultTableModel(datosNecesidad, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaNecesidad = crearTablaEstilizada(modNec);

        panelTablas.add(crearTarjetaTabla("Demanda Máxima (Max)", tablaMaximo));
        panelTablas.add(crearTarjetaTabla("En Uso (Allocation)", tablaAsignacion));
        panelTablas.add(crearTarjetaTabla("Necesidad (Max - Alloc)", tablaNecesidad));

        add(panelTablas, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR: ACCIONES, BADGE Y AUDITORÍA ---
        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.setBackground(FONDO_APP);
        panelSur.setBorder(new EmptyBorder(5, 20, 15, 20));

        // Botones de acción con contraste visual
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(FONDO_APP);

        JButton btnCalcular = crearBoton("1. Calcular Necesidad", VERDE_MEDIO, Color.WHITE);
        JButton btnEjecutar = crearBoton("▶ 2. Evaluar Estado del Sistema", AMARILLO_ACCENT, Color.WHITE);
        JButton btnRestaurar = crearBoton("↺ Restablecer Valores", new Color(100, 116, 139), Color.WHITE);

        panelBotones.add(btnCalcular);
        panelBotones.add(btnEjecutar);
        panelBotones.add(btnRestaurar);

        // Estado del sistema
        JPanel bannerResultado = new JPanel(new BorderLayout(12, 0));
        bannerResultado.setBackground(BLANCO_CARD);
        bannerResultado.setBorder(new CompoundBorder(
                new LineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        lblBadgeEstado = new JLabel("EN ESPERA", SwingConstants.CENTER);
        lblBadgeEstado.setOpaque(true);
        lblBadgeEstado.setBackground(new Color(226, 232, 240));
        lblBadgeEstado.setForeground(COLOR_TEXTO);
        lblBadgeEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadgeEstado.setPreferredSize(new Dimension(170, 32));

        lblDetalleResultado = new JLabel("Presione 'Evaluar Estado del Sistema' para ejecutar la verificación de seguridad.", SwingConstants.LEFT);
        lblDetalleResultado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetalleResultado.setForeground(COLOR_TEXTO);

        bannerResultado.add(lblBadgeEstado, BorderLayout.WEST);
        bannerResultado.add(lblDetalleResultado, BorderLayout.CENTER);

        // Terminal de auditoría
        txtLog = new JTextArea(7, 30);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(17, 24, 39));
        txtLog.setForeground(new Color(243, 244, 246));
        txtLog.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(new LineBorder(BORDE_SUAVE, 1));

        JPanel panelControles = new JPanel(new BorderLayout(8, 8));
        panelControles.setBackground(FONDO_APP);
        panelControles.add(panelBotones, BorderLayout.NORTH);
        panelControles.add(bannerResultado, BorderLayout.SOUTH);

        panelSur.add(panelControles, BorderLayout.NORTH);
        panelSur.add(scrollLog, BorderLayout.CENTER);

        add(panelSur, BorderLayout.SOUTH);

        // Acciones
        btnCalcular.addActionListener(e -> calcularMatrizNecesidad());
        btnEjecutar.addActionListener(e -> {
            calcularMatrizNecesidad();
            ejecutarDeteccion();
        });
        btnRestaurar.addActionListener(e -> reiniciarValoresPorDefecto());
    }

    private JPanel crearTarjetaTabla(String titulo, JTable tabla) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLANCO_CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(VERDE_PRINCIPAL);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(new LineBorder(BORDE_SUAVE, 1));
        sp.getViewport().setBackground(BLANCO_CARD);

        p.add(lbl, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JTable crearTablaEstilizada(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(230, 235, 230));
        table.setSelectionBackground(new Color(209, 250, 229));
        table.setSelectionForeground(COLOR_TEXTO);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(VERDE_PRINCIPAL);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    private JTextField crearInputRecurso(String def) {
        JTextField tf = new JTextField(def, 3);
        tf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }

    private JButton crearBoton(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void calcularMatrizNecesidad() {
        try {
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j <= 3; j++) {
                    int max = Integer.parseInt(tablaMaximo.getValueAt(i, j).toString().trim());
                    int alloc = Integer.parseInt(tablaAsignacion.getValueAt(i, j).toString().trim());
                    if (alloc > max) {
                        JOptionPane.showMessageDialog(this,
                                "Error en P" + (i + 1) + ": Los recursos asignados superan la demanda máxima.",
                                "Inconsistencia de Datos", JOptionPane.WARNING_MESSAGE);
                    }
                    tablaNecesidad.setValueAt(max - alloc, i, j);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Asegúrese de ingresar solo números en las celdas.",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarDeteccion() {
        txtLog.setText("");
        try {
            int numP = 4;
            int numR = 3;

            int[] disp = new int[]{
                    Integer.parseInt(txtR1.getText().trim()),
                    Integer.parseInt(txtR2.getText().trim()),
                    Integer.parseInt(txtR3.getText().trim())
            };

            int[][] alloc = new int[numP][numR];
            int[][] need = new int[numP][numR];

            for (int i = 0; i < numP; i++) {
                for (int j = 0; j < numR; j++) {
                    alloc[i][j] = Integer.parseInt(tablaAsignacion.getValueAt(i, j + 1).toString().trim());
                    need[i][j] = Integer.parseInt(tablaNecesidad.getValueAt(i, j + 1).toString().trim());
                }
            }

            boolean[] finalizado = new boolean[numP];
            int[] trabajo = Arrays.copyOf(disp, numR);
            int[] secuencia = new int[numP];
            int paso = 0;

            txtLog.append("--- INICIO DE AUDITORÍA: ALGORITMO DEL BANQUERO ---\n");
            txtLog.append("Vector Disponible Inicial (W): " + Arrays.toString(trabajo) + "\n\n");

            while (paso < numP) {
                boolean asignadoEnRonda = false;

                for (int p = 0; p < numP; p++) {
                    if (!finalizado[p]) {
                        boolean puede = true;
                        for (int r = 0; r < numR; r++) {
                            if (need[p][r] > trabajo[r]) {
                                puede = false;
                                break;
                            }
                        }

                        if (puede) {
                            for (int r = 0; r < numR; r++) {
                                trabajo[r] += alloc[p][r];
                            }
                            secuencia[paso++] = p;
                            finalizado[p] = true;
                            asignadoEnRonda = true;

                            txtLog.append(String.format("[✓ SEGURO] Proceso P%d satisface Necesidad %s <= W. Finaliza y libera recursos.\n",
                                    (p + 1), Arrays.toString(need[p])));
                            txtLog.append(String.format("          -> Vector Disponible resultante (W): %s\n", Arrays.toString(trabajo)));
                        } else {
                            txtLog.append(String.format("[x ESPERA] Proceso P%d NO puede avanzar. Necesidad %s excede W %s\n",
                                    (p + 1), Arrays.toString(need[p]), Arrays.toString(trabajo)));
                        }
                    }
                }

                if (!asignadoEnRonda) {
                    txtLog.append("\n=================================================================\n");
                    txtLog.append("BLOQUEO MUTUO DETECTADO: Ningún proceso restante puede satisfacer su necesidad.\n");
                    txtLog.append("ESTADO: INSEGURO (Riesgo inminente de Deadlock).\n");
                    txtLog.append("=================================================================\n");

                    lblBadgeEstado.setText("INTERBLOQUEO DETECTADO");
                    lblBadgeEstado.setBackground(COLOR_DANGER);
                    lblBadgeEstado.setForeground(Color.WHITE);
                    lblDetalleResultado.setText("Estado Inseguro: Con los recursos [0, 1, 1] ningún proceso puede completar su ejecución.");
                    return;
                }
            }

            StringBuilder seqTxt = new StringBuilder();
            for (int i = 0; i < numP; i++) {
                seqTxt.append("P").append(secuencia[i] + 1).append(i < numP - 1 ? " → " : "");
            }

            txtLog.append("\n=================================================================\n");
            txtLog.append("ESTADO SEGURO: Se encontró una secuencia válida de ejecución.\n");
            txtLog.append("SECUENCIA: " + seqTxt + "\n");
            txtLog.append("=================================================================\n");

            lblBadgeEstado.setText("ESTADO SEGURO");
            lblBadgeEstado.setBackground(COLOR_SAFE);
            lblBadgeEstado.setForeground(Color.WHITE);
            lblDetalleResultado.setText("Secuencia segura garantizada: " + seqTxt);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Revise los campos numéricos ingresados: " + ex.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reiniciarValoresPorDefecto() {
        txtR1.setText("0");
        txtR2.setText("1");
        txtR3.setText("1");

        int[][] defMax = {{3, 2, 2}, {6, 1, 3}, {3, 1, 4}, {4, 2, 2}};
        int[][] defAlloc = {{2, 0, 1}, {5, 1, 1}, {2, 1, 1}, {0, 0, 2}};

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                tablaMaximo.setValueAt(defMax[i][j], i, j + 1);
                tablaAsignacion.setValueAt(defAlloc[i][j], i, j + 1);
            }
        }
        calcularMatrizNecesidad();
        lblBadgeEstado.setText("EN ESPERA");
        lblBadgeEstado.setBackground(new Color(226, 232, 240));
        lblBadgeEstado.setForeground(COLOR_TEXTO);
        lblDetalleResultado.setText("Valores restablecidos al caso base.");
        txtLog.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BanqueroGUI().setVisible(true));
    }
}