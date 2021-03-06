package cruds;

import java.io.*;
import entidades.*;
import util.Util;

public class CrudSugestao {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Sugestao> arquivoSugestoes;
    private static ArvoreB chavesSugestaoUsuario;

    private int idUsuarioLogado;

    public CrudSugestao(int idUsuarioLogado) {
        try {
            this.idUsuarioLogado = idUsuarioLogado;
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
            chavesSugestaoUsuario = new ArvoreB(100, "chavesSugestaoUsuario.idx");

        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados das sugestões.");
            e.printStackTrace();
        }
    }

    /*
     * Método exibeMenuSugestoes(): Lista as opções do menu de sugestões para o
     * usuário logado. Realiza leitura da opção desejada e redireciona para o método
     * correspondente.
     */

    public void exibeMenuSugestoes() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > SUGESTÕES\n");
            System.out.println("1: Listar");
            System.out.println("2: Incluir");
            System.out.println("3: Alterar");
            System.out.println("4: Excluir");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.listar();
                    break;
                case 2:
                    this.criarSugestao();
                    break;
                case 3:
                    this.atualizarSugestao();
                    break;
                case 4:
                    this.deletarSugestao();
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
     * Método criarSugestao(): lê os dados e inclui no arquivo de sugestões uma nova
     * sugestão de presente vinculada ao usuário logado
     */

    public void criarSugestao() throws IOException {
        String produto, loja, observacoes;
        float preco;

        System.out.println("CADASTRO DE SUGESTÃO\n");
        System.out.print("Produto: ");
        produto = br.readLine();

        if (produto.isEmpty()) {
            System.out.println("\nO nome do produto é obrigatório. Tente novamente.");
        } else {
            System.out.print("\nLoja: ");
            loja = br.readLine();

            System.out.print("\nPreço: ");
            preco = Float.parseFloat(br.readLine());

            System.out.print("\nObservações: ");
            observacoes = br.readLine();

            Sugestao sugestao = new Sugestao(produto, loja, preco, observacoes);

            System.out.print("\nConfirmar inclusão de " + produto + "? (S/N) ");
            char confirmacao = br.readLine().charAt(0);
            if (confirmacao == 's' || confirmacao == 'S') {
                try {
                    // insere sugestão no arquivo de sugestões
                    int idSugestaoInserida = arquivoSugestoes.incluir(sugestao);
                    // cria chave na arvore b+ da nova sugestão ao usuário logado
                    chavesSugestaoUsuario.inserir(this.idUsuarioLogado, idSugestaoInserida);
                    Util.mensagemSucessoCadastro();
                } catch (Exception e) {
                    Util.mensagemErroCadastro();
                }
            }
        }

        Util.mensagemContinuar();
    }

    // #endregion

    // #region READ

    /*
     * Método listar(): exibe na tela as sugestões previamente cadastradas pelo
     * usuário
     */

    public void listar() throws Exception {
        System.out.println("MINHAS SUGESTÕES\n");
        this.listarSugestoes(this.idUsuarioLogado);
    }

    public void listarSugestoes(int idUsuario) throws Exception {
        int[] idsSugestoes = chavesSugestaoUsuario.lista(idUsuario);

        if (idsSugestoes.length == 0) {
            System.out.println("Ainda não foi cadastrada nenhuma sugestão.");
        }

        for (int i = 0; i < idsSugestoes.length; i++) {
            Sugestao sugestao = (Sugestao) arquivoSugestoes.buscar(idsSugestoes[i]);
            System.out.println((i + 1) + ". " + sugestao.getProduto());
            System.out.println("   " + sugestao.getLoja());
            System.out.println("   R$ " + sugestao.getValor());
            System.out.println("   " + sugestao.getObservacoes());
        }
        Util.mensagemContinuar();
    }

    // #endregion

    // #region UPDATE

    /*
     * Método atualizarSugestao(): realiza leitura da sugestão que se deseja alterar
     * as informações. Depois, exibe um menu para que o usuário escolha qual
     * informação deseja alterar e, ao sair dessa sessão, armazena os dados
     * alterados.
     */

    public void atualizarSugestao() throws Exception {
        int[] idsSugestoes = chavesSugestaoUsuario.lista(this.idUsuarioLogado);
        Sugestao[] sugestoes = new Sugestao[idsSugestoes.length];
        System.out.println("ATUALIZAR SUGESTÃO\n");

        if (idsSugestoes.length == 0) {
            System.out.println("Ainda não foi cadastrada nenhuma sugestão.");
        } else {
            for (int i = 0; i < idsSugestoes.length; i++) {
                sugestoes[i] = (Sugestao) arquivoSugestoes.buscar(idsSugestoes[i]);
                System.out.println((i + 1) + ". " + sugestoes[i].getProduto());
            }

            System.out
                    .print("\nInforme o número da sugestão que deseja atualizar ou 0 para retornar ao menu anterior: ");
            int indiceSugestaoAAtualizar = Integer.parseInt(br.readLine()) - 1;

            if (indiceSugestaoAAtualizar >= 0) {
                Sugestao sugestaoAtualizada = sugestoes[indiceSugestaoAAtualizar].clone();
                Util.limparTela();
                exibeMenuAtualizacaoSugestao(sugestoes, indiceSugestaoAAtualizar, sugestaoAtualizada);
            } else {
                System.out.println("Índice inválido.");
            }

            Util.mensagemContinuar();
        }
    }

