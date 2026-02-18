package ui;

import logica.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;



// =====================================================================
// JANELA FLUTUANTE: Editar Materiais do Cubo
// =====================================================================
class JanelaMateriais extends JDialog {

    private final JSlider sliderKaR, sliderKaG, sliderKaB;
    private final JSlider sliderKdR, sliderKdG, sliderKdB;
    private final JSlider sliderKsR, sliderKsG, sliderKsB;

    public interface MaterialCallback {
        void onAplicar(Map<String, double[]> materiais);
    }

    public JanelaMateriais(Frame parent, MaterialCallback callback, Map<String, double[]> materiaisIniciais, Runnable onClose) {
        super(parent, "Editar Materiais", false);
        setSize(520, 300);
        setResizable(false);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        double[] ka = materiaisIniciais != null ? materiaisIniciais.get("ka") : new double[]{0.2, 0.2, 0.2};
        double[] kd = materiaisIniciais != null ? materiaisIniciais.get("kd") : new double[]{0.8, 0.8, 0.8};
        double[] ks = materiaisIniciais != null ? materiaisIniciais.get("ks") : new double[]{1.0, 1.0, 1.0, 50};

        // Sliders criados com range 0-10 representando 0.0-1.0
        sliderKaR = criarSlider((int)(ka[0] * 10));
        sliderKaG = criarSlider((int)(ka[1] * 10));
        sliderKaB = criarSlider((int)(ka[2] * 10));

        sliderKdR = criarSlider((int)(kd[0] * 10));
        sliderKdG = criarSlider((int)(kd[1] * 10));
        sliderKdB = criarSlider((int)(kd[2] * 10));

        sliderKsR = criarSlider((int)(ks[0] * 10));
        sliderKsG = criarSlider((int)(ks[1] * 10));
        sliderKsB = criarSlider((int)(ks[2] * 10));

        add(criarLinhaRGB("Ka (Ambiente) RGB:", sliderKaR, sliderKaG, sliderKaB));
        add(criarLinhaRGB("Kd (Difuso) RGB:", sliderKdR, sliderKdG, sliderKdB));
        add(criarLinhaRGB("Ks (Especular) RGB:", sliderKsR, sliderKsG, sliderKsB));

        JPanel frameBotoes = new JPanel(new FlowLayout());
        JButton btnOk = new JButton("OK");
        JButton btnCancelar = new JButton("Cancelar");

        btnOk.setPreferredSize(new Dimension(90, 28));
        btnCancelar.setPreferredSize(new Dimension(90, 28));

        btnOk.addActionListener(e -> {
            Map<String, double[]> materiais = new HashMap<>();
            materiais.put("ka", new double[]{sliderKaR.getValue() / 10.0, sliderKaG.getValue() / 10.0, sliderKaB.getValue() / 10.0});
            materiais.put("kd", new double[]{sliderKdR.getValue() / 10.0, sliderKdG.getValue() / 10.0, sliderKdB.getValue() / 10.0});
            materiais.put("ks", new double[]{sliderKsR.getValue() / 10.0, sliderKsG.getValue() / 10.0, sliderKsB.getValue() / 10.0, 50});
            callback.onAplicar(materiais);
            if (onClose != null) onClose.run();
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            if (onClose != null) onClose.run();
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onClose != null) onClose.run();
            }
        });

        frameBotoes.add(btnOk);
        frameBotoes.add(btnCancelar);
        add(frameBotoes);

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JSlider criarSlider(int valorInicial) {
        JSlider s = new JSlider(0, 10, valorInicial);
        s.setMajorTickSpacing(5);
        s.setMinorTickSpacing(1);
        s.setPaintTicks(true);
        return s;
    }

    private JPanel criarLinhaRGB(String titulo, JSlider r, JSlider g, JSlider b) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("R:")); panel.add(r);
        panel.add(new JLabel("G:")); panel.add(g);
        panel.add(new JLabel("B:")); panel.add(b);
        return panel;
    }
}

