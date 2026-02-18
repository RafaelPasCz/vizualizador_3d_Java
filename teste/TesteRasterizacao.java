package teste;
import logica.*;
import java.util.Arrays;

public class TesteRasterizacao {

    public static void main(String[] args) {
        // 1. Cria uma cena pequena (20x20 pixels) para visualização no console
        int w = 20;
        int h = 20;
        Cena cena = new Cena(w, h, new double[]{0.5, 0.5, 0.5}, (byte)1, 0, null);

        // 2. Define 3 vértices de tela manualmente (já projetados)
        // Triângulo no meio da tela
        // Formato: x, y, z, cor, normal[]
        VerticeTela v1 = new VerticeTela(10, 2, 0.5, 0);   // Topo
        VerticeTela v2 = new VerticeTela(2, 18, 0.5, 0);   // Inferior Esquerdo
        VerticeTela v3 = new VerticeTela(18, 18, 0.5, 0);  // Inferior Direito

        VerticeTela[] triangulo = {v1, v2, v3};

        // 3. Define uma cor para o teste (Branco = inteiro gerado por RGB 255,255,255)
        int corBranca = cena.converterRGBInt(255, 255, 255);
        
        // Objeto dummy apenas para não passar null, caso o shader precise
        // ka, kd, ks
        Cubo cuboDummy = new Cubo(new double[]{0,0,0}, 1, new double[]{1,1,1}, new double[]{1,1,1}, new double[]{1,1,1,1});

        System.out.println("=== TESTE DE RASTERIZAÇÃO (SCANLINE) ===\n");
        
        // Chama o rasterizador diretamente
        cena.rasterizarFace(triangulo, (byte)1, corBranca, cuboDummy);

        // 4. Visualizar o Color Buffer no Console
        // Onde tiver cor (!= 0), desenhamos '#', onde for fundo, '.'
        
        for (int y = 0; y < h; y++) {
            System.out.printf("%02d | ", y); // Número da linha
            for (int x = 0; x < w; x++) {
                // Java tem origem (0,0) no topo-esquerdo para arrays, 
                // mas seu rasterizador pode estar usando Y para cima ou para baixo.
                // Vamos imprimir direto da memória.
                
                if (cena.color_buffer[x][y] != 0) {
                    System.out.print("# ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("     ----------------------------------------");
        System.out.print("     ");
        for(int i=0; i<w; i++) System.out.print((i%10) + " "); // Régua X
        System.out.println();
    }
}