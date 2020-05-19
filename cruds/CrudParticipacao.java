package cruds;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import entidades.Grupo;
import entidades.Participacao;
import entidades.Usuario;
import util.Util;

public class CrudParticipacao {
    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Participacao> arquivoParticipacoes;
    private static ArvoreB chavesGrupoParticipacao;
    private static ArvoreB chavesUsuarioParticipacao;

    private static CrudGrupo crudGrupo;
    private static CrudUsuario crudUsuario;
    private static CrudSugestao crudSugestao;
    private static CrudMensagem crudMensagem;

    private int idUsuarioLogado;

    public CrudParticipacao(int idUsuarioLogado) {
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

    public void inicializarBaseDados(CrudGrupo cg, CrudUsuario cu, CrudSugestao cs, CrudMensagem cm) {
        try {
            crudGrupo = cg;
            crudUsuario = cu;
            crudSugestao = cs;
            crudMensagem = cm;
            // tenta abrir os arquivos da base de dados caso existam;
            // se não existirem, são criados
            arquivoParticipacoes = new Arquivo<>(Participacao.class.getConstructor(), "participacoes.db");
            chavesGrupoParticipacao = new ArvoreB(100, "chavesGrupoParticipacao.idx");
            chavesUsuarioParticipacao = new ArvoreB(100, "chavesUsuarioParticipacao.idx");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados dos participantes.");
            e.printStackTrace();
        }
    }

    public void exibeMenuParticipantes() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > PARTICIPANTES\n");
            System.out.println("1: Listar");
            System.out.println("2: Remover");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.listarParticipantes();
                    break;
                case 2:
                    this.removerParticipacao();
                    break;
                case 0: // não é necessário fazer nada
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }

    // #region CREATE

    public void inserirParticipacao(int idGrupo, int idParticipante) throws Exception {
        // idAmigo definido como -1 representando que o sorteio ainda não foi realizado
        Participacao participacao = new Participacao(idParticipante, idGrupo, -1);

        int idParticipacao = arquivoParticipacoes.incluir(participacao);

        // insere chaves secudarias nas arvores
        chavesGrupoParticipacao.inserir(idGrupo, idParticipacao);
        chavesUsuarioParticipacao.inserir(idParticipante, idParticipacao);
    }

    // #endregion

    // #region READ

    public void listarParticipantes() throws Exception {
        System.out.println("Selecione qual grupo deseja listar os participantes:\n");
        Grupo grupo = crudGrupo.listarESelecionarGrupoAtivoCriadoPeloUsuario();
        if (grupo != null) {
            this.listarParticipantes(grupo, false);
        }
    }

    public Usuario listarParticipantes(Grupo grupo, boolean selecionarParticipante) throws Exception {
        Usuario usuarioSelecionado = null;

        Util.limparTela();

        if (grupo != null) {
            String statusSorteio = "";
            if (grupo.isSorteado()) {
                statusSorteio = "REALIZADO";
            } else {
                statusSorteio = "PENDENTE";
            }
            System.out.println("Status do sorteio: " + statusSorteio + "\n");
            System.out.println("PARTICIPANTES DO GRUPO \"" + grupo.getNome() + "\"\n");

            int[] idsParticipacoes = chavesGrupoParticipacao.lista(grupo.getID());
            Usuario[] usuariosParticipacoes = new Usuario[idsParticipacoes.length];

            if (idsParticipacoes.length == 0) {
                System.out.println("Ainda não existem participações ativas neste grupo.");
                Util.mensagemContinuar();
            } else {

                for (int i = 0; i < idsParticipacoes.length; i++) {
                    int idUsuario = ((Participacao) arquivoParticipacoes.buscar(idsParticipacoes[i])).getIdUsuario();
                    usuariosParticipacoes[i] = crudUsuario.obterUsuarioPorId(idUsuario);

                    System.out.println((i + 1) + ". " + usuariosParticipacoes[i].getNome());
                }

                if (selecionarParticipante) {
                    System.out.println("\nInsira 0 para voltar ao menu anterior");
                    System.out.print("\nNúmero do participante a ser removido: ");

                    int numero = Integer.parseInt(br.readLine()) - 1;
                    if (numero >= 0) {
                        usuarioSelecionado = usuariosParticipacoes[numero];
                    }
                } else {
                    Util.mensagemContinuar();
                }

            }
        }

        return usuarioSelecionado;
    }

