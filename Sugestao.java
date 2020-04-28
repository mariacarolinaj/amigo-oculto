import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Sugestao implements Registro {

    private int id;
    private int idUsuario;
    private String produto;
    private String loja;
    private float valor;
    private String observacoes;

    public Sugestao() {
    }

    public Sugestao(int idUsuario, String produto, String loja, float valor, String observacoes) {
        this.setIdUsuario(idUsuario);
        this.setProduto(produto);
        this.setLoja(loja);
        this.setValor(valor);
        this.setObservacoes(observacoes);
    }

    @Override
    public int getID() {
        return this.id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public void setID(int n) {
        this.id = n;

    }

    @Override
    public Object getSecudaryKey() {
        return null;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.id);
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.produto);
        saida.writeUTF(this.loja);
        saida.writeFloat(this.valor);
        saida.writeUTF(this.observacoes);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.id = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.produto = entrada.readUTF();
        this.loja = entrada.readUTF();
        this.valor = entrada.readFloat();
        this.observacoes = entrada.readUTF();
    }

}