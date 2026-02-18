import ui.InterfaceModelador;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // É uma boa prática iniciar aplicações Swing na Event Dispatch Thread (EDT)
        // para evitar problemas de concorrência na interface gráfica.
        SwingUtilities.invokeLater(() -> {
            try {
                // Instancia a interface gráfica principal
                new InterfaceModelador();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro fatal ao iniciar a aplicação: " + e.getMessage());
            }
        });
    }
}