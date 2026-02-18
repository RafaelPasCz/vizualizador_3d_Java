package logica;

public class Face {
    public int[] indices;
    public double[] normal;

    public boolean contem(int indiceProcurado) {
        // Percorre o array de inteiros (muito rápido)
        for (int i : this.indices) {
            if (i == indiceProcurado) {
                return true;
            }
        }
        return false;
    }

    public Face(int i0, int i1, int i2, int i3){
        this.indices = new int[] {i0, i1, i2, i3};
        this.normal = null;

    }
    
}
