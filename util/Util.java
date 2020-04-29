package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    public static void limparTela() {
        try {
            String so = System.getProperty("os.name");

            if (so.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("/bin/bash", "-c", "clear").inheritIO().start().waitFor();
                ;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void mensagemContinuar() throws IOException {
        System.out.print("\nPressione enter para continuar...");
        br.readLine();
        limparTela();
    }

    public static void mensagemTenteNovamente() {
        System.out.println("Opção inválida. Tente novamente.");
    }

    public static void mensagemErroAtualizacao() {
        System.out.println("\nNão foi possível atualizar os dados nesse momento. Tente novamente.");
    }

    public static void mensagemSucessoAtualizacao() {
        System.out.println("\nDados atualizados com sucesso.");
    }
}
