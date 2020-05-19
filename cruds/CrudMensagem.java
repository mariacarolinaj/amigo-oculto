package cruds;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import entidades.Grupo;
import entidades.Mensagem;
import entidades.Usuario;
import util.Util;

public class CrudMensagem {
    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Mensagem> arquivoMensagens;
    private static ArvoreB chavesMensagemGrupo;

    private static CrudUsuario crudUsuario;

    private int idUsuarioLogado;

    public CrudMensagem(int idUsuarioLogado) {
        try {
            this.idUsuarioLogado = idUsuarioLogado;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Método inicializarBaseDados(): tenta abrir os arquivos da base de dados. Caso
     * eles não existam, são criados dentro da pasta "dados".
     */

    public void inicializarBaseDados(CrudUsuario cu) {
        try {
            crudUsuario = cu;
            // tenta abrir os arquivos da base de dados caso existam;
            // se não existirem, são criados
            arquivoMensagens = new Arquivo<>(Mensagem.class.getConstructor(), "mensagens.db");
            chavesMensagemGrupo = new ArvoreB(100, "chavesMensagemGrupo.idx");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados das mensagens.");
            e.printStackTrace();
        }
    }

    public void exibirMenuMensagem(Grupo grupo) throws Exception {
        int opcao;
        do {
            Util.limparTela();
            System.out.println(
                    "INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPO > MENSAGENS DO GRUPO \"" + grupo.getNome() + "\"\n");
            ;
            System.out.println("1: Ler mensagens");
            System.out.println("2: Enviar nova mensagem ao grupo");
            System.out.println("0: Sair");

            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.lerMensagens(grupo);
                    break;
                case 2:
                    this.criarNovaMensagem(grupo);
                    break;
                case 0: // não precisa fazer nada
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);

    }

    // #region CREATE

    public void criarNovaMensagem(Grupo grupo) throws IOException {
        System.out.println("ENVIAR NOVA MENSAGEM AO GRUPO \"" + grupo.getNome() + "\"");
        System.out.print("\nInsira o conteúdo da mensagem: ");
        String mensagem = br.readLine();
        char confirmacao;
        do {
            System.out.print("\nConfirmar envio da mensagem (S/N)? ");
            confirmacao = br.readLine().charAt(0);

            if (confirmacao != 's' && confirmacao != 'S' && confirmacao != 'n' && confirmacao != 'N') {
                System.out.println("Opção inválida. Tente novamente.");
            }
        } while (confirmacao != 's' && confirmacao != 'S' && confirmacao != 'n' && confirmacao != 'N');

        if (confirmacao == 's' || confirmacao == 'S') {
            Mensagem mensagemASerEnviada = new Mensagem(this.idUsuarioLogado, grupo.getID(), mensagem,
                    new Date().getTime());
            try {
                int idInserido = arquivoMensagens.incluir(mensagemASerEnviada);
                chavesMensagemGrupo.inserir(grupo.getID(), idInserido);
                Util.mensagemSucessoCadastro();
            } catch (Exception e) {
                Util.mensagemErroCadastro();
            }
        }

        Util.mensagemContinuar();
    }

    // #endregion

    // #region READ

    public void lerMensagens(Grupo grupo) throws Exception {
        Mensagem[] mensagens = obterMensagensOrdenadas(grupo);
        Usuario[] autoresMensagens = obterAutoresMensagens(mensagens);
        int indiceInicial = 0; // armazena o indice inicial atual de listagem, contando de 5 em 5 mensagens
        int paginaAtual = 1;
        int quantidadePaginas = mensagens.length / 5;
        if (mensagens.length % 5 > 0) { // sobraram algumas mensagens na proxima pagina
            quantidadePaginas += 1;
        }
        char opcao;

        if (mensagens.length > 0) {
            do {
                Util.limparTela();

                System.out.println("FÓRUM DO GRUPO \"" + grupo.getNome() + "\"\n");

                for (int i = indiceInicial; i < mensagens.length && i < paginaAtual * 5; i++) {
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String dataString = dataFormat.format(new Date(mensagens[i].getMomentoEnvio()));
                    System.out.println(autoresMensagens[i].getNome() + " (" + autoresMensagens[i].getEmail()
                            + ") disse em " + dataString + ":");
                    System.out.println(mensagens[i].getMensagem());
                    System.out.println("---");
                }

                System.out.println("\nPÁGINA " + paginaAtual + " DE " + quantidadePaginas);

                if (quantidadePaginas > 1) {
                    if (paginaAtual == 1) {
                        do {
                            System.out.print("\nInsira P para ir para a próxima página ou 0 para sair: ");
                            opcao = br.readLine().charAt(0);
                            if (opcao != 'P' && opcao != 'p' && opcao != '0') {
                                System.out.println("Opção inválida. Tente novamente.");
                            }
                        } while (opcao != 'P' && opcao != 'p' && opcao != '0');
                    } else if (paginaAtual == quantidadePaginas) {
                        do {
                            System.out.print("\nInsira A para ir para a página anterior ou 0 para sair: ");
                            opcao = br.readLine().charAt(0);
                            if (opcao != 'A' && opcao != 'a' && opcao != '0') {
                                System.out.println("Opção inválida. Tente novamente.");
                            }
                        } while (opcao != 'A' && opcao != 'a' && opcao != '0');
                    } else {
                        do {
                            System.out.print(
                                    "\nInsira P para ir para a próxima página, A para a anterior ou 0 para sair: ");
                            opcao = br.readLine().charAt(0);
                            if (opcao != 'A' && opcao != 'a' && opcao != 'P' && opcao != 'p' && opcao != '0') {
                                System.out.println("Opção inválida. Tente novamente.");
                            }
                        } while (opcao != 'A' && opcao != 'a' && opcao != 'P' && opcao != 'p' && opcao != '0');
                    }

                    if (opcao == 'P' || opcao == 'p') {
                        indiceInicial += 5;
                        paginaAtual++;
                    } else if (opcao == 'A' || opcao == 'a') {
                        indiceInicial -= 5;
                        paginaAtual--;
                    }
                } else {
                    opcao = '0';
                    Util.mensagemContinuar();
                }

            } while (opcao != '0' && paginaAtual > 0 && paginaAtual <= quantidadePaginas);
        } else {
            System.out.println("Ainda não existem mensagens neste grupo.");
            Util.mensagemContinuar();
        }
    }

    public Mensagem[] obterMensagensOrdenadas(Grupo grupo) throws Exception {
        int[] idsMensagens = chavesMensagemGrupo.lista(grupo.getID());
        Mensagem[] mensagens = new Mensagem[idsMensagens.length];
        Mensagem[] mensagensOrdenadas = new Mensagem[mensagens.length];

        for (int i = 0; i < idsMensagens.length; i++) {
            mensagens[i] = (Mensagem) arquivoMensagens.buscar(idsMensagens[i]);
        }
        int contador = mensagens.length - 1;
        for (int i = 0; i < mensagens.length; i++) {
            mensagensOrdenadas[i] = mensagens[contador--];
        }

        return mensagensOrdenadas;
    }

    public Usuario[] obterAutoresMensagens(Mensagem[] mensagens) throws Exception {
        Usuario[] autores = new Usuario[mensagens.length];

        for (int i = 0; i < mensagens.length; i++) {
            autores[i] = (Usuario) crudUsuario.obterUsuarioPorId(mensagens[i].getIdUsuario());
        }

        return autores;
    }
    // #endregion
}