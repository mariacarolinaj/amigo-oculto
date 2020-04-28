package cruds;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import entidades.*;

public class Arquivo<T extends Registro> {

    public String nomeArquivo;
    public String nomeDiretorio;
    public String nomeListaCestos;
    public RandomAccessFile arquivo;
    Constructor<T> construtor;
    private HashExtensivel indice;

    public Arquivo(Constructor<T> c, String n) throws Exception {
        construtor = c;
        nomeArquivo = n;
        File d = new File("dados");
        if (!d.exists())
            d.mkdir();
        arquivo = new RandomAccessFile("dados/" + nomeArquivo, "rw");
        if (arquivo.length() < 4)
            arquivo.writeInt(0);

        nomeDiretorio = "diretorio." + nomeArquivo;
        nomeListaCestos = "cestos." + nomeArquivo;
        indice = new HashExtensivel(4, nomeDiretorio, nomeListaCestos);
    }

    public int incluir(T obj) throws Exception {
        this.arquivo.seek(0);
        int ultimoID = this.arquivo.readInt();
        ultimoID++;
        arquivo.seek(0);
        arquivo.writeInt(ultimoID);

        arquivo.seek(arquivo.length());
        long endereco = arquivo.getFilePointer();
        obj.setID(ultimoID);
        arquivo.writeByte(' '); // lápide
        byte[] byteArray = obj.toByteArray();
        arquivo.writeInt(byteArray.length);
        arquivo.write(byteArray); // inclui o registro
        indice.insere(ultimoID, endereco);
        return obj.getID();
    }

    public Object[] listar() throws Exception {
        ArrayList<T> lista = new ArrayList<>();
        arquivo.seek(4);
        byte lapide;
        byte[] byteArray;
        int s;
        T obj;
        while (arquivo.getFilePointer() < arquivo.length()) {
            obj = construtor.newInstance();
            lapide = arquivo.readByte();
            s = arquivo.readInt();
            byteArray = new byte[s];
            arquivo.read(byteArray);
            obj.fromByteArray(byteArray);
            if (lapide == ' ')
                lista.add(obj);
        }
        return lista.toArray();
    }

    public Object buscar(int id) throws Exception {
        byte lapide;
        byte[] byteArray;
        int s;
        T obj;

        long endereco = indice.busca(id);
        if (endereco != -1) {
            obj = construtor.newInstance();
            arquivo.seek(endereco);
            lapide = arquivo.readByte();
            s = arquivo.readInt();
            byteArray = new byte[s];
            arquivo.read(byteArray);
            obj.fromByteArray(byteArray);
            if (lapide == ' ' && obj.getID() == id)
                return obj;
        }
        return null;
    }

    public ChaveSecundariaUsuario buscarChaveSecundariaUsuario(String chaveSecundaria) throws Exception {
        Object[] itens = this.listar();

        for (int i = 0; i < itens.length; i++) {
            ChaveSecundariaUsuario item = (ChaveSecundariaUsuario) itens[i];
            if (item.getEmail().equals(chaveSecundaria)) {
                return item;
            }
        }

        return null;
    }

    public boolean excluir(int id) throws Exception {
        long endereco = indice.busca(id);
        if (endereco != -1) {
            arquivo.seek(endereco);
            arquivo.writeByte('*');
            indice.remove(id);
            return true;
        } else
            return false;
    }

    public boolean atualizar(T obj) throws Exception {
        byte lapide;
        byte[] byteArray;
        int s;
        T objetoEncontrado;

        long endereco = indice.busca(obj.getID());
        if (endereco != -1) {
            objetoEncontrado = construtor.newInstance();
            arquivo.seek(endereco);
            lapide = arquivo.readByte();
            s = arquivo.readInt();
            byteArray = new byte[s];
            arquivo.read(byteArray);
            objetoEncontrado.fromByteArray(byteArray);
            if (lapide == ' ' && objetoEncontrado.getID() == obj.getID()) { // encontrou o registro
                excluir(objetoEncontrado.getID()); // apaga o registro existente
                arquivo.seek(arquivo.length());
                endereco = arquivo.getFilePointer();
                obj.setID(obj.getID());
                arquivo.writeByte(' '); // lápide nova
                byteArray = obj.toByteArray();
                arquivo.writeInt(byteArray.length);
                arquivo.write(byteArray); // escreve o registro antigo atualizado, com o mesmo ID
                indice.insere(obj.getID(), endereco);
                return true;
            }
        }
        return false;
    }
}