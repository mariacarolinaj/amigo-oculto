package cruds;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

import entidades.Convite;
import entidades.Grupo;
import util.Util;

public class CrudConvite {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Convite> arquivoConvites;

    private static ArvoreB chavesGruposUsuario;

    private static CrudGrupo crudGrupo;
    private int idUsuarioLogado;

    public CrudConvite(int idUsuarioLogado) {
        try {
            this.idUsuarioLogado = idUsuarioLogado;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inicializarBaseDados(int idUsuarioLogado, CrudGrupo cg) {
        try {
            this.idUsuarioLogado = idUsuarioLogado;
            crudGrupo = cg;
            // tenta abrir os arquivos da base de dados caso existam; se não existirem, são
            // criados
            arquivoConvites = new Arquivo<>(Convite.class.getConstructor(), "convites.db");
            chavesGruposUsuario = new ArvoreB(100, "chavesConvitesUsuario.idx");
        } catch (Exception e) {
            System.err.println("Não foi possível inicializar a base de dados dos convites.");
            e.printStackTrace();
        }
    }

    public void exibeMenuConvite() throws Exception {
        int opcao;

        do {
            System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > CONVITES\n");
            System.out.println("1: Listagem dos convites");
            System.out.println("2: Emissão de convites");
            System.out.println("3: Cancelamento de convites");
            System.out.println("0: Retornar ao menu anterior");
            System.out.print("\nIr para: ");

            opcao = Integer.parseInt(br.readLine());

            Util.limparTela();

            switch (opcao) {
                case 1:
                    this.listarConvites();
                    break;
                case 2:
                    this.emitirConvites();
                    break;
                case 3:
                    // this.cancelarConvites();
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

    public void emitirConvites() throws Exception {
        System.out.println("EMISSÃO DE CONVITES");
        System.out.println("Informe o grupo que deseja emitir convites:\n");

        Grupo grupoSelecionado = crudGrupo.listarESelecionarGrupoAtivoNaoSorteado();

        if (grupoSelecionado != null) {
            Util.limparTela();
            System.out.println("GRUPO \"" + grupoSelecionado.getNome() + "\"");
            System.out.println(
                    "Para sair do cadastro e emissão de convites, deixe o campo e-mail em branco e tecle ENTER\n");
            Convite[] convitesExistentes = this.obterConvitesExistentesPorGrupo(grupoSelecionado);
            String email;

            do {
                System.out.print("E-mail: ");
                email = br.readLine();

                Convite emailJaConvidado = this.verificarSeEmailJaFoiConvidado(email, convitesExistentes);
                if (!email.isEmpty()) {

                    if (emailJaConvidado != null) {
                        if (emailJaConvidado.getEstado() == 0 || emailJaConvidado.getEstado() == 1) {
                            System.out.println(
                                    "\nO e-mail informado já possui um convite emitido por esse grupo no estado pendente ou aceito.");
                        } else if (emailJaConvidado.getEstado() == 2 || emailJaConvidado.getEstado() == 3) {
                            System.out.println(
                                    "\nO e-mail informado já possui um convite emitido por este grupo no estado recusado ou cancelado.");
                            System.out.println("Deseja reemitir o convite? (S/N)");
                            char confirmacao = br.readLine().charAt(0);
                            if (confirmacao == 's' || confirmacao == 'S') {
                                emitirConvite(grupoSelecionado, email);
                            }
                        }
                    } else {
                        // e-mail ainda não foi utilizado em nenhum convite
                        emitirConvite(grupoSelecionado, email);
                    }
                }
            } while (!email.isEmpty());
        }

        Util.limparTela();
    }

    private void emitirConvite(Grupo grupoSelecionado, String email) {
        try {

            Convite conviteAEmitir = new Convite(grupoSelecionado.getID(), email, System.currentTimeMillis(), (byte) 0);
            int indiceConviteEmitido = arquivoConvites.incluir(conviteAEmitir);
            chavesGruposUsuario.inserir(grupoSelecionado.getID(), indiceConviteEmitido);
            System.out.println("\nConvite emitido com sucesso.\n");
        } catch (Exception e) {
            System.out.println("\nNão foi possível emitir o convite no momento. Tente novamente.");
        }
    }

    private Convite verificarSeEmailJaFoiConvidado(String email, Convite[] convitesExistentes) {
        for (Convite convite : convitesExistentes) {
            if (convite.getEmail().equals(email)) {
                return convite;
            }
        }

        return null;
    }

    private Convite[] obterConvitesExistentesPorGrupo(Grupo grupoSelecionado) throws IOException, Exception {
        int[] idsConvitesExistentes = chavesGruposUsuario.lista(grupoSelecionado.getID());
        Convite[] convitesExistentes = new Convite[idsConvitesExistentes.length];

        for (int i = 0; i < idsConvitesExistentes.length; i++) {
            convitesExistentes[i] = (Convite) arquivoConvites.buscar(idsConvitesExistentes[i]);
        }

        return convitesExistentes;
    }

    // #endregion

    // #region READ

    public void listarConvites() throws Exception {
        System.out.println("ESCOLHA O GRUPO:\n");
        Grupo grupoSelecionado = this.crudGrupo.listarESelecionarGrupoAtivo();

        if (grupoSelecionado != null) {
            this.exibirConvitesGrupo(grupoSelecionado);
        }
    }

    public void exibirConvitesGrupo(Grupo grupo) throws Exception {
        int[] idsConvites = chavesGruposUsuario.lista(grupo.getID());
        Convite[] convites = new Convite[idsConvites.length];

        for (int i = 0; i < idsConvites.length; i++) {
            convites[i] = (Convite) arquivoConvites.buscar(idsConvites[i]);
        }

        System.out.println("CONVITES DO GRUPO \"" + grupo.getNome() + "\"\n");

        if (convites.length == 0) {
            System.out.println("Ainda não foi emitido nenhum convite para este grupo.");
        }

        for (int i = 0; i < convites.length; i++) {
            Convite convite = convites[i];

            SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataString = dataFormat.format(new Date(convite.getMomentoConvite()));

            System.out.println((i + 1) + ". " + convite.getEmail() + " (" + dataString + " - "
                    + obterDescricaoEstadoConvite(convite.getEstado()) + ")");
        }

        Util.mensagemContinuar();
    }

    public String obterDescricaoEstadoConvite(short estado) {
        String descricao = null;

        switch (estado) {
            case 0:
                descricao = "pendente";
                break;
            case 1:
                descricao = "aceito";
                break;
            case 2:
                descricao = "recusado";
                break;
            case 3:
                descricao = "cancelado";
                break;
        }

        return descricao;
    }

    // #endregion

    // public int emissaoConvites() throws Exception {
    // int indice = -1;
    // int[] idsGrupos = chavesGruposUsuario.lista(this.idUsuarioLogado);
    // int indiceInsercao = 0;
    // int opcaoGrupo = -1;
    // Grupo[] grupos = new Grupo[idsGrupos.length];

    // for (int i = 0; i < idsGrupos.length; i++) {
    // Grupo grupo = (Grupo) arquivoGrupos.buscar(idsGrupos[i]);
    // if (grupo.isAtivo() && !grupo.isSorteado()) {
    // grupos[indiceInsercao++] = grupo;
    // }
    // }

    // for (int i = 0; i < indiceInsercao; i++) {
    // System.out.println((i + 1) + ". " + grupos[i].getNome());
    // }

    // System.out.print("Grupo: ");
    // opcaoGrupo = Integer.parseInt(br.readLine());
    // if (opcaoGrupo == 0) {
    // this.exibeMenuConvite();
    // }

    // return indice;
    // }

    // public void cancelarConvites() {

    // }

}