    private void exibeMenuAtualizacaoSugestao(Sugestao[] sugestoes, int indiceSugestaoAAtualizar,
            Sugestao sugestaoAtualizada) throws IOException, Exception {
        int opcao;
        do {
            opcao = exibirMenuAtualizacaoDados();

            switch (opcao) {
                case 1:
                    System.out.print("\nNovo produto: ");
                    String novoProduto = br.readLine();
                    sugestaoAtualizada.setProduto(novoProduto);
                    Util.limparTela();
                    break;
                case 2:
                    System.out.print("\nNova loja: ");
                    String novaLoja = br.readLine();
                    sugestaoAtualizada.setLoja(novaLoja);
                    Util.limparTela();
                    break;
                case 3:
                    System.out.print("\nNovo valor: ");
                    float novoValor = Float.parseFloat(br.readLine());
                    sugestaoAtualizada.setValor(novoValor);
                    Util.limparTela();
                    break;
                case 4:
                    System.out.print("\nNovas observações: ");
                    String novasObservacoes = br.readLine();
                    sugestaoAtualizada.setObservacoes(novasObservacoes);
                    Util.limparTela();
                    break;
                case 0:
                    // verifica se houve alguma mudança; se sim, atualiza a sugestão
                    if (!sugestaoAtualizada.equals(sugestoes[indiceSugestaoAAtualizar])) {
                        boolean atualizado = (boolean) arquivoSugestoes.atualizar(sugestaoAtualizada);
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

    /*
     * Método exibirMenuAtualizacaoDados(): exibe menu com as opções de atualização
     * dos dados do usuário.
     */

    private int exibirMenuAtualizacaoDados() throws IOException {
        int opcao;
        System.out.println("Informe qual atributo deseja alterar:\n");
        System.out.println("1: Produto");
        System.out.println("2: Loja");
        System.out.println("3: Valor");
        System.out.println("4: Observações");
        System.out.println("0: Salvar alterações e voltar para o menu anterior");

        System.out.print("\nIr para: ");
        opcao = Integer.parseInt(br.readLine());

        return opcao;
    }
    // #endregion

    // #region DELETE

    /*
     * Método deletarSugestao(): realiza leitura da sugestão que deseja excluir.
     * Busca por ela no arquivo de sugestões e, caso encontre seu registro o apaga e
     * depois apaga sua chave secundária do arquivo correspondente.
     */

    public void deletarSugestao() throws Exception {
        int[] idsSugestoes = chavesSugestaoUsuario.lista(this.idUsuarioLogado);
        Sugestao[] sugestoes = new Sugestao[idsSugestoes.length];
        System.out.println("EXCLUIR SUGESTÃO\n");

        if (idsSugestoes.length == 0) {
            System.out.println("Ainda não foi cadastrada nenhuma sugestão.");
        } else {
            for (int i = 0; i < idsSugestoes.length; i++) {
                sugestoes[i] = (Sugestao) arquivoSugestoes.buscar(idsSugestoes[i]);
                System.out.println((i + 1) + ". " + sugestoes[i].getProduto());
            }

            System.out.print("\nInforme o número da sugestão que deseja excluir ou 0 para retornar ao menu anterior: ");
            int indiceSugestaoAExcluir = Integer.parseInt(br.readLine()) - 1;

            if (indiceSugestaoAExcluir >= 0) {
                Sugestao sugestaoAExcluir = sugestoes[indiceSugestaoAExcluir].clone();

                System.out.print("\nConfirmar inclusão de " + sugestaoAExcluir.getProduto() + "? (S/N) ");
                char confirmacao = br.readLine().charAt(0);

                if (confirmacao == 's' || confirmacao == 'S') {
                    boolean excluido = arquivoSugestoes.excluir(sugestaoAExcluir.getID())
                            && chavesSugestaoUsuario.excluir(this.idUsuarioLogado, sugestaoAExcluir.getID());

                    if (excluido) {
                        Util.mensagemSucessoExclusao();
                    } else {
                        Util.mensagemErroExclusao();
                    }
                }
            }

        }
        Util.mensagemContinuar();
    }
    // #endregion
}