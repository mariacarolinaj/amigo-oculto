package cruds;

import java.io.*;

import entidades.Convite;
import entidades.Grupo;
import entidades.Participacao;
import util.Util;

public class CrudParticipacao {
    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Participacao> arquivoParticipacoes;

    private static CrudGrupo crudGrupo;
    private static CrudConvite crudConvite;
    private static CrudUsuario crudUsuario;

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

    public void inicializarBaseDados(CrudGrupo cg, CrudConvite cc, CrudUsuario cu) {
        try {
            // tenta abrir os arquivos da base de dados caso existam;
            // se não existirem, são criados
            arquivoParticipacoes = new Arquivo<>(Participacao.class.getConstructor(), "participacoes.db");
            crudGrupo = cg;
            crudConvite = cc;
            crudUsuario = cu;
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

    public void inserirParticipacao(Convite convite) throws Exception {
        int idUsuarioConvite = crudUsuario.obterDadosUsuarioPorEmail(convite.getEmail()).getID();
        Participacao participacao = new Participacao(idUsuarioConvite, convite.getIdGrupo(), -1);
        // o idAmigo é definido como -1 representando que o sorteio ainda não foi realizado
        arquivoParticipacoes.incluir(participacao);
    }

    // #endregion

    // #region READ

    public void listarParticipantes() throws Exception {
        System.out.println("Selecione qual grupo deseja listar os participantes:\n");

        Grupo grupo = crudGrupo.listarESelecionarGrupoAtivo();

        Util.limparTela();

        if (grupo != null) {
            crudConvite.exibirConvitesGrupo(grupo, true);
        }
    }

    // #endregion
}