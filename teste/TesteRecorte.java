package teste;
import logica.Cena;
import java.util.Arrays;

public class TesteRecorte {

    public static void printPoly(String label, double[][] vertices) {
        System.out.println(label + " (" + vertices.length + " vértices):");
        for (int i = 0; i < vertices.length; i++) {
            // Imprime X, Y, Z, W para facilitar a leitura
            System.out.printf("  v%d: [x=%.2f, y=%.2f, z=%.2f, w=%.2f]\n", 
                i, vertices[i][0], vertices[i][1], vertices[i][2], vertices[i][3]);
        }
        System.out.println("--------------------------------------------------");
    }

    public static void main(String[] args) {
        // 1. Instanciar Cena (Parâmetros dummy, pois só queremos usar o método recorteSH)
        // O tamanho e luz não importam para o teste de recorte matemático
        Cena cena = new Cena(800, 600, new double[]{0,0,0}, (byte)1, 0, null);

        System.out.println("=== TESTE DE RECORTE (SUTHERLAND-HODGMAN) ===\n");

        // O formato esperado pelo seu código é: {x, y, z, w, nx, ny, nz}
        // w define o limite do frustum (clip space: -w <= coord <= w)

        // -----------------------------------------------------------------
        // CASO 1: Triângulo Totalmente DENTRO
        // w = 10. Limites: [-10, 10]
        // Vértices entre 0 e 5.
        // -----------------------------------------------------------------
        double[][] trianguloDentro = {
            {0, 0, 0, 10,  0,0,1}, // x=0
            {5, 0, 0, 10,  0,0,1}, // x=5
            {0, 5, 0, 10,  0,0,1}  // y=5
        };

        double[][] resultado1 = cena.recorteSH(trianguloDentro);
        printPoly("CASO 1: Triângulo Totalmente DENTRO (Esperado: 3 vértices inalterados)", resultado1);


        // -----------------------------------------------------------------
        // CASO 2: Triângulo Cruzando o Plano RIGHT (x <= w)
        // w = 10. Limite x <= 10.
        // Um ponto está em x=20 (fora).
        // Isso deve cortar o triângulo e gerar um quadrilátero (4 vértices).
        // -----------------------------------------------------------------
        double[][] trianguloCruzandoRight = {
            {0,  0, 0, 10,  0,0,1}, // Dentro
            {0, 10, 0, 10,  0,0,1}, // No limite
            {20, 5, 0, 10,  0,0,1}  // FORA (x=20 > w=10)
        };

        double[][] resultado2 = cena.recorteSH(trianguloCruzandoRight);
        printPoly("CASO 2: Cruzando plano RIGHT (Esperado: 4 vértices / Quadrilátero)", resultado2);


        // -----------------------------------------------------------------
        // CASO 3: Triângulo Totalmente FORA (Plano LEFT: x >= -w)
        // w = 10. Limite x >= -10.
        // Todos os pontos em x = -20.
        // -----------------------------------------------------------------
        double[][] trianguloFora = {
            {-20, 0, 0, 10,  0,0,1},
            {-20, 5, 0, 10,  0,0,1},
            {-25, 2, 0, 10,  0,0,1}
        };

        double[][] resultado3 = cena.recorteSH(trianguloFora);
        printPoly("CASO 3: Totalmente FORA (Esperado: 0 vértices)", resultado3);


        // -----------------------------------------------------------------
        // CASO 4: Cruzando o Plano NEAR (z >= -w)
        // O plano Near no seu código é {0, 0, 1, 1} -> z + w >= 0 -> z >= -w
        // w = 10. Limite z >= -10.
        // Um ponto muito negativo em Z (-30) deve ser cortado.
        // -----------------------------------------------------------------
        double[][] trianguloCruzandoNear = {
            {0, 0,   0, 10,  0,0,1}, // z=0 (Dentro)
            {5, 0,   0, 10,  0,0,1}, // z=0 (Dentro)
            {2, 5, -30, 10,  0,0,1}  // z=-30 (FORA, pois -30 < -10)
        };

        double[][] resultado4 = cena.recorteSH(trianguloCruzandoNear);
        printPoly("CASO 4: Cruzando plano NEAR (Esperado: 4 vértices)", resultado4);
    }
}