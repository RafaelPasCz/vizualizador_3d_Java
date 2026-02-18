package logica;

public class Camera {

    public double[] vrp, prp, vpn, vup, P, Y, window;
    public double[] u, v, n, N, up;
    public double u_max, u_min, v_max, v_min;
    public double DP, near, far, z_min, z_max, AR;
    public double Cu, Cv, Su, Sv;
    public int Vres, Hres; // Altura e Largura em pixels



    public double[][] get_view_spec(){
        return new double[][]{
            {this.u[0], this.u[1], this.u[2]},
            {this.v[0], this.v[1], this.v[2]},
            {this.n[0], this.n[1], this.n[2]}
        };
    }
    
    public void cal_view_spec(){
        this.n = Vector_op.normalize(this.N);

        this.up = Vector_op.normalize(this.Y);

        double dot = Vector_op.dot(this.up, this.n);

        double vx = this.up[0] - (dot * n[0]);
        double vy = this.up[1] - (dot * n[1]);
        double vz = this.up[2] - (dot * n[2]);

        double[] v_temp = new double[] {vx, vy, vz};

        this.v = Vector_op.normalize(v_temp);

        this.u = Vector_op.normalize(Vector_op.cross_product(this.n, this.v));

    }

    public Camera(double[] vrp, double[] prp, double[] vpn, double[] vup, double[] P, double[] Y, 
        double u_max, double u_min, double v_max, double v_min, 
        double DP, double near, double far, int Vres, int Hres) {

            this.vrp = vrp; this.prp = prp; this.vpn = vpn; this.vup = vup;
            this.P = P; this.Y = Y;
            this.u_max = u_max; this.u_min = u_min; this.v_max = v_max; this.v_min = v_min;
            this.DP = DP; this.near = near; this.far = far;
            this.Vres = Vres; this.Hres = Hres;
    
            // Cálculos internos (não devem ser pedidos no construtor)
            this.N = Vector_op.create_vector(vrp, P);
            cal_view_spec();
    
            // Configuração da Janela/Viewplane
            this.window = new double[]{u_min, u_max, v_min, v_max};
            this.Cu = (u_max + u_min) / 2.0;
            this.Cv = (v_max + v_min) / 2.0;
            this.Su = (u_max - u_min) / 2.0;
            this.Sv = (v_max - v_min) / 2.0;
            
            this.AR = this.Su / this.Sv;
            
            // Z normalization
            this.z_min = near / far; 
            this.z_max = 1.0;
            cal_view_spec();

        }
}
