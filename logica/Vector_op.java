package logica;

public class Vector_op{
    public static double magn(double[] V){
        double soma_quadrados = 0.0;
        for(int i = 0; i < 3; i++){
            soma_quadrados += (V[i] * V[i]); 
        }
        return Math.sqrt(soma_quadrados);
    }

    public static double[] create_vector(double[] A, double[] B){
        double Vx = B[0] - A[0];
        double Vy = B[1] - A[1];
        double Vz = B[2] - A[2];
        
        return new double[]{Vx, Vy, Vz};
    }

    public static double[] normalize(double[] V){
        double mag = Vector_op.magn(V);

        if(mag == 0)
            return new double[]{0, 0, 0};

        return new double[] {V[0] / mag, V[1] / mag, V[2] / mag};
        
    }
    
    public static double[] cross_product(double[] A, double[] B){
        double Nx = (A[1] * B[2]) - (A[2] * B[1]);
        double Ny = (A[2] * B[0]) - (A[0] * B[2]);
        double Nz = (A[0] * B[1]) - (A[1] * B[0]);
        
        return new double[]{Nx, Ny, Nz};
    }

    public static double dot(double[] A, double[]B){
        double Fx = (A[0] * B[0]);
        double Fy = (A[1] * B[1]);
        double Fz = (A[2] * B[2]);
        double Fw = 0;
        if(A.length > 3 && B.length > 3)
            Fw = (A[3] * B[3]);
        return Fx + Fy + Fz + Fw;
    }
}
