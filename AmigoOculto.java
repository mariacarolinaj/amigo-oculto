/*
 * Amigo Oculto
 * Trabalho prático para a disciplina de Algoritmos e Estruturas de Dados III do curso de Ciência da Computação
 * PUC Minas - Professor Marcos Kutova
 */

import java.io.*;
import cruds.*;
import entidades.Usuario;
import util.Util;

public class AmigoOculto {
    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static CrudUsuario crudUsuario;
    private static CrudSugestao crudSugestao;
    private static CrudGrupo crudGrupo;
    private static CrudConvite crudConvite;
    private static CrudParticipacao crudParticipacao;
    private static CrudMensagem crudMensagem;

    public static void main(String[] args) throws IOException {
        crudUsuario = new CrudUsuario();
        try {
            exibeMenuInicial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Método inicializarServicos(): Inicializa as instâncias dos CRUD's a serem
     * utilizados nas operações.
     */

    private static void inicializarServicos(Usuario usuarioLogado) {
        crudSugestao = new CrudSugestao(usuarioLogado.getID());
        crudConvite = new CrudConvite(usuarioLogado);
        crudParticipacao = new CrudParticipacao(usuarioLogado.getID());
        crudGrupo = new CrudGrupo(usuarioLogado.getID(), crudConvite, crudParticipacao);
        crudMensagem = new CrudMensagem(usuarioLogado.getID());

        crudMensagem.inicializarBaseDados(crudUsuario);
        crudParticipacao.inicializarBaseDados(crudGrupo, crudUsuario, crudSugestao, crudMensagem);
        crudConvite.inicializarBaseDados(crudGrupo, crudUsuario, crudParticipacao);
    }

    /*
     * Método exibeMenuInicial(): Exibe o menu inicial, realiza leitura da opção
     * desejada e redireciona o usuário para o método correspondente.
     */

    private static void exibeMenuInicial() throws IOException, Exception {
        int opcao;

        do {
            System.out.println("▒▒▒▒▒▒▒█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█");
            System.out.println("▒▒▒▒▒▒▒█░▒▒▒▒▒▒▒▓▒▒▓▒▒▒▒▒▒▒░█");
            System.out.println("▒▒▒▒▒▒▒█░▒▒▓▒▒▒▒▒▒▒▒▒▄▄▒▓▒▒░█░▄▄         Amigo Oculto 1.0");
            System.out.println("▒▒▄▀▀▄▄█░▒▒▒▒▒▒▓▒▒▒▒█░░▀▄▄▄▄▄▀░░█");
            System.out.println("▒▒█░░░░█░▒▒▒▒▒▒▒▒▒▒▒█░░░░░░░░░░░█");
            System.out.println("▒▒▒▀▀▄▄█░▒▒▒▒▓▒▒▒▓▒█░░░█▒░░░░█▒░░█       1: Acesso ao sistema");
            System.out.println("▒▒▒▒▒▒▒█░▒▓▒▒▒▒▓▒▒▒█░░░░░░░▀░░░░░█       2: Novo usuário (primeiro acesso)");
            System.out.println("▒▒▒▒▒▄▄█░▒▒▒▓▒▒▒▒▒▒▒█░░█▄▄█▄▄█░░█        0: Sair");
            System.out.println("▒▒▒▒█░░░█▄▄▄▄▄▄▄▄▄▄█░█▄▄▄▄▄▄▄▄▄█");
            System.out.print("▒▒▒▒█▄▄█░░█▄▄█░░░░░░█▄▄█░░█▄▄█           Ir para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    Usuario usuarioLogado = crudUsuario.logar();
                    boolean sucessoAoLogar = usuarioLogado != null;
                    if (sucessoAoLogar) {
                        inicializarServicos(usuarioLogado);
                        exibeMenuUsuarioLogado();
                    }
                    break;
                case 2:
                    crudUsuario.criarUsuario();
                    break;
                case 0:
                    // sair do programa; não precisa fazer nada
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }

    /*
     * Método exibeMenuUsuarioLogado(): Lista as opções do menu inicial para o
     * usuário após seu login. Realiza leitura da opção desejada e redireciona para
     * o método correspondente.
     */

    public static void exibeMenuUsuarioLogado() throws Exception {
        int opcao;
        do {
            int quantidadeConvitesPendentes = crudConvite.obterQuantidadeConvitesPendentes();
            System.out.println("INÍCIO\n");
            System.out.println("1: Sugestões de presente");
            System.out.println("2: Grupos");
            System.out.println("3: Novos convites (" + quantidadeConvitesPendentes + ")");
            System.out.println("0: Sair");

            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    crudSugestao.exibeMenuSugestoes();
                    break;
                case 2:
                    crudGrupo.exibeMenuGrupos();
                    break;
                case 3:
                    crudConvite.verificarConvitesPendentes();
                    break;
                case 0:
                    System.out.println("Fazendo logoff...");
                    Util.mensagemContinuar();
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }
}