//intensidades de luz
class JanelaLuz extends JDialog {

    private final JSlider sliderKdR, sliderKdG, sliderKdB;
    private final JSlider sliderKsR, sliderKsG, sliderKsB;
    private final JRadioButton rbDirecional, rbPontual;

    public interface LuzCallback {
        void onAplicar(Map<String, Object> intensidades);
    }

    public JanelaLuz(Frame parent, LuzCallback callback, byte tipoInicial,
                     double[] kdInicial, double[] ksInicial, Runnable onClose) {
        super(parent, "Editar Intensidades da Luz", false);
        setSize(520, 320);
        setResizable(false);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        double[] kd = kdInicial != null ? kdInicial : new double[]{1.0, 1.0, 1.0};
        double[] ks = ksInicial != null ? ksInicial : new double[]{1.0, 1.0, 1.0};

        sliderKdR = criarSlider((int)(kd[0] * 10));
        sliderKdG = criarSlider((int)(kd[1] * 10));
        sliderKdB = criarSlider((int)(kd[2] * 10));

        sliderKsR = criarSlider((int)(ks[0] * 10));
        sliderKsG = criarSlider((int)(ks[1] * 10));
        sliderKsB = criarSlider((int)(ks[2] * 10));

        add(criarLinhaRGB("Intensidade Difusa (Kd) RGB:", sliderKdR, sliderKdG, sliderKdB));
        add(criarLinhaRGB("Intensidade Especular (Ks) RGB:", sliderKsR, sliderKsG, sliderKsB));

        // Tipo de Luz
        JPanel frameTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        frameTipo.setBorder(BorderFactory.createTitledBorder("Tipo de Luz:"));
        rbDirecional = new JRadioButton("Direcional", tipoInicial == 0);
        rbPontual = new JRadioButton("Pontual",  tipoInicial == 1);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDirecional); bg.add(rbPontual);
        frameTipo.add(rbDirecional); frameTipo.add(rbPontual);
        add(frameTipo);

        JPanel frameBotoes = new JPanel(new FlowLayout());
        JButton btnOk = new JButton("OK");
        JButton btnCancelar = new JButton("Cancelar");
        btnOk.setPreferredSize(new Dimension(90, 28));
        btnCancelar.setPreferredSize(new Dimension(90, 28));

        btnOk.addActionListener(e -> {
            Map<String, Object> intensidades = new HashMap<>();
            intensidades.put("tipo", (byte)(rbPontual.isSelected() ? 1 : 0));
            intensidades.put("kd", new double[]{sliderKdR.getValue() / 10.0, sliderKdG.getValue() / 10.0, sliderKdB.getValue() / 10.0});
            intensidades.put("ks", new double[]{sliderKsR.getValue() / 10.0, sliderKsG.getValue() / 10.0, sliderKsB.getValue() / 10.0});
            callback.onAplicar(intensidades);
            if (onClose != null) onClose.run();
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            if (onClose != null) onClose.run();
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onClose != null) onClose.run();
            }
        });

        frameBotoes.add(btnOk);
        frameBotoes.add(btnCancelar);
        add(frameBotoes);

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JSlider criarSlider(int valorInicial) {
        JSlider s = new JSlider(0, 10, valorInicial);
        s.setMajorTickSpacing(5);
        s.setMinorTickSpacing(1);
        s.setPaintTicks(true);
        return s;
    }

    private JPanel criarLinhaRGB(String titulo, JSlider r, JSlider g, JSlider b) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("R:")); panel.add(r);
        panel.add(new JLabel("G:")); panel.add(g);
        panel.add(new JLabel("B:")); panel.add(b);
        return panel;
    }
}


public class InterfaceModelador extends JFrame {

    // canvas de renderização 
    private final JPanel canvasPanel;
    private BufferedImage imagemCanvas;

    // painel de controles com rolagem
    private final JPanel painelControles;

