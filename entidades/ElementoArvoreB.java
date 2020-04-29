package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ElementoArvoreB implements Registro {
    private int idChave;
    private String idUsuario;
    private int idSugestao;

    public ElementoArvoreB() {
    }

    public ElementoArvoreB(String idUsuario, int idSugestao) {
        this.setIdUsuario(idUsuario);
        this.setIdSugestao(idSugestao);
    }

    @Override
    public int getID() {
        return this.idChave;
    }

    @Override
    public void setID(int n) {
        this.idChave = n;
    }

    public int getIdSugestao() {
        return idSugestao;
    }

    public void setIdSugestao(int idSugestao) {
        this.idSugestao = idSugestao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String getSecudaryKey() {
        return null;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeUTF(this.idUsuario);
        saida.writeInt(this.idSugestao);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.idUsuario = entrada.readUTF();
        this.idSugestao = entrada.readInt();
    }

}