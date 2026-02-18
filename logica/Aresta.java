package logica;

public class Aresta {

    public int yMax;
    public double x;      // X atual (incrementado a cada scanline)
    public double dx_dy;  // Inclinação (quanto X muda por Y)
    
    // Z-Buffer
    public double z;      // Z atual (para profundidade)
    public double dz_dy;  // Variação de Z por Y


    public double nx, ny, nz;          // Normais atuais
    public double dnx_dy, dny_dy, dnz_dy; // Variação das normais por Y

    public Aresta(int yMax, double x, double dx_dy, double z, double dz_dy) {
        this.yMax = yMax;
        this.x = x;
        this.dx_dy = dx_dy;
        this.z = z;
        this.dz_dy = dz_dy;

    }

    public void setNormais(double nx, double dnx, double ny, double dny, double nz, double dnz) {
        this.nx = nx;   this.dnx_dy = dnx;
        this.ny = ny;   this.dny_dy = dny;
        this.nz = nz;   this.dnz_dy = dnz;
    }
}