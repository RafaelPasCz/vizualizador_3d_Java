package logica;

public class Mat4{
    public static double[][] identity(){
        return new double [][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    };

    public static double[][] zeros() {
        return new double [4][4];
    };

    public static double[][] trans(double dx, double dy, double dz) {
        return new double[][]{
            {1, 0, 0, dx},
            {0, 1, 0, dy},
            {0, 0, 1, dz},
            {0, 0, 0, 1}
        };

    };

    public static double[] mul_point(double[][] mat, double[] ponto) {
        double[] res = new double[4];
        
        for (int i = 0; i < 4; i++) {

            res[i] = mat[i][0] * ponto[0] + 
                     mat[i][1] * ponto[1] + 
                     mat[i][2] * ponto[2] + 
                     mat[i][3] * ponto[3]; 
        }
        return res;
    }


    public static double[][] mul(double[][] mat1, double[][] mat2){
        double[][] res = new double [4][4];
        
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                for(int k = 0; k < 4; k++){
                    res[i][j] += mat1[i][k] * mat2[k][j];
                }

            }
        }

        return res;
    };

    public static double [][] scale(double sx, double sy, double sz, double[] centroide){
        double[][] escala = new double[][]{
            {sx, 0, 0, 0},
            {0, sy, 0, 0},
            {0, 0, sz, 0},
            {0, 0, 0, 1},
        };

        //processo para o cubo girar em torno de seu eixo
        //leva o centroide do cubo para a origem -> aplica transformação -> devolve para o local original
        double[][] trans_centroide_ida = Mat4.trans(-centroide[0], -centroide[1], -centroide[2]);
        double[][] trans_centroide_volta = Mat4.trans(centroide[0], centroide[1], centroide[2]);
        
        double[][] escala_centroide = Mat4.mul(escala, trans_centroide_ida);

        return(Mat4.mul(trans_centroide_volta, escala_centroide));
    };
        
    public static double[][] rotate_x(double tetha, double[] centroide){
        double tetha_radianos = Math.toRadians(tetha);

        double sen = Math.sin(tetha_radianos);
        double cos = Math.cos(tetha_radianos);

        double[][] rotac_x = new double[][]{
            {1, 0, 0, 0},
            {0, cos, -sen, 0},
            {0, sen, cos, 0},
            {0, 0, 0, 1}
        };

        double[][] trans_centroide_ida = Mat4.trans(-centroide[0], -centroide[1], -centroide[2]);
        double[][] trans_centroide_volta = Mat4.trans(centroide[0], centroide[1], centroide[2]);

        double[][] rotac_x_centroide = Mat4.mul(rotac_x, trans_centroide_ida);
        
        return(Mat4.mul(trans_centroide_volta, rotac_x_centroide));
    }    
    
    public static double[][] rotate_y(double tetha, double[] centroide){

        double tetha_radianos = Math.toRadians(tetha);

        double sen = Math.sin(tetha_radianos);
        double cos = Math.cos(tetha_radianos);

        double[][] rotac_y = new double[][]{
            {cos, 0, sen, 0},
            {0, 1, 0, 0},
            {-sen, 0, cos, 0},
            {0, 0, 0, 1}
        };


        double[][] trans_centroide_ida = Mat4.trans(-centroide[0], -centroide[1], -centroide[2]);
        double[][] trans_centroide_volta = Mat4.trans(centroide[0], centroide[1], centroide[2]);

        double[][] rotac_y_centroide = Mat4.mul(rotac_y, trans_centroide_ida);

        return(Mat4.mul(trans_centroide_volta, rotac_y_centroide));

    }


    public static double[][] rotate_z(double tetha, double[] centroide){

        double tetha_radianos = Math.toRadians(tetha);

        double sen = Math.sin(tetha_radianos);
        double cos = Math.cos(tetha_radianos);

        double[][] rotac_z = new double[][]{
            {cos, -sen, 0, 0},
            {sen, cos, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };

        double[][] trans_centroide_ida = Mat4.trans(-centroide[0], -centroide[1], -centroide[2]);
        double[][] trans_centroide_volta = Mat4.trans(centroide[0], centroide[1], centroide[2]);

        double[][] rotac_z_centroide = Mat4.mul(rotac_z, trans_centroide_ida);

        return(Mat4.mul(trans_centroide_volta, rotac_z_centroide));

    }         
}



