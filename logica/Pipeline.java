package logica;

public class Pipeline {
    public static double[][] get_matrix_A(double[] viewpoint){
        return new double[][]{
            {1, 0, 0, -viewpoint[0]},
            {0, 1, 0, -viewpoint[1]},
            {0, 0, 1, -viewpoint[2]},
            {0, 0, 0, 1}
        };
        
    }

    public static double[][] get_matrix_B(double[] u, double[] v, double[] n){
        return new double[][]{
            {u[0], u[1], u[2], 0},
            {v[0], v[1], v[2], 0},
            {n[0], n[1], n[2], 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] get_matrix_C(double Cu, double Cv, double d){
        return new double[][]{
            {1, 0, -Cu/d, 0},
            {0, 1, -Cv/d, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] get_matrix_D(double Su, double Sv, double d, double f){
        return new double[][]{
            {d / (Su * f), 0, 0, 0},
            {0, d / (Sv * f), 0, 0},
            {0, 0, 1 / f, 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] get_matrix_P(double far, double near){
        return new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, far / (far - near), (-near) / (far - near)},
            {0, 0, 1, 0}
        };
    }

    public static double[][] get_matrix_J(){
        return new double[][]{
            {1, 0, 0, 0},
            {0, -1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] get_matrix_K(){
        return new double[][]{
            {0.5, 0, 0, 0.5},
            {0, 0.5, 0, 0.5},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }

    public static double[][] get_matrix_L(double x_max, double x_min, double y_max, double y_min, double z_max, double z_min){
        return new double[][]{
            {x_max - x_min, 0, 0, x_min},
            {0, y_max - y_min, 0, y_min},
            {0, 0, z_max - z_min, z_min},
            {0, 0, 0, 1},
        };
    }

    public static double[][] get_matrix_M(){
        return new double[][]{
            {1, 0, 0, 0.5},
            {0, 1, 0, 0.5},
            {0, 0, 1, 0.5},
            {0, 0, 0, 1}
        };
    }
    
}
