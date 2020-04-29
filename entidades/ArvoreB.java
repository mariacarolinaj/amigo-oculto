package entidades;

// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.DataInputStream;
// import java.io.DataOutputStream;
import java.io.IOException;

public class ArvoreB implements Registro {
    private ArvoreB arvore;

    public ArvoreB() {
    }

    public ArvoreB(int ordem, String nomeArquivo){
        arvore = new ArvoreB(ordem, nomeArquivo);
    }

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setID(int n) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getSecudaryKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        // TODO Auto-generated method stub

    }
}