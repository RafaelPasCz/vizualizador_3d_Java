package logica;

public class Luz {
    public byte tipo;
    public double[] posicao_ou_direcao;
    public double[] id;
    public double[] i_spec;

    public Luz(double[] vetor_pos_dir, double[] id, double[] is, byte tipo){
        this.id = new double[] {id[0], id[1], id[2]};
        this.i_spec = new double[] {is[0], is[1], is[2]};


        this.posicao_ou_direcao = new double[] {vetor_pos_dir[0], vetor_pos_dir[1], vetor_pos_dir[2]};    

        this.tipo = tipo;
    }
}
