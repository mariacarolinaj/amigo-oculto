package entidades;

import java.io.*;

public class Mensagem implements Registro {
    private int idMensagem;
    private int idUsuario;
    private int idGrupo;
    private String mensagem;
    private long momentoEnvio;

    public Mensagem() {
    }

    public Mensagem(int idMensagem, int idUsuario, int idGrupo, String mensagem, long momentoEnvio) {
        this.idMensagem = idMensagem;
        this.idUsuario = idUsuario;
        this.idGrupo = idGrupo;
        this.mensagem = mensagem;
        this.momentoEnvio = momentoEnvio;
    }

    public Mensagem(int idUsuario, int idGrupo, String mensagem, long momentoEnvio) {
        this.idUsuario = idUsuario;
        this.idGrupo = idGrupo;
        this.mensagem = mensagem;
        this.momentoEnvio = momentoEnvio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public long getMomentoEnvio() {
        return momentoEnvio;
    }

    public void setMomentoEnvio(long momentoEnvio) {
        this.momentoEnvio = momentoEnvio;
    }

    @Override
    public int getID() {
        return this.idMensagem;
    }

    @Override
    public void setID(int n) {
        this.idMensagem = n;
    }

    @Override
    public String getSecudaryKey() {
        return getID() + "|" + getIdUsuario();
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.idMensagem);
        saida.writeInt(this.idUsuario);
        saida.writeInt(this.idGrupo);
        saida.writeLong(this.momentoEnvio);
        saida.writeUTF(this.mensagem);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);

        this.idMensagem = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.momentoEnvio = entrada.readLong();
        this.mensagem = entrada.readUTF();
    }
}