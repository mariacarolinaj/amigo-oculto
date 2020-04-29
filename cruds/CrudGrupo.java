package cruds;

import java.io.*;
import entidades.Grupo;

public class CrudGrupo {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Grupo> arquivoSugestoes;
    private int idUsuarioLogado;

    public CrudGrupo(int idUsuarioLogado) {
        try {
            this.inicializarBaseDados(idUsuarioLogado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Método inicializarBaseDados(): tenta abrir os arquivos da base de dados. Caso
     * eles não existam, são criados dentro da pasta "dados".
     */

    private void inicializarBaseDados(int idUsuarioLogado) {
        try {
            this.idUsuarioLogado = idUsuarioLogado;
            // tenta abrir os arquivos da base de dados caso existam;
            // se não existirem, são criados
            arquivoSugestoes = new Arquivo<>(Grupo.class.getConstructor(), "grupos.db");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados dos grupos.");
            e.printStackTrace();
        }
    }

}