    // campos de coordenadas 
    private JTextField entryX, entryY, entryZ;

    // lista de objetos 
    private DefaultListModel<String> listModel;
    private JList<String> listaObjetos;

    // campos da câmera 
    private JTextField entryVrp, entryPrp, entryVpn, entryVup, entryP, entryYCam;
    private JTextField entryUMin, entryUMax, entryVMin, entryVMax, entryDp, entryNear, entryFar;

    // shader 
    private JRadioButton rbFlat, rbPhong;
    private int modoShader = 2; // 1=Flat, 2=Phong

    // --- Estado ---
    private Cena cena;
    private Camera camera;
    private Cubo cuboSelecionado = null;
    private JanelaLuz janelaLuz = null;
    private JanelaMateriais janelaMateriais = null;

    private byte tipoLuz = 0; 

    private Map<String, double[]> materiaisCubo = new HashMap<>();
    private Map<String, double[]> intensidadeLuz = new HashMap<>();

    private final Map<String, Object> cameraConfig = new HashMap<>();

    public InterfaceModelador() {
        super("Modelador 3D - Pipeline Alvy-Ray-Smith");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setResizable(false);
        setLayout(new BorderLayout());


        materiaisCubo.put("ka", new double[]{0.2, 0.2, 0.2});
        materiaisCubo.put("kd", new double[]{0.8, 0.8, 0.8});
        materiaisCubo.put("ks", new double[]{1.0, 1.0, 1.0, 50});

        intensidadeLuz.put("kd", new double[]{1.0, 1.0, 1.0});
        intensidadeLuz.put("ks", new double[]{1.0, 1.0, 1.0});

        cameraConfig.put("vrp", new double[]{40, 40, 40});
        cameraConfig.put("prp", new double[]{0, 0, 0});
        cameraConfig.put("vpn", new double[]{0, 0, 1});
        cameraConfig.put("vup", new double[]{0, 1, 0});
        cameraConfig.put("P",   new double[]{0, 0, 0});
        cameraConfig.put("Y",   new double[]{0, 1, 0});
        cameraConfig.put("u_min", -141.0);
        cameraConfig.put("u_max",  141.0);
        cameraConfig.put("v_min", -100.0);
        cameraConfig.put("v_max",  100.0);
        cameraConfig.put("DP",   100.0);
        cameraConfig.put("near",   1.0);
        cameraConfig.put("far",  200.0);

        // --- Canvas de renderização ---
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemCanvas != null) {
                    g.drawImage(imagemCanvas, 0, 0, null);
                }
            }
        };
        canvasPanel.setBackground(Color.BLACK);
        add(canvasPanel, BorderLayout.CENTER);

        // --- Painel de controles scrollável ---
        painelControles = new JPanel();
        painelControles.setBackground(Color.LIGHT_GRAY);
        painelControles.setLayout(new BoxLayout(painelControles, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(painelControles,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(360, 600));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.EAST);

        criarPainelControles();

        // --- Inicializa Cena e Câmera após o layout ---
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            int larguraCanvas = canvasPanel.getWidth();
            int alturaCanvas  = canvasPanel.getHeight();
            if (larguraCanvas <= 1 || alturaCanvas <= 1) {
                larguraCanvas = 1200 - 360;
                alturaCanvas  = 600;
            }

            cena = new Cena(larguraCanvas, alturaCanvas,
                    new double[]{0.1, 0.1, 0.1}, (byte) 2, 0, new double[]{1, 1, 1});

            camera = criarCamera(larguraCanvas, alturaCanvas);
            cena.definir_camera(camera);
            atualizarCena();
        });

        configurarAtalhos();
    }

    private Camera criarCamera(int hres, int vres) {
        return new Camera(
                (double[]) cameraConfig.get("vrp"),
                (double[]) cameraConfig.get("prp"),
                (double[]) cameraConfig.get("vpn"),
                (double[]) cameraConfig.get("vup"),
                (double[]) cameraConfig.get("P"),
                (double[]) cameraConfig.get("Y"),
                (double) cameraConfig.get("u_max"),
                (double) cameraConfig.get("u_min"),
                (double) cameraConfig.get("v_max"),
                (double) cameraConfig.get("v_min"),
                (double) cameraConfig.get("DP"),
                (double) cameraConfig.get("near"),
                (double) cameraConfig.get("far"),
                vres, hres
        );
    }


    private void configurarAtalhos() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP    -> { if (e.isShiftDown()) moverObjeto(0, 0, -5); else moverObjeto(0, 5, 0); }
                case KeyEvent.VK_DOWN  -> { if (e.isShiftDown()) moverObjeto(0, 0, 5);  else moverObjeto(0, -5, 0); }
                case KeyEvent.VK_LEFT  -> moverObjeto(-5, 0, 0);
                case KeyEvent.VK_RIGHT -> moverObjeto(5, 0, 0);
                case KeyEvent.VK_D     -> rotacionarObjeto("y",  5);
                case KeyEvent.VK_A     -> rotacionarObjeto("y", -5);
                case KeyEvent.VK_S     -> rotacionarObjeto("x",  5);
                case KeyEvent.VK_W     -> rotacionarObjeto("x", -5);
                case KeyEvent.VK_Q     -> rotacionarObjeto("z",  5);
                case KeyEvent.VK_E     -> rotacionarObjeto("z", -5);
            }
            return false;
        });
    }


    private void criarPainelControles() {

        JLabel titulo = new JLabel("Controles do Modelador 3D", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        titulo.setBackground(Color.GRAY);
        titulo.setOpaque(true);
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        painelControles.add(titulo);

        painelControles.add(Box.createVerticalStrut(8));

    
        JPanel frameCoords = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        frameCoords.setBackground(Color.LIGHT_GRAY);
        frameCoords.add(new JLabel("X:")); entryX = new JTextField("0", 5); frameCoords.add(entryX);
        frameCoords.add(new JLabel("Y:")); entryY = new JTextField("0", 5); frameCoords.add(entryY);
        frameCoords.add(new JLabel("Z:")); entryZ = new JTextField("0", 5); frameCoords.add(entryZ);
        painelControles.add(frameCoords);

        painelControles.add(Box.createVerticalStrut(5));


        JPanel frameBotoesCubo = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        frameBotoesCubo.setBackground(Color.LIGHT_GRAY);

        JButton btnAddCubo = new JButton("Adicionar Cubo");
        btnAddCubo.addActionListener(e -> adicionarCubo(parseIntField(entryX), parseIntField(entryY), parseIntField(entryZ)));

        JButton btnEditMat = new JButton("\uD83C\uDFA8"); 
        btnEditMat.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnEditMat.setPreferredSize(new Dimension(36, 28));
        btnEditMat.addActionListener(e -> abrirJanelaMateriais());

        frameBotoesCubo.add(btnAddCubo);
        frameBotoesCubo.add(btnEditMat);
        painelControles.add(frameBotoesCubo);

        painelControles.add(Box.createVerticalStrut(5));


        JPanel frameBotoesLuz = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        frameBotoesLuz.setBackground(Color.LIGHT_GRAY);

        JButton btnAddLuz = new JButton("Adicionar Luz");
        btnAddLuz.addActionListener(e -> adicionarLuz(parseIntField(entryX), parseIntField(entryY), parseIntField(entryZ)));

        JButton btnEditLuz = new JButton("\uD83D\uDCA1"); 
        btnEditLuz.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnEditLuz.setPreferredSize(new Dimension(36, 28));
        btnEditLuz.addActionListener(e -> abrirJanelaLuz());

        frameBotoesLuz.add(btnAddLuz);
        frameBotoesLuz.add(btnEditLuz);
        painelControles.add(frameBotoesLuz);

        painelControles.add(Box.createVerticalStrut(5));


        JButton btnEditCubo = new JButton("Editar Cubo Selecionado");
        btnEditCubo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEditCubo.addActionListener(e -> abrirEdicaoCuboSelecionado());
        painelControles.add(btnEditCubo);

        painelControles.add(Box.createVerticalStrut(5));

        JButton btnLimpar = new JButton("Limpar Tela");
        btnLimpar.setBackground(Color.GRAY);
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLimpar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLimpar.setMaximumSize(new Dimension(200, 30));
        btnLimpar.addActionListener(e -> limparTela());
        painelControles.add(btnLimpar);

        painelControles.add(Box.createVerticalStrut(10));


        JLabel lblObjetos = new JLabel("Objetos na Cena", SwingConstants.CENTER);
        lblObjetos.setFont(new Font("Arial", Font.BOLD, 11));
        lblObjetos.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelControles.add(lblObjetos);

        listModel = new DefaultListModel<>();
        listaObjetos = new JList<>(listModel);
        listaObjetos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaObjetos.setVisibleRowCount(8);
        listaObjetos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarObjetoDaLista();
        });
  
        listaObjetos.setFocusable(false);

        JScrollPane scrollLista = new JScrollPane(listaObjetos);
        scrollLista.setMaximumSize(new Dimension(300, 140));
        scrollLista.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelControles.add(scrollLista);

        painelControles.add(Box.createVerticalStrut(10));


        JLabel lblEscala = new JLabel("Escalar Objeto", SwingConstants.CENTER);
        lblEscala.setFont(new Font("Arial", Font.BOLD, 11));
        lblEscala.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelControles.add(lblEscala);

        JPanel frameEscala = new JPanel(new GridLayout(3, 3, 4, 4));
        frameEscala.setBackground(Color.LIGHT_GRAY);
        frameEscala.setMaximumSize(new Dimension(220, 120));
        frameEscala.add(new JLabel("X", SwingConstants.CENTER));
        frameEscala.add(new JLabel("Y", SwingConstants.CENTER));
        frameEscala.add(new JLabel("Z", SwingConstants.CENTER));
        frameEscala.add(criarBotaoEscala("+", () -> escalaObjeto(1.1, 1.0, 1.0)));
        frameEscala.add(criarBotaoEscala("+", () -> escalaObjeto(1.0, 1.1, 1.0)));
        frameEscala.add(criarBotaoEscala("+", () -> escalaObjeto(1.0, 1.0, 1.1)));
        frameEscala.add(criarBotaoEscala("-", () -> escalaObjeto(0.9, 1.0, 1.0)));
        frameEscala.add(criarBotaoEscala("-", () -> escalaObjeto(1.0, 0.9, 1.0)));
        frameEscala.add(criarBotaoEscala("-", () -> escalaObjeto(1.0, 1.0, 0.9)));
        painelControles.add(frameEscala);

        painelControles.add(Box.createVerticalStrut(10));


        JLabel lblCam = new JLabel("Parâmetros da Câmera", SwingConstants.CENTER);
        lblCam.setFont(new Font("Arial", Font.BOLD, 11));
        lblCam.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelControles.add(lblCam);

        JPanel frameCamera = new JPanel(new GridBagLayout());
        frameCamera.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        entryVrp   = criarCampoCamera(frameCamera, gbc, 0, "VRP",  fmtVec((double[]) cameraConfig.get("vrp")));
        entryPrp   = criarCampoCamera(frameCamera, gbc, 1, "PRP",  fmtVec((double[]) cameraConfig.get("prp")));
        entryVpn   = criarCampoCamera(frameCamera, gbc, 2, "VPN",  fmtVec((double[]) cameraConfig.get("vpn")));
        entryVup   = criarCampoCamera(frameCamera, gbc, 3, "VUP",  fmtVec((double[]) cameraConfig.get("vup")));
        entryP     = criarCampoCamera(frameCamera, gbc, 4, "P",    fmtVec((double[]) cameraConfig.get("P")));
        entryYCam  = criarCampoCamera(frameCamera, gbc, 5, "Y",    fmtVec((double[]) cameraConfig.get("Y")));


        gbc.gridx = 0; gbc.gridy = 6; frameCamera.add(new JLabel("u_min"), gbc);
        gbc.gridx = 1; entryUMin = new JTextField(String.valueOf(cameraConfig.get("u_min")), 7); frameCamera.add(entryUMin, gbc);
        gbc.gridx = 2; frameCamera.add(new JLabel("u_max"), gbc);
        gbc.gridx = 3; entryUMax = new JTextField(String.valueOf(cameraConfig.get("u_max")), 7); frameCamera.add(entryUMax, gbc);

        gbc.gridx = 0; gbc.gridy = 7; frameCamera.add(new JLabel("v_min"), gbc);
        gbc.gridx = 1; entryVMin = new JTextField(String.valueOf(cameraConfig.get("v_min")), 7); frameCamera.add(entryVMin, gbc);
        gbc.gridx = 2; frameCamera.add(new JLabel("v_max"), gbc);
        gbc.gridx = 3; entryVMax = new JTextField(String.valueOf(cameraConfig.get("v_max")), 7); frameCamera.add(entryVMax, gbc);

        gbc.gridx = 0; gbc.gridy = 8; frameCamera.add(new JLabel("DP"), gbc);
        gbc.gridx = 1; entryDp = new JTextField(String.valueOf(cameraConfig.get("DP")), 7); frameCamera.add(entryDp, gbc);
        gbc.gridx = 2; frameCamera.add(new JLabel("near"), gbc);
        gbc.gridx = 3; entryNear = new JTextField(String.valueOf(cameraConfig.get("near")), 7); frameCamera.add(entryNear, gbc);

        gbc.gridx = 0; gbc.gridy = 9; frameCamera.add(new JLabel("far"), gbc);
        gbc.gridx = 1; entryFar = new JTextField(String.valueOf(cameraConfig.get("far")), 7); frameCamera.add(entryFar, gbc);

        painelControles.add(frameCamera);

        painelControles.add(Box.createVerticalStrut(5));

        JButton btnAplicarCamera = new JButton("Aplicar Câmera");
        btnAplicarCamera.setBackground(Color.GRAY);
        btnAplicarCamera.setForeground(Color.WHITE);
        btnAplicarCamera.setFont(new Font("Arial", Font.PLAIN, 11));
        btnAplicarCamera.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAplicarCamera.setMaximumSize(new Dimension(200, 30));
        btnAplicarCamera.addActionListener(e -> aplicarParametrosCamera());
        painelControles.add(btnAplicarCamera);

        painelControles.add(Box.createVerticalStrut(10));


        JLabel lblShader = new JLabel("Modo de Iluminação", SwingConstants.CENTER);
        lblShader.setFont(new Font("Arial", Font.BOLD, 11));
        lblShader.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelControles.add(lblShader);

        JPanel frameShader = new JPanel(new FlowLayout());
        frameShader.setBackground(Color.LIGHT_GRAY);
        rbFlat  = new JRadioButton("Flat");
        rbPhong = new JRadioButton("Phong", true);
        ButtonGroup bgShader = new ButtonGroup();
        bgShader.add(rbFlat); bgShader.add(rbPhong);
        rbFlat.addActionListener(e  -> alterarModoIluminacao(1));
        rbPhong.addActionListener(e -> alterarModoIluminacao(2));
        frameShader.add(rbFlat); frameShader.add(rbPhong);
        painelControles.add(frameShader);

        painelControles.add(Box.createVerticalStrut(10));
    }


    private JTextField criarCampoCamera(JPanel panel, GridBagConstraints gbc, int row, String label, String valor) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        JTextField field = new JTextField(valor, 18);
        panel.add(field, gbc);
        gbc.gridwidth = 1;
        return field;
    }

    private JButton criarBotaoEscala(String texto, Runnable acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusable(false);
        btn.addActionListener(e -> acao.run());
        return btn;
    }

    private String fmtVec(double[] v) {
        return String.format(Locale.US, "%.1f, %.1f, %.1f", v[0], v[1], v[2]);
    }

    private double[] parseVecField(JTextField field, String nome) throws NumberFormatException {
        String texto = field.getText().trim();
        String[] partes = texto.split(",");
        if (partes.length != 3) throw new NumberFormatException(nome + " deve ter 3 valores (ex: 0, 0, 40)");
        return new double[]{Double.parseDouble(partes[0].trim()), Double.parseDouble(partes[1].trim()), Double.parseDouble(partes[2].trim())};
    }

    private int parseIntField(JTextField field) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    public void adicionarCubo(int x, int y, int z) {
        Cubo cubo = new Cubo(
                new double[]{x, y, z},
                20,
                materiaisCubo.get("ka"),
                materiaisCubo.get("kd"),
                materiaisCubo.get("ks")
        );
        cena.adicionar_objeto(cubo);
        atualizarListaObjetos();
        atualizarCena();
    }

    public void adicionarLuz(int x, int y, int z) {
        Luz luz = new Luz(
                new double[]{x, y, z},
                intensidadeLuz.get("kd"),
                intensidadeLuz.get("ks"),
                tipoLuz
        );
        cena.adicionar_luz(luz);
        atualizarCena();
    }

    public void limparTela() {
        cena.objetos.clear();
        cena.luzes.clear();
        listModel.clear();
        cuboSelecionado = null;
        atualizarCena();
    }

    public void moverObjeto(int dx, int dy, int dz) {
        if (cuboSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum cubo selecionado!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double[][] mat = Mat4.trans(dx, dy, dz);
        cuboSelecionado.aplicar_transformacao(mat);
        atualizarListaObjetos();
        atualizarCena();
    }

    public void rotacionarObjeto(String eixo, double tetha) {
        if (cuboSelecionado == null) return;
        double[][] mat = switch (eixo) {
            case "x" -> Mat4.rotate_x(tetha, cuboSelecionado.centroide);
            case "y" -> Mat4.rotate_y(tetha, cuboSelecionado.centroide);
            case "z" -> Mat4.rotate_z(tetha, cuboSelecionado.centroide);
            default  -> null;
        };
        if (mat == null) { JOptionPane.showMessageDialog(this, "Eixo inválido!", "Erro", JOptionPane.ERROR_MESSAGE); return; }
        cuboSelecionado.aplicar_transformacao(mat);
        atualizarListaObjetos();
        atualizarCena();
    }

    public void escalaObjeto(double sx, double sy, double sz) {
        if (cuboSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum cubo selecionado!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double[][] mat = Mat4.scale(sx, sy, sz, cuboSelecionado.centroide);
        cuboSelecionado.aplicar_transformacao(mat);
        atualizarListaObjetos();
        atualizarCena();
    }

    public void alterarModoIluminacao(int modo) {
        modoShader = modo;
        atualizarCena();
    }

    private void abrirJanelaMateriais() {
        if (janelaMateriais != null && janelaMateriais.isVisible()) {
            janelaMateriais.toFront(); return;
        }
        janelaMateriais = new JanelaMateriais(this, materiais -> {
            materiaisCubo = new HashMap<>(materiais);
            JOptionPane.showMessageDialog(this, "Materiais atualizados! Próximo cubo usará essas cores.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }, null, () -> janelaMateriais = null);
    }

    private void abrirEdicaoCuboSelecionado() {
        if (cuboSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cubo na lista primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Map<String, double[]> matAtuais = new HashMap<>();
        matAtuais.put("ka", cuboSelecionado.ka.clone());
        matAtuais.put("kd", cuboSelecionado.kd.clone());
        matAtuais.put("ks", cuboSelecionado.ks.clone());

        new JanelaMateriais(this, materiais -> {
            cuboSelecionado.ka = materiais.get("ka");
            cuboSelecionado.kd = materiais.get("kd");
            cuboSelecionado.ks = materiais.get("ks");
            atualizarCena();
            JOptionPane.showMessageDialog(this, "Material do cubo atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }, matAtuais, null);
    }

    private void abrirJanelaLuz() {
        if (janelaLuz != null && janelaLuz.isVisible()) {
            janelaLuz.toFront(); return;
        }
        janelaLuz = new JanelaLuz(this, intensidades -> {
            tipoLuz = (byte) intensidades.get("tipo");
            intensidadeLuz.put("kd", (double[]) intensidades.get("kd"));
            intensidadeLuz.put("ks", (double[]) intensidades.get("ks"));
            JOptionPane.showMessageDialog(this, "Intensidades da luz atualizadas!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }, tipoLuz, intensidadeLuz.get("kd"), intensidadeLuz.get("ks"), () -> janelaLuz = null);
    }

    private void aplicarParametrosCamera() {
        try {
            double[] vrp = parseVecField(entryVrp, "VRP");
            double[] prp = parseVecField(entryPrp, "PRP");
            double[] vpn = parseVecField(entryVpn, "VPN");
            double[] vup = parseVecField(entryVup, "VUP");
            double[] p   = parseVecField(entryP,   "P");
            double[] y   = parseVecField(entryYCam, "Y");

            double uMin = Double.parseDouble(entryUMin.getText().trim());
            double uMax = Double.parseDouble(entryUMax.getText().trim());
            double vMin = Double.parseDouble(entryVMin.getText().trim());
            double vMax = Double.parseDouble(entryVMax.getText().trim());
            double dp   = Double.parseDouble(entryDp.getText().trim());
            double near = Double.parseDouble(entryNear.getText().trim());
            double far  = Double.parseDouble(entryFar.getText().trim());

            if (near <= 0 || far <= 0 || near >= far) {
                JOptionPane.showMessageDialog(this, "Parâmetros inválidos: near deve ser menor que far e ambos > 0.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            cameraConfig.put("vrp", vrp); cameraConfig.put("prp", prp);
            cameraConfig.put("vpn", vpn); cameraConfig.put("vup", vup);
            cameraConfig.put("P", p);     cameraConfig.put("Y", y);
            cameraConfig.put("u_min", uMin); cameraConfig.put("u_max", uMax);
            cameraConfig.put("v_min", vMin); cameraConfig.put("v_max", vMax);
            cameraConfig.put("DP", dp);   cameraConfig.put("near", near); cameraConfig.put("far", far);

            camera = criarCamera(camera.Hres, camera.Vres);
            cena.definir_camera(camera);
            atualizarCena();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void atualizarListaObjetos() {
        listModel.clear();
        for (int i = 0; i < cena.objetos.size(); i++) {
            double[] c = cena.objetos.get(i).centroide;
            listModel.addElement(String.format("Cubo %d - Pos: (%.1f, %.1f, %.1f)", i + 1, c[0], c[1], c[2]));
        }
    }

    private void selecionarObjetoDaLista() {
        int idx = listaObjetos.getSelectedIndex();
        if (idx >= 0 && idx < cena.objetos.size()) {
            cuboSelecionado = cena.objetos.get(idx);
            System.out.println("Cubo " + (idx + 1) + " selecionado");
        }
    }


    public void atualizarCena() {
        if (cena == null) return;
        cena.modo_shader = (byte) modoShader;
        cena.renderizar();
        mostrarCenaNoCanvas();
    }

    private void mostrarCenaNoCanvas() {
        int largura = cena.width;
        int altura  = cena.height;

        imagemCanvas = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                imagemCanvas.setRGB(x, y, cena.color_buffer[x][y]);
            }
        }

        canvasPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceModelador::new);
    }
}