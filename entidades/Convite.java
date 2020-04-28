package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Convite implements Registro {
    private int idConvite;
    private int idGrupo;
    private String email;
    private long momentoConvite;
    private byte estado;

    public Convite() {
    }

    public Convite(int idGrupo, String email, long momentoConvite, byte estado) {
        this.setIdGrupo(idGrupo);
        this.setEmail(email);
        this.setMomentoConvite(momentoConvite);
        this.setEstado(estado);
    }

    @Override
    public int getID() {
        return this.idConvite;
    }

    @Override
    public void setID(int n) {
        this.idConvite = n;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public long getMomentoConvite() {
        return momentoConvite;
    }

    public void setMomentoConvite(long momentoConvite) {
        this.momentoConvite = momentoConvite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    @Override
    public String getSecudaryKey() {
        return null;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        / final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.idConvite);
        saida.writeInt(this.idGrupo);
        saida.writeUTF(this.email);
        saida.writeLong(this.momentoConvite);
        saida.writeByte(this.estado);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.idConvite = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.email = entrada.readUTF();
        this.momentoConvite = entrada.readLong();
        this.estado = entrada.readByte();
    }

}