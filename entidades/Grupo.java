package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Grupo implements Registro {
    private int id;
    private int idUsuarioCriador;
    private String nome;
    private long momentoSorteio;
    private float valor;
    private long momentoEncontro;
    private String localEncontro;
    private String observacoes;
    private boolean sorteado;
    private boolean ativo;

    public Grupo() {
    }

    public Grupo(int idUsuarioCriador, String nome, long momentoSorteio, float valor, long momentoEncontro,
            String localEncontro, String observacoes, boolean sorteado, boolean ativo) {
        this.setIdUsuarioCriador(idUsuarioCriador);
        this.setNome(nome);
        this.setMomentoSorteio(momentoSorteio);
        this.setValor(valor);
        this.setMomentoEncontro(momentoEncontro);
        this.setLocalEncontro(localEncontro);
        this.setObservacoes(observacoes);
        this.setSorteado(sorteado);
        this.setAtivo(ativo);
    }

    public Grupo(int id, int idUsuarioCriador, String nome, long momentoSorteio, float valor, long momentoEncontro,
            String localEncontro, String observacoes, boolean sorteado, boolean ativo) {
        this.setID(id);
        this.setIdUsuarioCriador(idUsuarioCriador);
        this.setNome(nome);
        this.setMomentoSorteio(momentoSorteio);
        this.setValor(valor);
        this.setMomentoEncontro(momentoEncontro);
        this.setLocalEncontro(localEncontro);
        this.setObservacoes(observacoes);
        this.setSorteado(sorteado);
        this.setAtivo(ativo);
    }

    public Grupo clone() {
        return new Grupo(this.getID(), this.getIdUsuarioCriador(), this.getNome(), this.getMomentoSorteio(),
                this.getValor(), this.getMomentoEncontro(), this.getLocalEncontro(), this.getObservacoes(),
                this.isSorteado(), this.isAtivo());
    }

    public boolean equals(Grupo grupo) {
        return this.getIdUsuarioCriador() == grupo.getIdUsuarioCriador() && this.getNome() == grupo.getNome()
                && this.getMomentoSorteio() == grupo.getMomentoSorteio() && this.getValor() == grupo.getValor()
                && this.getMomentoEncontro() == grupo.getMomentoEncontro()
                && this.getLocalEncontro() == grupo.getLocalEncontro()
                && this.getObservacoes() == grupo.getObservacoes() && this.isSorteado() == grupo.isSorteado()
                && this.isAtivo() == grupo.isAtivo();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isSorteado() {
        return sorteado;
    }

    public void setSorteado(boolean sorteado) {
        this.sorteado = sorteado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getLocalEncontro() {
        return localEncontro;
    }

    public void setLocalEncontro(String localEncontro) {
        this.localEncontro = localEncontro;
    }

    public long getMomentoEncontro() {
        return momentoEncontro;
    }

    public void setMomentoEncontro(long momentoEncontro) {
        this.momentoEncontro = momentoEncontro;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getMomentoSorteio() {
        return momentoSorteio;
    }

    public void setMomentoSorteio(long momentoSorteio) {
        this.momentoSorteio = momentoSorteio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdUsuarioCriador() {
        return idUsuarioCriador;
    }

    public void setIdUsuarioCriador(int idUsuarioCriador) {
        this.idUsuarioCriador = idUsuarioCriador;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int n) {
        this.id = n;
    }

    @Override
    public String getSecudaryKey() {
        return null;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream dados = new ByteArrayOutputStream();
        final DataOutputStream saida = new DataOutputStream(dados);
        saida.writeInt(this.id);
        saida.writeInt(this.idUsuarioCriador);
        saida.writeUTF(this.nome);
        saida.writeLong(this.momentoSorteio);
        saida.writeFloat(this.valor);
        saida.writeLong(this.momentoEncontro);
        saida.writeUTF(this.localEncontro);
        saida.writeUTF(this.observacoes);
        saida.writeBoolean(this.sorteado);
        saida.writeBoolean(this.ativo);
        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        final ByteArrayInputStream dados = new ByteArrayInputStream(ba);
        final DataInputStream entrada = new DataInputStream(dados);
        this.id = entrada.readInt();
        this.idUsuarioCriador = entrada.readInt();
        this.nome = entrada.readUTF();
        this.momentoSorteio = entrada.readLong();
        this.valor = entrada.readFloat();
        this.momentoEncontro = entrada.readLong();
        this.localEncontro = entrada.readUTF();
        this.observacoes = entrada.readUTF();
        this.sorteado = entrada.readBoolean();
        this.ativo = entrada.readBoolean();
    }
}