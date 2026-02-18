package teste;
import logica.*;
import java.util.Arrays;

public class TesteTrans {

    // Método auxiliar para imprimir vértices (simula cubo.print_vertices())
    public static void printVertices(Cubo cubo) {
        for (int i = 0; i < cubo.vertices_modelo_transformados.length; i++) {
            System.out.println("V" + i + ": " + Arrays.toString(cubo.vertices_modelo_transformados[i]));
        }
    }

    // Método auxiliar para imprimir matrizes
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        // Inicialização do Cubo
        // v0=(0,0,0), lado=1, ka, kd, ks
        Cubo cubo = new Cubo(
            new double[]{0, 0, 0}, 
            1.0, 
            new double[]{1, 1, 1}, 
            new double[]{1, 1, 1}, 
            new double[]{1, 1, 1, 10}
        );

        System.out.println("========== Cubo original ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Rotação Y ---
        double[][] R = Mat4.rotate_y(0, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo após rotação (y) ==========");
        printVertices(cubo);
        System.out.println("====================");

        

        
        /* 
        // --- Translação X ---
        double[][] T = Mat4.trans(2, 0, 0);
        cubo.aplicar_transformacao(T);

        System.out.println("========== Cubo após translação (x) ==========");
        printVertices(cubo);
        System.out.println("====================");

        T = Mat4.trans(-2, 0, 0);
        cubo.aplicar_transformacao(T);

        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Translação Y ---
        T = Mat4.trans(0, 2, 0);
        cubo.aplicar_transformacao(T);
        System.out.println("========== Cubo após translação (y) ==========");
        printVertices(cubo);
        System.out.println("====================");

        T = Mat4.trans(0, -2, 0);
        cubo.aplicar_transformacao(T);

        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Translação Z ---
        T = Mat4.trans(0, 0, 2);
        cubo.aplicar_transformacao(T);
        System.out.println("========== Cubo após translação (z) ==========");
        printVertices(cubo);
        System.out.println("====================");

        T = Mat4.trans(0, 0, -2);
        cubo.aplicar_transformacao(T);

        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Rotação X ---
        // Nota: Em Java, Mat4.rotate exige o centroide
        double[][] R = Mat4.rotate_x(45, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo após rotação (x) ==========");
        printVertices(cubo);
        System.out.println("====================");

        R = Mat4.rotate_x(-45, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Rotação Y ---
        R = Mat4.rotate_y(0, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo após rotação (y) ==========");
        printVertices(cubo);
        System.out.println("====================");

        R = Mat4.rotate_y(0, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Rotação Z ---
        R = Mat4.rotate_z(45, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo após rotação (z) ==========");
        printVertices(cubo);
        System.out.println("====================");

        R = Mat4.rotate_z(-45, cubo.get_centroide());
        cubo.aplicar_transformacao(R);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Escala X ---
        double[][] S = Mat4.scale(2, 1, 1, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo após escala(x) ==========");
        printVertices(cubo);
        System.out.println("====================");

        S = Mat4.scale(0.5, 1, 1, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Escala Y ---
        S = Mat4.scale(1, 2, 1, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo após escala(y) ==========");
        printVertices(cubo);
        System.out.println("====================");

        S = Mat4.scale(1, 0.5, 1, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Escala Z ---
        S = Mat4.scale(1, 1, 2, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo após escala(z) ==========");
        printVertices(cubo);
        System.out.println("====================");

        S = Mat4.scale(1, 1, 0.5, cubo.get_centroide());
        cubo.aplicar_transformacao(S);
        System.out.println("========== Cubo revertido  ==========");
        printVertices(cubo);
        System.out.println("====================");

        // --- Matriz Composta ---
        // Nota: Centroide estático (0,0,0) usado aqui para simular a lógica do python se necessário,
        // ou usamos cubo.get_centroide() se quisermos transformar em torno do objeto atual.
        // O Python usava transformações genéricas, aqui usarei o centroide atual.
        
        T = Mat4.trans(2, 0, 0);
        R = Mat4.rotate_y(45, cubo.get_centroide()); 
        S = Mat4.scale(2, 2, 2, cubo.get_centroide());

        // M = T * (R * S)
        double[][] M = Mat4.mul(T, Mat4.mul(R, S));

        System.out.println("========== Teste matriz composta  ==========");
        printMatrix(M);
        System.out.println("====================");*/
    }
}