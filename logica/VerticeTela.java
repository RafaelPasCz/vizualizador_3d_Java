package logica;

public class VerticeTela {
    public double x, y, z;
    public int cor;
    public double[] normal;

    // CONSTRUTOR PRINCIPAL (Faz a atribuição direta)
    public VerticeTela(double x, double y, double z, int cor, double[] normal) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.cor = cor;
        this.normal = normal;
    }

    // Sobrecarga (chama o principal)
    public VerticeTela(double x, double y, double z, int cor) {
        this(x, y, z, cor, new double[]{0, 0, 0});
    }

    // Sobrecarga (chama o principal)
    public VerticeTela(double x, double y, double z) {
        this(x, y, z, 0, new double[]{0, 0, 0}); 
    }
}