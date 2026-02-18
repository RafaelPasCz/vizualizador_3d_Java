package logica;

public class Cubo {
    public double[] ka; // r, g, b
    public double[] kd; // r, g, b
    public double[] ks; // r, g, b, n

    // Geometria
    public double[][] vertices_modelo; // Original
    public double[][] vertices_modelo_transformados; // Atualizado
    
    public double[][] normais_vertices; 

    public Face[] lista_faces; 
    public Face[] model_faces;

    // Estado
    public double[] rotacao = new double[] {0.0, 0.0, 0.0};
    public double escala = 1.0;
    
    // Centroide: xyz
    public double[] centroide;
    
    // Posição atual (baseada no centroide)
    public double[] trans; 

    // --- MÉTODOS ---


    public void aplicar_transformacao(double[][] mat) {
        // Percorre cada vértice transformado e aplica a matriz
        for (int i = 0; i < this.vertices_modelo_transformados.length; i++) {
            // Pega o vértice atual
            double[] v = this.vertices_modelo_transformados[i];
            
            this.vertices_modelo_transformados[i] = Mat4.mul_point(mat, v);
        }
        

        this.atualizar_normais();
        this.atualizar_centroide();
    }


    public void atualizar_normais() {

        for (Face face : this.lista_faces) {
            int i0 = face.indices[0];
            int i1 = face.indices[1];
            int i2 = face.indices[2];
  

            double[] A = this.vertices_modelo_transformados[i0];
            double[] B = this.vertices_modelo_transformados[i1];
            double[] C = this.vertices_modelo_transformados[i2];


            double[] AB = Vector_op.create_vector(A, B);
            double[] AC = Vector_op.create_vector(A, C);

            double[] normal = Vector_op.cross_product(AB, AC);
            face.normal = Vector_op.normalize(normal);
        }


        int num_vertices = this.vertices_modelo_transformados.length;

        this.normais_vertices = new double[num_vertices][3];

        for (int idx_vertice = 0; idx_vertice < num_vertices; idx_vertice++) {
            double soma_nx = 0.0;
            double soma_ny = 0.0;
            double soma_nz = 0.0;
            int count = 0;

            for (Face face : this.lista_faces) {

                if (face.contem(idx_vertice)) {
                    soma_nx += face.normal[0];
                    soma_ny += face.normal[1];
                    soma_nz += face.normal[2];
                    count++;
                }
            }

            if (count > 0){ 
                double[] media = new double[] { soma_nx / count, soma_ny / count, soma_nz / count };
                this.normais_vertices[idx_vertice] = Vector_op.normalize(media);
            }
    }
}



    public void atualizar_centroide() {
        double soma_x = 0.0;
        double soma_y = 0.0;
        double soma_z = 0.0;

        int num_vertices = this.vertices_modelo_transformados.length;

        for (double[] v : this.vertices_modelo_transformados) {
            soma_x += v[0];
            soma_y += v[1];
            soma_z += v[2];
        }

        double cx = soma_x / num_vertices;
        double cy = soma_y / num_vertices;
        double cz = soma_z / num_vertices;

        this.centroide[0] = cx;
        this.centroide[1] = cy;
        this.centroide[2] = cz;
    }


    public double[] get_centroide() {
        return this.centroide;
    }

    public Cubo(double[] v0, double lado, double[] ka, double[] kd, double[] ks) { 

        this.ka = new double[] {ka[0], ka[1], ka[2]};
        this.kd = new double[] {kd[0], kd[1], kd[2]};
   
        this.ks = new double[] {ks[0], ks[1], ks[2], ks[3]};

        double x = v0[0];
        double y = v0[1];
        double z = v0[2];
        double l = lado;

        this.vertices_modelo = new double[][]{
            {x, y, z, 1.0},         // v0
            {x + l, y, z, 1.0},     // v1
            {x, y + l, z, 1.0},     // v2
            {x + l, y + l, z, 1.0}, // v3
            {x, y, z + l, 1.0},     // v4
            {x + l, y, z + l, 1.0}, // v5
            {x, y + l, z + l, 1.0}, // v6
            {x + l, y + l, z + l, 1.0} // v7
        };


        this.vertices_modelo_transformados = new double[8][4];
        for(int i = 0; i < 8; i++){
            this.vertices_modelo_transformados[i] = this.vertices_modelo[i].clone();
        }


        this.lista_faces = new Face[]{
            new Face(4, 5, 7, 6),
            new Face(1, 0, 2, 3),
            new Face(0, 4, 6, 2),
            new Face(5, 1, 3, 7),
            new Face(2, 6, 7, 3),
            new Face(4, 0, 1, 5),
        };
        

        this.model_faces = this.lista_faces.clone();


        this.centroide = new double[3];
        this.normais_vertices = new double[8][3];

        atualizar_normais();
        atualizar_centroide();


        this.trans = new double[] {centroide[0], centroide[1], centroide[2]};
    }
}