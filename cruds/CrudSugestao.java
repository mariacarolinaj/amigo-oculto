package cruds;

import java.io.*;
import entidades.Sugestao;

public class CrudSugestao {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Sugestao> arquivoSugestoes;

    public CrudSugestao() {
        try {
            this.inicializarBaseDados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Método inicializarBaseDados(): tenta abrir os arquivos da base de dados. Caso
     * eles não existam, são criados dentro da pasta "dados".
     */

    private void inicializarBaseDados() {
        try {
            // tenta abrir os arquivos da base de dados caso existam;
            // se não existirem, são criados
            arquivoSugestoes = new Arquivo<>(Sugestao.class.getConstructor(), "sugestoes.db");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados das sugestões.");
            e.printStackTrace();
        }
    }

}