package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static void mensagemErroExclusao() {
        System.out.println("\nNão foi possível realizar a exclusão no momento. Tente novamente.");
    }

    public static void mensagemSucessoExclusao() {
        System.out.println("\nExclusão concluída com sucesso.");
    }

    public static void mensagemErroCadastro() {
        System.out.println("\nNão foi possível cadastrar os dados informados no momento. Tente novamente.");
    }

    public static void mensagemSucessoCadastro() {
        System.out.println("\nCadastro realizado com sucesso.");
    }

    public static Date validarEMontarData(String data) {
        Date objetoData = null;
        try {

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            objetoData = (Date) formatter.parse(data);
        } catch (Exception e) {
            System.out.println("A data inserida é inválida. Tente novamente.");
        }

        return objetoData;
    }
}
