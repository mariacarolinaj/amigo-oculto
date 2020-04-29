package cruds;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import entidades.Grupo;
import util.Util;

public class CrudGrupo {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Grupo> arquivoGrupos;
    private static ArvoreB chavesGruposUsuario;

    private int idUsuarioLogado;

    public CrudGrupo(int idUsuarioLogado) {
        try {
            this.inicializarBaseDados(idUsuarioLogado);
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
                            localEncontro, observacoes, false, false);

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
        System.out.println("MEUS GRUPOS\n");

        if (idsGrupos.length == 0) {
            System.out.println("Você não faz parte de nenhum grupo ativo no momento.");
        }

        for (int i = 0; i < idsGrupos.length; i++) {
            Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
            System.out.println((i + 1) + ". " + grupo.getNome());
        }
        Util.mensagemContinuar();
    }

    // #endregion

}