package teste;
import logica.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TestePipeline {

    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        // =========================================================
        // 1. Criar cubo em MODEL SPACE
        // =========================================================
        Cubo cubo = new Cubo(
            new double[]{0, 0, 0}, 
            10, 
            new double[]{1, 1, 1}, 
            new double[]{1, 1, 1}, 
            new double[]{1, 1, 1, 10}
        );

        System.out.println("\n===== MODEL SPACE (CUBO) =====");
        for (int i = 0; i < cubo.vertices_modelo_transformados.length; i++) {
            System.out.println("V" + i + ": " + Arrays.toString(cubo.vertices_modelo_transformados[i]));
        }

        // =========================================================
        // 2. Criar câmera
        // =========================================================
        Camera camera = new Camera(
            new double[]{0, 0, 25}, // VRP
            new double[]{0, 0, 0},   // PRP
            new double[]{0, 0, 1},   // VPN
            new double[]{0, 1, 0},   // VUP
            new double[]{0, 0, 0},   // P (Ponto observado)
            new double[]{0, 1, 0},   // Y (View-Up)
            400, -400,               // u_max, u_min
            300, -300,               // v_max, v_min
            20,                      // DP
            10,                      // near
            100,                     // far
            600, 900                 // Vres, Hres
        );

        // Instanciando Cena apenas para pegar configurações se necessário, 
        // embora neste teste usamos Pipeline estático.
        // O construtor de Cena pede: width, height, ia, shader, cor_flat, material
        // Passando valores dummy pois o teste foca nas matrizes
        Cena cena = new Cena(600, 800, new double[]{0.5,0.5,0.5}, (byte)1, 0, null);
        cena.definir_camera(camera);

        // Como view spec é calculado no construtor da camera em Java:
        double[] u = camera.u;
        double[] v = camera.v;
        double[] n = camera.n;

        System.out.println("\n===== VIEW SPEC =====");
        System.out.println("u = " + Arrays.toString(u));
        System.out.println("v = " + Arrays.toString(v));
        System.out.println("n = " + Arrays.toString(n));

        // =========================================================
        // 3. Matrizes do pipeline
        // =========================================================

        double[][] A = Pipeline.get_matrix_A(camera.vrp);
        double[][] B = Pipeline.get_matrix_B(u, v, n);
        double[][] C = Pipeline.get_matrix_C(camera.Cu, camera.Cv, camera.DP);
        double[][] D = Pipeline.get_matrix_D(camera.Su, camera.Sv, camera.DP, camera.far);
        double[][] P = Pipeline.get_matrix_P(camera.far, camera.near);

        double[][] J = Pipeline.get_matrix_J();
        double[][] K = Pipeline.get_matrix_K();
        
        // Cuidado com a ordem dos parametros em get_matrix_L no Java:
        // get_matrix_L(x_max, x_min, y_max, y_min, z_max, z_min)
        double[][] L = Pipeline.get_matrix_L(
            cena.viewport[2], // x_max
            cena.viewport[0], // x_min
            cena.viewport[3], // y_max
            cena.viewport[1], // y_min
            camera.z_max,
            camera.z_min
        );

        double[][] M = Pipeline.get_matrix_M();

        // =========================================================
        // 4. Composição da matriz final do pipeline
        // =========================================================
        
        List<double[][]> listMatrizes = new ArrayList<>();
        listMatrizes.add(A); listMatrizes.add(B); listMatrizes.add(C);
        listMatrizes.add(D); listMatrizes.add(P); listMatrizes.add(J);
        listMatrizes.add(K); listMatrizes.add(L); listMatrizes.add(M);

        int idx = 0;
        for (double[][] mat : listMatrizes) {
            System.out.println("\n" + idx);
            printMatrix(mat);
            idx++;
        }

        // Mfinal = M * L * K * J * P * D * C * B * A
        // Em Java (Mat4.mul(Mat1, Mat2)):
        double[][] M_pipeline = Mat4.mul(M,
            Mat4.mul(L,
                Mat4.mul(K,
                    Mat4.mul(J,
                        Mat4.mul(P,
                            Mat4.mul(D,
                                Mat4.mul(C,
                                    Mat4.mul(B, A)
                                )
                            )
                        )
                    )
                )
            )
        );

        System.out.println("\n===== MATRIZ FINAL DO PIPELINE =====");
        printMatrix(M_pipeline);

        // =========================================================
        // 5. Aplicar pipeline aos vértices do cubo
        // =========================================================

        System.out.println("\n===== RESULTADO DO PIPELINE =====");

        for (int i = 0; i < cubo.vertices_modelo_transformados.length; i++) {
            double[] v_model = cubo.vertices_modelo_transformados[i];
            System.out.println("\nV" + i + " MODEL : " + Arrays.toString(v_model));

            // Multiplicação Matriz x Ponto
            double[] v_clip = Mat4.mul_point(M_pipeline, v_model);
            System.out.println("CLIP       : " + Arrays.toString(v_clip));

            // Perspectiva / Divisão por W
            if (v_clip[3] != 0) { // Evitar divisão por zero, embora o python use > near/far
                // Lógica simples de verificação similar ao Python
                if (v_clip[3] > (camera.near / camera.far)) { // Mantendo a lógica do script python
                     double[] v_tela = new double[]{
                        v_clip[0] / v_clip[3],
                        v_clip[1] / v_clip[3],
                        v_clip[2] / v_clip[3]
                    };
                    System.out.println("tela        : " + Arrays.toString(v_tela));
                }
            }
        }

        System.out.println("\n===== TESTE DO PIPELINE FINALIZADO =====");
    }
}