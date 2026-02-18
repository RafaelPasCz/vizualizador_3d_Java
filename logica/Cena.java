package logica;

import java.util.Vector;

import logica.Mat4;
import logica.Vector_op;
import logica.VerticeTela;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cena {
    public List<Cubo> objetos; //lista de instâncias de cubo
    public List<Luz> luzes; //lista de instâncias de luz
    public Camera camera; //camera (singleton)
    public int height;
    public int width;
    public int[] viewport; //[x_min, y_min, x_max, y_max]
    public double[] ia; //luz ambiente
    public byte modo_shader; //1 = flat, 2 = Phong 
    //interiro no color buffer pq tem 32 bits, então da pra dividir em 4 bytes
    //cada byte consegue guardar 0-255, separa em 4 canais
    //byte 0 - alpha (transparencia)
    //byte 1 - R
    //byte 2 - G
    //byte 3 - B
    //o java organiza cores assim
    public int [][] color_buffer; //buffer de rasterização
    public double[][] z_buffer; 



    public int converterRGBInt(int r, int g, int b) {
        // Clamp garante que os valores fiquem entre 0 e 255
        // Isso evita que uma luz muito forte "estoure" a cor e vire preto/negativo
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        // Bitwise Shift: Empacota os 4 canais em um único inteiro de 32 bits
        // Formato ARGB (Alpha, Red, Green, Blue)
        // 255 (0xFF) é o Alpha totalmente opaco
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public int calcular_phong_pixel(double[] N, Cubo cubo){
        double r = this.ia[0] * cubo.ka[0];
        double g = this.ia[1] * cubo.ka[1];
        double b = this.ia[2] * cubo.ka[2];

        double[] S = this.camera.vrp.clone();
        if(S == null){
            System.err.println("Câmera não instanciada");
        }
        
        for (Luz luz : this.luzes) {
            double lx = luz.posicao_ou_direcao[0];
            double ly = luz.posicao_ou_direcao[1];
            double lz = luz.posicao_ou_direcao[2];
            
            double[] L = Vector_op.normalize(new double[]{lx, ly, lz});

            //componente difusa
            double dot_nl = Math.max(0, Vector_op.dot(N, L));

            r += luz.id[0] * cubo.kd[0] * dot_nl;
            g += luz.id[1] * cubo.kd[1] * dot_nl;
            b += luz.id[2] * cubo.kd[2] * dot_nl;

            if(dot_nl > 0){
                //calculo do vetor H

                double hx = L[0] + S[0];
                double hy = L[1] + S[1];
                double hz = L[2] + S[2];

                double[] H = Vector_op.normalize(new double[]{hx, hy, hz});

                double dot_nh = Math.max(0, Vector_op.dot(N, H));

                double spec = Math.pow(dot_nh, cubo.ks[3]);

                r += luz.i_spec[0] * cubo.ks[0] * spec;
                g += luz.i_spec[1] * cubo.ks[1] * spec;
                b += luz.i_spec[2] * cubo.ks[2] * spec;
            }
        }
        r = Math.min(255, Math.max(0, (int) (r*255)));
        g = Math.min(255, Math.max(0, (int) (g*255)));
        b = Math.min(255, Math.max(0, (int) (b*255)));

        int rgb = converterRGBInt((int) r, (int) g, (int)b);
        return rgb;

    }

    public void adicionar_objeto(Cubo cubo){
        this.objetos.add(cubo);
    }

    public void adicionar_luz(Luz luz){
        this.luzes.add(luz);
    }

    public void definir_camera(Camera camera){
        this.camera = camera;
    }

    public void limparColor_buffer(){
        //preenche cada linha do color buffer com 0
        for (int[] row : this.color_buffer) {
            Arrays.fill(row, 0);
        }
    }

    public void limparZ_buffer() {
        //preenche cada linha do z-buffer com infinito 
        for (double[] row : this.z_buffer) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
    }

public boolean dentroPlano(double[] v, double[] plano){
        // Cálculo explícito do produto escalar 4D (x,y,z,w)
        // Isso ignora a classe Vector_op para evitar erros de polimorfismo/tamanho de array
        double dot = (v[0] * plano[0]) + (v[1] * plano[1]) + (v[2] * plano[2]) + (v[3] * plano[3]);

        return (dot >= -1e-5);
    }

    public double[] intersecao_plano(double[] anterior, double[] atual, double[] plano){
        double dist_ant = anterior[0]*plano[0] + anterior[1]*plano[1] + anterior[2]*plano[2] + anterior[3]*plano[3];
        //double dist_ant = Vector_op.dot(anterior, plano);
        //double dist_atu = Vector_op.dot(anterior, plano);
        double dist_atu = atual[0]*plano[0] + atual[1]*plano[1] + atual[2]*plano[2] + atual[3]*plano[3];

        double denominador = dist_ant - dist_atu;

        if (Math.abs(denominador) < 1e-6){
            return atual;
        }

        double t = dist_ant / denominador;

        //interpolação linear (LERP) de x, y, z e w
        double ix = anterior[0] + t * (atual[0] - anterior[0]);
        double iy = anterior[1] + t * (atual[1] - anterior[1]);
        double iz = anterior[2] + t * (atual[2] - anterior[2]);
        double iw = anterior[3] + t * (atual[3] - anterior[3]);

        //interpolaçao da normal (se existir);
        int anterior_len = anterior.length;
        int atual_len = atual.length;
        if(anterior_len >= 7 && atual_len >= 7){
            double nx = anterior[4] + t * (atual[4] - anterior[4]);
            double ny = anterior[5] + t * (atual[5] - anterior[5]);
            double nz = anterior[6] + t * (atual[6] - anterior[6]);

            // Retorna array completo (Posição + Normal)
            return new double[] { ix, iy, iz, iw, nx, ny, nz };
        }
        return new double[] { ix, iy, iz, iw };
        
    }

    public double[][] recorteSH(double[][] verticesClip){
        /*
        Algoritmo Sutherland-Hodgman 3D (Clip Space).
        Recebe lista de vértices [(x, y, z, w, n0, n1, n2), ...].
        Retorna lista recortada.
        */

        if (verticesClip == null || verticesClip.length == 0) {
            // Retorna um array vazio ou trata o caso de "nada a desenhar"
            return new double[0][0]; 
        }

        double[][] planos = new double[][]{
            {0, 0, 1, 1}, //NEAR
            {0, 0, -1, 1}, //FAR
            {1, 0, 0, 1}, //LEFT
            {-1, 0, 0, 1}, //RIGHT
            {0, 1, 0, 1}, //BOTTOM
            {0, -1, 0, 1} //TOP
        };

        List<double[]> outputList = new ArrayList<>(Arrays.asList(verticesClip));

        for (double[] plano : planos) {
            List<double[]> inputList = new ArrayList<>(outputList);
            outputList.clear();
            for(int i = 0; i< inputList.size(); i++){
                double[] atual = inputList.get(i);
                double[] anterior = inputList.get((i - 1 + inputList.size()) % inputList.size());

                boolean atualDentro = dentroPlano(atual, plano);
                boolean anteriorDentro = dentroPlano(anterior, plano);
                
                if(atualDentro){
                    if(!anteriorDentro){
                        //saiu -> entrou: salva interseccao e o atual
                        double[] interseccao = intersecao_plano(anterior, atual, plano);
                        outputList.add(interseccao);
                    }
                    // entrou -> entrou ou saiu->entrou: salva o atual
                    outputList.add(atual);
                }
                else if(anteriorDentro){
                    double[] interseccao = intersecao_plano(anterior, atual, plano);
                    outputList.add(interseccao);
                }
            }
        }

        double[][] resultadoFinal = outputList.toArray(new double[outputList.size()][]);
        return resultadoFinal;
    }

    public void rasterizarFace(VerticeTela[] verticesTela, byte shaderMode, int cor_flat, Cubo obj) {
        
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        //encontrar limites em y
        for (VerticeTela v : verticesTela) {
            if (v.y < min) min = v.y;
            if (v.y > max) max = v.y;
        }
    
        int y_min = (int) min;
        int y_max = (int) max;
    
        //clipping simples, para evitar valores y fora da tela
    
        y_min = Math.max(0, y_min);
        y_max = Math.min(this.height, y_max);
        //criação da edge table
        Map<Integer, List<Aresta>> et = new HashMap<>();
        for (int y = y_min; y < y_max; y++) {
            et.put(y, new ArrayList<>());
        }
    
        int n = verticesTela.length;
    
 
        for (int i = 0; i < n; i++) {
            VerticeTela p1 = verticesTela[i];
            VerticeTela p2 = verticesTela[(i + 1) % n];
    
            //ordenar y
            if (p1.y > p2.y) {
                VerticeTela temp = p1;
                p1 = p2;
                p2 = temp;
            }
            
            double dy = p2.y - p1.y;
            //caso seja uma resta horizontal
            if (dy == 0) continue; 
    
            //calculamos a taxa de variação em y
            double dx_dy = (p2.x - p1.x) / dy;
            double dz_dy = (p2.z - p1.z) / dy;
 
            int start_y = (int) Math.ceil(p1.y);
            double offset_y = start_y - p1.y;
            
            int yMaxAresta = (int) Math.ceil(p2.y);
            //valores de x e z com ajuste sub pixel
            double xInicial = p1.x + (offset_y * dx_dy);
            double zInicial = p1.z + (offset_y * dz_dy);
    

            Aresta aresta = new Aresta(yMaxAresta, xInicial, dx_dy, zInicial, dz_dy);
    
  
            if (shaderMode == 2) {
                double dnx_dy = (p2.normal[0] - p1.normal[0]) / dy;
                double dny_dy = (p2.normal[1] - p1.normal[1]) / dy;
                double dnz_dy = (p2.normal[2] - p1.normal[2]) / dy;
    

                double nx = p1.normal[0] + (offset_y * dnx_dy);
                double ny = p1.normal[1] + (offset_y * dny_dy);
                double nz = p1.normal[2] + (offset_y * dnz_dy);
                //definição das normais e sua variação, caso o shader seja phong
                aresta.setNormais(nx, dnx_dy, ny, dny_dy, nz, dnz_dy);
            }
    

            if (start_y >= 0 && start_y < y_max) {
                 List<Aresta> lista = et.get(start_y);
                 if (lista != null) {
                     lista.add(aresta);
                 }
            }
        }
    
        //lista de arestas ativas
        List<Aresta> aet = new ArrayList<>();
    
        for (int y = y_min; y < y_max; y++) {

            final int yatual = y;

            List<Aresta> novasArestas = et.get(y);
            if (novasArestas != null && !novasArestas.isEmpty()) {
                //caso seja um y válido, adicionamos a lista de arestas ativas
                aet.addAll(novasArestas);
            }
    

            // "Remova todo elemento 'e' onde y for maior ou igual ao yMax da aresta"
            aet.removeIf(e -> yatual >= e.yMax);
    
            // Ordena a lista 'aet' baseada no valor de 'x' (crescente)
            aet.sort((e1, e2) -> Double.compare(e1.x, e2.x));
    
            //preencher spans (pares de arestas)
            for (int i = 0; i < aet.size() - 1; i += 2) {
                Aresta e1 = aet.get(i);
                Aresta e2 = aet.get(i + 1);
    
                int x_start = (int) Math.ceil(e1.x);
                int x_end = (int) Math.ceil(e2.x);
    

                x_start = Math.max(0, x_start);
                x_end = Math.min(this.width, x_end);
    
                if (x_end <= x_start) continue;
    
                double span = e2.x - e1.x; 
                if (span == 0) span = 1;
    

                double dz_dx = (e2.z - e1.z) / span;
                
                //setup de interpolação de z (z-buffer)
                double offset_x = x_start - e1.x;
                double z = e1.z + (offset_x * dz_dx);
    

                double nx = 0, ny = 0, nz = 0;
                double dnx_dx = 0, dny_dx = 0, dnz_dx = 0;


                //setup de interpolação das normais (Phong)
                if (shaderMode == 2) {
                    dnx_dx = (e2.nx - e1.nx) / span;
                    dny_dx = (e2.ny - e1.ny) / span;
                    dnz_dx = (e2.nz - e1.nz) / span;


                // Iniciamos a normal já avançada esse pedacinho      
                    nx = e1.nx + (offset_x * dnx_dx);
                    ny = e1.ny + (offset_x * dny_dx);
                    nz = e1.nz + (offset_x * dnz_dx);
                }
    
     
                for (int x = x_start; x < x_end; x++) {
                    

                    if (z <= this.z_buffer[x][y] + 1e-6) {
                        this.z_buffer[x][y] = z;
    
                        int cor_final = cor_flat;
    
                        if (shaderMode == 2) {
                            double[] N = Vector_op.normalize(new double[]{nx, ny, nz});

                             cor_final = calcular_phong_pixel(N, obj); 
                        }
                        this.color_buffer[x][y] = cor_final;
                    }
    
                    //soma os steps
                    z += dz_dx;
                    if (shaderMode == 2) {
                        nx += dnx_dx;
                        ny += dny_dx;
                        nz += dnz_dx;
                    }
                }
            }
    
            //soma os steps
            for (Aresta e : aet) {
                e.x += e.dx_dy;
                e.z += e.dz_dy;
                if (shaderMode == 2) {
                    e.nx += e.dnx_dy;
                    e.ny += e.dny_dy;
                    e.nz += e.dnz_dy;
                }
            }
        }
    }

    public void renderizar(){
        limparZ_buffer();
        limparColor_buffer();

        if(this.camera == null){
            System.err.println("Câmera não instânciada");
        }
        double[][] mat_A = Pipeline.get_matrix_A(this.camera.vrp);
        double[][] mat_B = Pipeline.get_matrix_B(this.camera.u, this.camera.v, this.camera.n);
        double[][] mat_C = Pipeline.get_matrix_C(this.camera.Cu, this.camera.Cv, this.camera.DP);
        double[][] mat_D = Pipeline.get_matrix_D(this.camera.Su, this.camera.Sv, this.camera.DP, this.camera.far);

        double[][] mat_P = Pipeline.get_matrix_P(this.camera.far, this.camera.near);
        double[][] mat_J = Pipeline.get_matrix_J();
        double[][] mat_K = Pipeline.get_matrix_K();
                                                //x_max             //x_min         //y_max            //y_min
        double[][] mat_L = Pipeline.get_matrix_L(this.viewport[2], this.viewport[0], this.viewport[3], this.viewport[1],
            this.camera.z_max, this.camera.z_min);
        double[][] mat_M = Pipeline.get_matrix_M();

        //composição das matrizes do pipeline
        //mundo -> clip space -> tela    
        double [][] m_view = Mat4.mul(mat_B, mat_A);
        double [][] m_proj = Mat4.mul(mat_P, Mat4.mul(mat_D, mat_C));
        double [][] m_total_proj = Mat4.mul(m_proj, m_view);
        double [][] m_screen = Mat4.mul(mat_M, Mat4.mul(mat_L, Mat4.mul(mat_K, mat_J)));    

        for (Cubo cubo : this.objetos) {
            for (Face face : cubo.lista_faces) {
                //back-face culling
                double[] v0 = cubo.vertices_modelo_transformados[face.indices[0]];
                double vx = this.camera.vrp[0] - v0[0];
                double vy = this.camera.vrp[1] - v0[1];
                double vz = this.camera.vrp[2] - v0[2];

                double dot_vis = Vector_op.dot(face.normal, new double[]{vx, vy, vz});

                if(dot_vis > 0){
                    List<double[]> vertices_clip_spaceList = new ArrayList<double[]>();

                    for (int idx : face.indices) {
                        double[] v_mundo = cubo.vertices_modelo_transformados[idx];
                        double[] v_clip = Mat4.mul_point(m_total_proj, v_mundo);

                        double[] normal_vert = cubo.normais_vertices[idx];

                        vertices_clip_spaceList.add(new double[]{v_clip[0], v_clip[1], v_clip[2], v_clip[3],
                            normal_vert[0], normal_vert[1], normal_vert[2]});                        
                    }

                    double[][] vertices_clip_space = vertices_clip_spaceList.toArray(new double[vertices_clip_spaceList.size()][]);
                    
                    double[][] poly_clipado = recorteSH(vertices_clip_space);

                    if(poly_clipado.length < 3)
                        continue;
                    
                    List<double[]> poligonosRecortadosList = new ArrayList<>();

                    for (double[] v : poly_clipado) {
                        double x = v[0];
                        double y = v[1];
                        double z = v[2];
                        double w = v[3];
                        double nx = v[4];
                        double ny = v[5];
                        double nz = v[6];
                        
                        //valores muito pequenos de w podem causar comportamento inesperado
                        if(w < 0.001){
                            w = 0.001;
                        }

                        double[] v_ndc = new double[]{x / w, y / w, z / w, 1};
                        double[] v_tela = Mat4.mul_point(m_screen, v_ndc);

                        poligonosRecortadosList.add(new double[]{
                            v_tela[0],
                            v_tela[1],
                            v_tela[2],
                            nx,
                            ny,
                            nz
                        });
                    }
                    double[][] poligonosRecortados = poligonosRecortadosList.toArray(new double[poligonosRecortadosList.size()][]);
                    byte shaderMode = this.modo_shader; //1 = flat 2 = phong
                    VerticeTela[] verticesProntos = new VerticeTela[poligonosRecortados.length];

                    for (int i = 0; i < poligonosRecortados.length; i++) {
                        double[] pt = poligonosRecortados[i];

                        double[] normalInterp;
                        if (pt.length >= 6) {
                            normalInterp = new double[]{pt[3], pt[4], pt[5]};
                        } else {
                            normalInterp = face.normal; 
                        }

                        verticesProntos[i] = new VerticeTela(pt[0], pt[1], pt[2], 0, normalInterp);
                    }

  
                    int corFinal = 0; // Preto/Transparente por padrão

                    if (this.modo_shader == 1) { // 1 = Flat
                        if (!this.luzes.isEmpty()) {
                            // Calcula iluminação baseada na normal da FACE (constante para todo o polígono)
                            corFinal = this.calcular_phong_pixel(face.normal, cubo);
                        } else {
                            // Se não tiver luz, usa uma cor cinza padrão para debug
                            corFinal = converterRGBInt(100, 100, 100);
                        }
                    }

                    // 3. Chamada do Rasterizador (Scanline)
                    // Passamos os vertices convertidos e a cor calculada
                    this.rasterizarFace(verticesProntos, this.modo_shader, corFinal, cubo);

                } 
            }
        } 
    } 

    public Cena(int width, int height, double[] ia, byte modo_shader, int cor_flat, double[] material){
        //x_min, y_min, x_max, y_max
        this.viewport = new int[]{0, 0, width, height}; 
        this.objetos = new ArrayList<Cubo>();
        this.luzes = new ArrayList<Luz>();

        this.ia = ia;

        this.modo_shader = modo_shader;

        this.height = height;
        this.width = width;
        this.color_buffer = new int[width][height]; 

        this.z_buffer = new double[width][height];
        //aqui na verdade ele preenche com infinito
        limparZ_buffer();

    }
}

