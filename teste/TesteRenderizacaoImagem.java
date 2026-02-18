package teste;
import logica.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TesteRenderizacaoImagem {

    public static void main(String[] args) {
        System.out.println("=== Iniciando Teste de Renderização 3D (Exportação PNG) ===");

        // 1. Configuração da Tela
        int largura = 1600; // Reduzi um pouco para ser mais rápido, mas pode usar 1600
        int altura = 900;

        // Cena: width, height, luz_ambiente (ia), modo_shader (2=Phong), cor_flat, material
        // Modo 2 (Phong) para usar as luzes e normais
        Cena cena = new Cena(largura, altura, new double[]{0.5, 0.5, 0.5}, (byte) 2, 50, null);

        // 2. Criar um Cubo
        // Centro (0,0,0), Lado 20
        // Materiais: ka (amb), kd (dif), ks (spec + shininess)
        Cubo cubo = new Cubo(
            new double[]{0, 0, 0}, 
            20.0, 
            new double[]{0.2, 0.2, 0.2}, 
            new double[]{0.2, 0.2, 0.2}, 
            new double[]{1, 1, 1, 50.0}
        );


        // Aplicar transformações para ver o cubo em ângulo
        // Rotaciona 45 graus em X e 45 em Y para ver um vértice apontando pra gente
        //double[][] rotX = Mat4.rotate_y(0, cubo.get_centroide());
       // double[][] rot = Mat4.rotate_y(0, cubo.get_centroide());
       // double[][] rot2 = Mat4.rotate_x(0, cubo.get_centroide());
        //double[][] trans = Mat4.trans(0,0, 0);

        // Combina as rotações

        //double [][]transform = Mat4.mul(rot, rot2);

//        cubo.aplicar_transformacao(transform);

        cena.adicionar_objeto(cubo);

        // 3. Configurar Câmera
        // Posicionada em Z = -40, olhando para Z positivo (0,0,0)
        Camera camera = new Camera(
            new double[]{40, 40, 40}, // VRP
            new double[]{0, 0, 0},   // PRP
            new double[]{0, 0, 1},   // VPN (Olhando para o eixo Z positivo)
            new double[]{0, 1, 0},   // VUP
            new double[]{0, 0, 0},   // P (Ponto de foco)
            new double[]{0, 1, 0},   // Y (View Up Vector)
            15, -15, 15, -15,        // Janela (Viewplane) pequena para dar "zoom" no objeto
            20,                      // DP (Distância de projeção)
            1,                       // Near
            200,                     // Far
            altura, largura          // Vres, Hres (Invertido no construtor da sua classe Camera?) 
                                     // *Nota: Verifique se sua Camera pede Vres, Hres ou Hres, Vres.
                                     // No código anterior era Vres, Hres.
        );
        cena.definir_camera(camera);

        // 4. Adicionar Luz
        // Luz direcional ou pontual vindo da direita/cima
        Luz luz = new Luz(
            new double[]{40, 40, 40}, // Posição da luz
            new double[]{0.2, 0.2, 0.2},     // Intensidade difusa (Branca)
            new double[]{0.5, 0.5, 0.5},     // Intensidade especular
            (byte) 0                   // Tipo (assumindo 0 ou 1)
        );
        cena.adicionar_luz(luz);

        // 5. Renderizar
        System.out.println("Renderizando frame...");
        long inicio = System.currentTimeMillis();
        cena.renderizar();
        long fim = System.currentTimeMillis();
        System.out.println("Tempo de renderização: " + (fim - inicio) + "ms");

        // 6. Analisar e Exportar
        exportarParaImagem(cena, "cubo_renderizado.png");
    }

    public static void exportarParaImagem(Cena cena, String nomeArquivo) {
        int width = cena.width;
        int height = cena.height;
        int pixelsColoridos = 0;

        // BufferedImage é a classe do Java para manipular imagens em memória
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int corARGB = cena.color_buffer[x][y];

                // Verifica se não é o fundo (assumindo fundo 0 ou preto transparente)
                // Se sua classe Cena inicializa com 0 (preto transparente), isso funciona.
                // Mas se o alpha for 0, pode ser invisível.
                // Vamos checar se é diferente de 0 absoluto.
                if (corARGB != 0) {
                    pixelsColoridos++;
                }

                // O Java BufferedImage tem origem (0,0) no topo-esquerdo.
                // Motores 3D matemáticos geralmente tem Y=0 embaixo.
                // Se sua imagem sair de cabeça para baixo, descomente a linha do "yInvertido".
                
                // int yFinal = y; 
                int yFinal = y; // Inverter Y se necessário (comum em 3D)

                image.setRGB(x, yFinal, corARGB);
            }
        }

        System.out.println("-" .repeat(40));
        if (pixelsColoridos > 0) {
            System.out.println("SUCESSO! Pixels desenhados: " + pixelsColoridos);
            try {
                File outputfile = new File(nomeArquivo);
                ImageIO.write(image, "png", outputfile);
                System.out.println("Imagem salva com sucesso em: " + outputfile.getAbsolutePath());
                System.out.println("Abra o arquivo para ver o cubo 3D.");
            } catch (IOException e) {
                System.err.println("Erro ao salvar imagem: " + e.getMessage());
            }
        } else {
            System.out.println("ERRO: O buffer está vazio (tela preta).");
            System.out.println("Dicas de debug:");
            System.out.println("1. O objeto está dentro do frustum da câmera? (Near/Far/FOV)");
            System.out.println("2. As luzes estão iluminando o objeto?");
            System.out.println("3. As normais do cubo estão apontando para fora?");
        }
        System.out.println("-" .repeat(40));
    }
}