    public void listarDadosParticipanteSorteado(Grupo grupo) throws Exception {
        int[] idsParticipacaoGrupo = chavesGrupoParticipacao.lista(grupo.getID());
        int[] idsParticipacaoUsuario = chavesUsuarioParticipacao.lista(this.idUsuarioLogado);

        int idParticipacaoUsuarioGrupo = -1;

        for (int i : idsParticipacaoGrupo) {
            for (int j : idsParticipacaoUsuario) {
                if (i == j) {
                    idParticipacaoUsuarioGrupo = i; // encontra o ID da participacao do usuário no grupo
                }
            }
        }

        if (idParticipacaoUsuarioGrupo != -1) {
            Participacao dadosParticipacao = (Participacao) arquivoParticipacoes.buscar(idParticipacaoUsuarioGrupo);
            Usuario usuarioSorteado = crudUsuario.obterUsuarioPorId(dadosParticipacao.getIdAmigo());

            System.out.println("Amigo(a) sorteado(a): " + usuarioSorteado.getNome() + "\n");
            System.out.println("Sugestões de presente cadastradas por ele(a):\n");
            crudSugestao.listarSugestoes(dadosParticipacao.getIdAmigo());
        } else {
            Util.mensagemTenteNovamente();
        }

        Util.mensagemContinuar();
    }

    public int[] obterIdsGruposEmQueOUsuarioParticipa(int id) throws Exception {
        int[] idsParticipacoesUsuario = chavesUsuarioParticipacao.lista(id);
        int[] idsGruposUsuarioParticipa = new int[idsParticipacoesUsuario.length];

        for (int i = 0; i < idsParticipacoesUsuario.length; i++) {
            idsGruposUsuarioParticipa[i] = ((Participacao) arquivoParticipacoes.buscar(idsParticipacoesUsuario[i]))
                    .getIdGrupo();
        }

        return idsGruposUsuarioParticipa;
    }

    // #endregion

    // #region UPDATE

    public void atualizarParticipacao(Participacao participacao) {
        try {
            arquivoParticipacoes.atualizar(participacao);
        } catch (Exception e) {
            Util.mensagemErroAtualizacao();
        }
    }

    // #endregion

    // #region DELETE

    public void removerParticipacao() throws Exception {
        System.out.println("Selecione qual grupo deseja remover participantes:\n");
        Grupo grupo = crudGrupo.listarESelecionarGrupoAtivoCriadoPeloUsuario();

        if (grupo != null) {
            Usuario participanteASerRemovido = this.listarParticipantes(grupo, true);
            int[] participacoesGrupoIds = chavesGrupoParticipacao.lista(grupo.getID());
            Participacao participacaoASerModificada = null, participacaoASerExcluida = null;
            // participacaoASerModificada é o objeto que trocará o amigo com o excluído

            if (participanteASerRemovido != null) {
                for (int i = 0; i < participacoesGrupoIds.length; i++) {
                    Participacao participacao = (Participacao) arquivoParticipacoes.buscar(participacoesGrupoIds[i]);
                    if (grupo.isSorteado() && participacao.getIdAmigo() == participanteASerRemovido.getID()) {
                        participacaoASerModificada = participacao.clone();
                    }
                    if (participacao.getIdUsuario() == participanteASerRemovido.getID()) {
                        participacaoASerExcluida = participacao.clone();
                    }
                }

                if (grupo.isSorteado()) {
                    if (participacaoASerModificada != null && participacaoASerExcluida != null) {
                        participacaoASerModificada.setIdAmigo(participacaoASerExcluida.getIdAmigo());
                    }

                    this.atualizarParticipacao(participacaoASerModificada);
                }

                this.removerParticipacao(participacaoASerExcluida);
            }
        } else {
            Util.limparTela();
        }
        Util.mensagemContinuar();
    }

    public void removerParticipacao(Participacao participacao) {
        try {
            arquivoParticipacoes.excluir(participacao.getID());
            chavesGrupoParticipacao.excluir(participacao.getIdGrupo(), participacao.getID());
            chavesUsuarioParticipacao.excluir(participacao.getIdUsuario(), participacao.getID());
            Util.mensagemSucessoExclusao();
        } catch (Exception e) {
            Util.mensagemErroExclusao();
        }
    }
    // #endregion

