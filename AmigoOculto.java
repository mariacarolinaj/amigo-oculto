import java.io.*;
import cruds.*;

public class AmigoOculto {
    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static CrudUsuario crudUsuario;
    private static CrudSugestao crudSugestao;
    private static CrudGrupo crudGrupo;

    public static void main(String[] args) throws IOException {
        inicializarServicos();

        try {
            exibeMenuInicial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void inicializarServicos() {
        crudUsuario = new CrudUsuario();
        crudSugestao = new CrudSugestao();
        crudGrupo = new CrudGrupo();
    }

    private static void exibeMenuInicial() throws IOException, Exception {
        int opcao;

        do {
            System.out.println("\nAMIGO OCULTO 1.0\n");
            System.out.println("1: Acesso ao sistema");
            System.out.println("2: Novo usuário (primeiro acesso)");
            System.out.println("0: Sair\n");

            System.out.print("Ir para: ");

            opcao = Integer.parseInt(br.readLine());

            switch (opcao) {
                case 1:
                    boolean sucessoAoLogar = crudUsuario.logar();
                    if (sucessoAoLogar) {
                        exibeMenuUsuarioLogado();
                    }
                    break;
                case 2:
                    crudUsuario.criarUsuario();
                    break;
                case 0: // sair do programa
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.\n");
            }
        } while (opcao != 0);
    }

    public static void exibeMenuUsuarioLogado() throws NumberFormatException, IOException {
        int opcao;
        do {
            System.out.println("\nINÍCIO\n");
            System.out.println("1: Sugestões de presente");
            System.out.println("2: Grupos");
            System.out.println("3: Novos convites (0)");
            System.out.println("0: Sair");

            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());
            switch (opcao) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 0:
                    System.out.println("\nFazendo logoff...");
                    break;
                default:
                    System.out.println("\nOpção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }
}