package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Participacao implements Registro {
    private int idParticipacao;
    private int idUsuario;
    private int idGrupo;
    private int idAmigo;

    public Participacao() {
    }

    public Participacao(int idUsuario, int idGrupo, int idAmigo) {
        this.setIdUsuario(idUsuario);
        this.setIdGrupo(idGrupo);
        this.setIdAmigo(idAmigo);
    }

    public Participacao(int id, int idUsuario, int idGrupo, int idAmigo) {
        this.setID(id);
        this.setIdUsuario(idUsuario);
        this.setIdGrupo(idGrupo);
        this.setIdAmigo(idAmigo);
    }

    @Override
    public int getID() {
        return idParticipacao;
    }

    @Override
    public void setID(int idParticipacao) {
        this.idParticipacao = idParticipacao;
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

    public int getIdAmigo() {
        return idAmigo;
    }

    public void setIdAmigo(int idAmigo) {
        this.idAmigo = idAmigo;
    }

    @Override
    public String getSecudaryKey() {
        return this.idUsuario + "|" + this.idGrupo;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.idParticipacao);
        saida.writeInt(this.idUsuario);
        saida.writeInt(this.idGrupo);
        saida.writeInt(this.idAmigo);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.idParticipacao = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.idAmigo = entrada.readInt();
    }

    public Participacao clone() {
        return new Participacao(getID(), getIdUsuario(), getIdGrupo(), getIdAmigo());
    }
}