    public void exibirMenuParticipacoes() throws Exception {
        Grupo grupoSelecionado = crudGrupo.listarESelecionarGrupoAtivoQueOUsuarioParticipa();

        Util.limparTela();

        if (grupoSelecionado != null) {
            int opcao;

            do {
                System.out.println("INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPO\n");
                this.exibirDadosGrupoSelecionado(grupoSelecionado);

                opcao = this.exibirMenuParticipacaoGrupo();

                Util.limparTela();

                switch (opcao) {
                    case 1:
                        this.listarParticipantes(grupoSelecionado, false);
                        break;
                    case 2:
                        if (grupoSelecionado.isSorteado()) {
                            this.listarDadosParticipanteSorteado(grupoSelecionado);
                        } else {
                            System.out.println("O sorteio ainda não foi realizado.");
                            Util.mensagemContinuar();
                        }
                        break;
                    case 3:
                        crudMensagem.exibirMenuMensagem(grupoSelecionado);
                        break;
                    case 0: // não é preciso fazer nada
                        break;
                    default:
                        Util.mensagemTenteNovamente();
                        Util.mensagemContinuar();
                }
            } while (opcao != 0);
        }
    }

    private int exibirMenuParticipacaoGrupo() throws NumberFormatException, IOException {
        System.out.println("\n1: Visualizar participantes");
        System.out.println("2: Visualizar amigo sorteado");
        System.out.println("3: Ler/enviar mensagens ao grupo");
        System.out.println("0: Voltar para o menu anterior");

        System.out.print("\nIr para: ");
        int opcao = Integer.parseInt(br.readLine());

        return opcao;
    }

    private void exibirDadosGrupoSelecionado(Grupo grupoSelecionado) {
        System.out.println(grupoSelecionado.getNome());

        Date data = new Date(grupoSelecionado.getMomentoSorteio());
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = dataFormat.format(data);

        String statusSorteio = "";

        if (grupoSelecionado.isSorteado()) {
            statusSorteio = "ocorreu";
        } else {
            statusSorteio = "ocorrerá";
        }

        System.out.println("O sorteio " + statusSorteio + " dia " + dataString + ", às " + data.getHours() + ":"
                + data.getMinutes() + ".");

        System.out.println("Os presentes devem ter o valor aproximado de R$ " + grupoSelecionado.getValor() + ".");

        data = new Date(grupoSelecionado.getMomentoEncontro());
        dataString = dataFormat.format(data);
        System.out.println(
                "O encontro ocorrerá em " + dataString + ", às " + data.getHours() + ":" + data.getMinutes() + ",");
        System.out.println("em " + grupoSelecionado.getLocalEncontro());

        System.out.println("\nObservações: ");
        System.out.println(grupoSelecionado.getObservacoes());
    }

    public void realizarSorteio() throws Exception {
        Grupo grupoSelecionado = crudGrupo.listarESelecionarGrupoAtivoSemSorteioCriadoPeloUsuario();

        if (grupoSelecionado != null) {

            int[] idsParticipacoesGrupo = chavesGrupoParticipacao.lista(grupoSelecionado.getID());
            List<Participacao> participantes = new ArrayList<Participacao>();

            for (int i = 0; i < idsParticipacoesGrupo.length; i++) {
                participantes.add((Participacao) arquivoParticipacoes.buscar(idsParticipacoesGrupo[i]));
            }

            try {
                participantes = this.embaralhar(participantes); // embaralha participantes

                participantes.get(participantes.size() - 1).setIdAmigo(participantes.get(0).getIdUsuario());

                for (int i = 0; i < participantes.size() - 1; i++) {
                    int idAmigoSorteado = participantes.get(i + 1).getIdUsuario();
                    participantes.get(i).setIdAmigo(idAmigoSorteado);
                    arquivoParticipacoes.atualizar(participantes.get(i));
                }

                grupoSelecionado.setSorteado(true);
                crudGrupo.atualizar(grupoSelecionado, false);

                System.out.println("Sorteio realizado com sucesso.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro ao realizar o sorteio. Tente novamente.");
            }

            Util.mensagemContinuar();
        } else {
            Util.limparTela();
        }
    }

    private List<Participacao> embaralhar(List<Participacao> lista) {
        Random random = new Random();

        for (int i = 0; i < (lista.size() - 1); i++) {

            // sorteia um índice
            int j = random.nextInt(lista.size());

            // troca o conteúdo dos índices i e j do vetor
            Participacao temp = lista.get(i);
            lista.set(i, lista.get(j));
            lista.set(j, temp);
        }
        return lista;
    }
}