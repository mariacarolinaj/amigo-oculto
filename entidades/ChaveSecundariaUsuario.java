package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChaveSecundariaUsuario implements Registro {
    private int id;
    private int idUsuario;
    private String email;

    public ChaveSecundariaUsuario() {
    }

    public ChaveSecundariaUsuario(int idUsuario, String email) {
        this.setIdUsuario(idUsuario);
        this.setEmail(email);
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int n) {
        this.id = n;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(int n) {
        this.idUsuario = n;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String n) {
        this.email = n;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.id);
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.email);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.id = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.email = entrada.readUTF();
    }

    @Override
    public String getSecudaryKey() {
        return this.email;
    }

}