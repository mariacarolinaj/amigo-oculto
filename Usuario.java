import java.io.*;

public class Usuario implements Registro {
    int idUsuario;
    String nome;
    String email;
    String senha;

    public Usuario() {

    }

    public Usuario(int id, String nome, String email, String senha) {
        this.setID(id);
        this.setNome(nome);
        this.setEmail(email);
        this.setSenha(senha);
    }

    public Usuario(String nome, String email, String senha) {
        this.setNome(nome);
        this.setEmail(email);
        this.setSenha(senha);
    }

    @Override
    public int getID() {
        return this.idUsuario;
    }

    @Override
    public void setID(int n) {
        this.idUsuario = n;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario clone() {
        return new Usuario(this.getID(), this.getNome(), this.getEmail(), this.getSenha());
    }

    public boolean equals(Usuario usuario) {
        return usuario.getNome().equals(getNome()) && usuario.getEmail().equals(getEmail())
                && usuario.getSenha().equals(getSenha());
    }

    public String chaveSecundaria() {
        return this.email;
    }

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.nome);
        saida.writeUTF(this.email);
        saida.writeUTF(this.senha);
        return dados.toByteArray();
    }

    public void fromByteArray(final byte[] bytes) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        final DataInputStream entrada = new DataInputStream(dados);
        this.idUsuario = entrada.readInt();
        this.nome = entrada.readUTF();
        this.email = entrada.readUTF();
        this.senha = entrada.readUTF();
    }

    @Override
    public String getSecudaryKey() {
        return this.email;
    }
}
