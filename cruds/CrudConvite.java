package cruds;

import java.io.*;
import entidades.Convite;

public class CrudConvite {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Convite> arquivoConvites;

    public CrudConvite() {
        try {
            this.inicializarBaseDados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarBaseDados() {
        try {
            // tenta abrir os arquivos da base de dados caso existam; se não existirem, são
            // criados
            arquivoConvites = new Arquivo<>(Convite.class.getConstructor(), "convites.db");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados dos convites.");
            e.printStackTrace();
        }
    }

}