package cruds;

import java.io.*;
import java.util.Date;

import entidades.Grupo;
import util.Util;

public class CrudGrupo {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Grupo> arquivoGrupos;
    private static ArvoreB chavesGruposUsuario;

    private static CrudConvite crudConvite;
    private int idUsuarioLogado;

    public CrudGrupo(int idUsuarioLogado, CrudConvite cc) {
        try {
            this.inicializarBaseDados(idUsuarioLogado);
            crudConvite = cc;
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
            arquivoGrupos = new Arquivo<>(Grupo.class.getConstructor(), "grupos.db");
            chavesGruposUsuario = new ArvoreB(100, "chavesGrupoUsuario.idx");
        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados dos grupos.");
            e.printStackTrace();
        }
    }

    /*
     * Método exibeMenuGrupos(): Lista as opções do menu de grupos para o usuário
     * logado. Realiza leitura da opção desejada e redireciona para o método
     * correspondente.
     */

    public void exibeMenuGrupos() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > GRUPOS\n");
            System.out.println("1: Criação e gerenciamento de grupos");
            System.out.println("2: Participação nos grupos");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.exibeMenuGerenciamento();
                    break;
                case 2:
                    break;
                case 0: // não é necessário fazer nada
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }

    public void exibeMenuGerenciamento() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS\n");
            System.out.println("1: Grupos");
            System.out.println("2: Convites");
            System.out.println("3: Participantes");
            System.out.println("4: Sorteio");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.exibeMenuManipulacaoGrupos();
                    break;
                case 2:
                    crudConvite.exibeMenuConvite();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 0: // não é necessário fazer nada
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }

    public void exibeMenuManipulacaoGrupos() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > GRUPOS\n");
            System.out.println("1: Listar");
            System.out.println("2: Incluir");
            System.out.println("3: Alterar");
            System.out.println("4: Desativar");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.listarGrupos();
                    break;
                case 2:
                    this.criarGrupo();
                    break;
                case 3:
                    this.atualizarGrupo();
                    break;
                case 4:
                    this.desativarGrupo();
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

    /*
     * Método criarGrupo(): lê os dados e inclui no arquivo de grupos um novo grupo
     * vinculada ao usuário logado
     */

    public void criarGrupo() throws IOException {
        String nome, localEncontro, observacoes;
        long momentoSorteio, momentoEncontro;
        float valor;

        System.out.println("CADASTRO DE GRUPO\n");
        System.out.print("Nome: ");
        nome = br.readLine();

        if (nome.isEmpty()) {
            System.out.println("\nO nome do grupo é obrigatório. Tente novamente.");
        } else {
            System.out.print("\nData do sorteio (insira no formato DD-MM-AAAA; não deve ser inferior à data atual): ");
            String dataSorteio = br.readLine();
            // verifica se a data inserida é valida
            Date objetoDataSorteio = Util.validarEMontarData(dataSorteio);
            if (objetoDataSorteio != null && objetoDataSorteio.getTime() > System.currentTimeMillis()) {
                // data do sorteio válida. Continua cadastro
                momentoSorteio = objetoDataSorteio.getTime();

                System.out.print("\nValor médio dos presentes: ");
                valor = Float.parseFloat(br.readLine());

                System.out.print(
                        "\nData do encontro (insira no formato DD-MM-AAAA; não deve ser inferior à data do sorteio): ");
                String dataEncontro = br.readLine();

                Date objetoDataEncontro = Util.validarEMontarData(dataEncontro);

                if (objetoDataEncontro != null && objetoDataEncontro.getTime() > momentoSorteio) {
                    // data do encontro válida. Continua cadastro
                    momentoEncontro = objetoDataEncontro.getTime();

                    System.out.print("\nLocal do encontro: ");
                    localEncontro = br.readLine();

                    System.out.print("\nObservações adicionais: ");
                    observacoes = br.readLine();

                    Grupo grupo = new Grupo(this.idUsuarioLogado, nome, momentoSorteio, valor, momentoEncontro,
                            localEncontro, observacoes, false, true);

                    System.out.print("\nConfirmar inclusão do grupo " + nome + "? (S/N) ");
                    char confirmacao = br.readLine().charAt(0);
                    if (confirmacao == 's' || confirmacao == 'S') {
                        try {
                            // insere grupo no arquivo de grupos
                            int idGrupoInserido = arquivoGrupos.incluir(grupo);
                            // cria chave na arvore b+ do novo grupo ao usuário logado
                            chavesGruposUsuario.inserir(this.idUsuarioLogado, idGrupoInserido);
                            Util.mensagemSucessoCadastro();
                        } catch (Exception e) {
                            Util.mensagemErroCadastro();
                        }
                    }
                } else {
                    // data do encontro inferior à data do sorteio
                    System.out.println("\nA data do encontro não pode ser inferior à data do sorteio. Tente novamente");
                }
            } else {
                // data do sorteio inferior à data atual
                System.out.println("\nA data do sorteio não pode ser inferior à data atual. Tente novamente");
            }
        }

        Util.mensagemContinuar();
    }

    // #endregion

    // #region READ

    public void listarGrupos() throws Exception {
        int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
        int indiceInsercao = 0;
        Grupo[] grupos = new Grupo[idsGrupos.length];
        System.out.println("MEUS GRUPOS\n");

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            if (grupo.isAtivo()) {
                grupos[indiceInsercao++] = grupo;
            }
        }

        if (indiceInsercao == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        }

        for (int i = 0; i < indiceInsercao; i++) {
            System.out.println((i + 1) + ". " + grupos[i].getNome());
        }

        Util.mensagemContinuar();
    }

    public Grupo listarESelecionarGrupoAtivo() throws Exception {
        int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
        int indiceInsercao = 0;
        Grupo[] grupos = new Grupo[idsGrupos.length];
        Grupo grupoSelecionado = null;

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            if (grupo.isAtivo()) {
                grupos[indiceInsercao++] = grupo;
            }
        }

        if (indiceInsercao == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        } else {
            for (int i = 0; i < indiceInsercao; i++) {
                System.out.println((i + 1) + ". " + grupos[i].getNome());
            }
            System.out.print("\nGrupo: ");
            int indiceGrupoEscolhido = Integer.parseInt(br.readLine()) - 1;

            if (indiceGrupoEscolhido >= 0) {
                grupoSelecionado = grupos[indiceGrupoEscolhido].clone();
                Util.limparTela();
            } else {
                System.out.println("Índice inválido.");
                Util.mensagemContinuar();
            }
        }
        return grupoSelecionado;
    }

    public Grupo listarESelecionarGrupoAtivoNaoSorteado() throws Exception {
        int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
        int indiceInsercao = 0;
        Grupo[] grupos = new Grupo[idsGrupos.length];
        Grupo grupoSelecionado = null;

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            if (grupo.isAtivo() && grupo.getMomentoSorteio() > System.currentTimeMillis()) {
                grupos[indiceInsercao++] = grupo;
            }
        }

        if (indiceInsercao == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        } else {
            for (int i = 0; i < indiceInsercao; i++) {
                System.out.println((i + 1) + ". " + grupos[i].getNome());
            }
            System.out.print("\nGrupo: ");
            int indiceGrupoEscolhido = Integer.parseInt(br.readLine()) - 1;

            if (indiceGrupoEscolhido >= 0) {
                grupoSelecionado = grupos[indiceGrupoEscolhido].clone();
                Util.limparTela();
            } else {
                System.out.println("Índice inválido.");
                Util.mensagemContinuar();
            }
        }

        return grupoSelecionado;
    }

    // #endregion

    // #region UPDATE

    /*
     * Método atualizarGrupo(): realiza leitura do grupo que se deseja alterar as
     * informações. Depois, exibe um menu para que o usuário escolha qual informação
     * deseja alterar e, ao sair dessa sessão, armazena os dados alterados.
     */

    public void atualizarGrupo() throws Exception {
        int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
        int indiceInsercao = 0;
        Grupo[] grupos = new Grupo[idsGrupos.length];

        System.out.println("ATUALIZAR GRUPO\n");

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            if (grupo.isAtivo()) {
                grupos[indiceInsercao++] = grupo;
            }
        }

        if (indiceInsercao == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        } else {
            for (int i = 0; i < indiceInsercao; i++) {
                System.out.println((i + 1) + ". " + grupos[i].getNome());
            }
            System.out.print("\nInforme o número do grupo que deseja atualizar ou 0 para retornar ao menu anterior: ");
            int indiceGrupoAAtualizar = Integer.parseInt(br.readLine()) - 1;

            if (indiceGrupoAAtualizar >= 0) {
                Grupo grupoAtualizado = grupos[indiceGrupoAAtualizar].clone();
                Util.limparTela();
                this.exibeMenuAtualizacaoGrupo(grupos, indiceGrupoAAtualizar, grupoAtualizado);
            } else {
                System.out.println("Índice inválido.");
            }
        }

        Util.mensagemContinuar();
    }

    /*
     * Método exibirMenuAtualizacaoDados(): exibe menu com as opções de atualização
     * dos dados do usuário.
     */

    private int exibirMenuAtualizacaoDados() throws IOException {
        int opcao;
        System.out.println("Informe qual atributo deseja alterar:\n");
        System.out.println("1: Nome");
        System.out.println("2: Data do sorteio");
        System.out.println("3: Valor médio dos presentes");
        System.out.println("4: Data do encontro");
        System.out.println("5: Local do encontro");
        System.out.println("6: Observações");
        System.out.println("0: Salvar alterações e voltar para o menu anterior");

        System.out.print("\nIr para: ");
        opcao = Integer.parseInt(br.readLine());

        return opcao;
    }

    private void exibeMenuAtualizacaoGrupo(Grupo[] grupos, int indiceGrupoAAtualizar, Grupo grupoAtualizado)
            throws Exception {
        int opcao;
        do {
            opcao = exibirMenuAtualizacaoDados();

            switch (opcao) {
                case 1:
                    System.out.print("\nNovo nome: ");
                    String novoNome = br.readLine();
                    grupoAtualizado.setNome(novoNome);
                    Util.limparTela();
                    break;
                case 2:
                    System.out.print(
                            "\nNova data do sorteio (insira no formato DD-MM-AAAA; não deve ser inferior à data atual): ");
                    String dataSorteio = br.readLine();
                    // verifica se a data inserida é valida
                    Date objetoDataSorteio = Util.validarEMontarData(dataSorteio);
                    if (objetoDataSorteio != null && objetoDataSorteio.getTime() > System.currentTimeMillis()
                            && objetoDataSorteio.getTime() < grupoAtualizado.getMomentoEncontro()) {
                        // data do sorteio válida
                        grupoAtualizado.setMomentoSorteio(objetoDataSorteio.getTime());
                    } else {
                        System.out.println("\nData inválida. Tente novamente.");
                    }
                    Util.limparTela();
                    break;
                case 3:
                    System.out.print("\nNovo valor médio dos presentes: ");
                    float novoValor = Float.parseFloat(br.readLine());
                    grupoAtualizado.setValor(novoValor);
                    Util.limparTela();
                    break;
                case 4:
                    System.out.print(
                            "\nNova data do encontro (insira no formato DD-MM-AAAA; não deve ser inferior à data atual): ");
                    String dataEncontro = br.readLine();
                    // verifica se a data inserida é valida
                    Date objetoDataEncontro = Util.validarEMontarData(dataEncontro);
                    if (objetoDataEncontro != null
                            && objetoDataEncontro.getTime() > grupoAtualizado.getMomentoSorteio()) {
                        // data do encontro válida
                        grupoAtualizado.setMomentoEncontro(objetoDataEncontro.getTime());
                    } else {
                        System.out.println("\nData inválida. Tente novamente.");
                    }
                    Util.limparTela();
                    break;
                case 5:
                    System.out.print("\nNovo local do encontro: ");
                    String novoLocal = br.readLine();
                    grupoAtualizado.setLocalEncontro(novoLocal);
                    Util.limparTela();
                    break;
                case 6:
                    System.out.print("\nNovas observações: ");
                    String novasObservacoes = br.readLine();
                    grupoAtualizado.setObservacoes(novasObservacoes);
                    Util.limparTela();
                    break;
                case 0:
                    // verifica se houve alguma mudança; se sim, atualiza a sugestão
                    if (!grupoAtualizado.equals(grupos[indiceGrupoAAtualizar])) {
                        boolean atualizado = (boolean) arquivoGrupos.atualizar(grupoAtualizado);
                        if (atualizado) {
                            Util.mensagemSucessoAtualizacao();
                        } else {
                            Util.mensagemErroAtualizacao();
                        }
                    }
                    // volta pro menu anterior
                    break;
                default:
                    Util.mensagemTenteNovamente();
                    Util.mensagemContinuar();
            }
        } while (opcao != 0);
    }

    // #endregion

    // #region DELETE

    /* Realiza a desativação do grupo. Não apaga seus dados da base de dados. */

    public void desativarGrupo() throws Exception {
        int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
        int indiceInsercao = 0;
        Grupo[] grupos = new Grupo[idsGrupos.length];

        System.out.println("DESATIVAR GRUPO\n");

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            if (grupo.isAtivo()) {
                grupos[indiceInsercao++] = grupo;
            }
        }

        if (indiceInsercao == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        } else {
            for (int i = 0; i < indiceInsercao; i++) {
                System.out.println((i + 1) + ". " + grupos[i].getNome());
            }
            System.out.print("\nInforme o número do grupo que deseja desativar ou 0 para retornar ao menu anterior: ");
            int indiceGrupoADesativar = Integer.parseInt(br.readLine()) - 1;

            if (indiceGrupoADesativar >= 0) {
                Grupo grupoADesativar = grupos[indiceGrupoADesativar].clone();

                System.out.print("\nConfirmar desativação do grupo " + grupoADesativar.getNome() + "? (S/N) ");
                char confirmacao = br.readLine().charAt(0);

                if (confirmacao == 's' || confirmacao == 'S') {
                    grupoADesativar.setAtivo(false);
                    boolean desativado = arquivoGrupos.atualizar(grupoADesativar);
                    if (desativado) {
                        System.out.println("\nO grupo foi desativado com sucesso.");
                    } else {
                        System.out.println("\nNão foi possível desativar o grupo no momento. Tente novamente.");

                    }
                }
            }
        }
        Util.mensagemContinuar();
    }

    // #endregion
}