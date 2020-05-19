package cruds;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import entidades.Convite;
import entidades.Grupo;
import entidades.Usuario;
import util.Util;

public class CrudConvite {

    private static InputStream is = System.in;
    private static InputStreamReader isr = new InputStreamReader(is);
    private static BufferedReader br = new BufferedReader(isr);

    private static Arquivo<Convite> arquivoConvites;
    private static ArvoreB chavesGruposUsuario;
    private static ListaInvertida convitesPendentes;

    private static CrudGrupo crudGrupo;
    private static CrudUsuario crudUsuario;
    private static CrudParticipacao crudParticipacao;

    private Usuario usuarioLogado;

    public CrudConvite(Usuario usuarioLogado) {
        try {
            this.usuarioLogado = usuarioLogado;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inicializarBaseDados(CrudGrupo cg, CrudUsuario cu, CrudParticipacao cp) {
        try {
            crudGrupo = cg;
            crudUsuario = cu;
            crudParticipacao = cp;
            // tenta abrir os arquivos da base de dados caso existam; se não existirem, são
            // criados
            arquivoConvites = new Arquivo<>(Convite.class.getConstructor(), "convites.db");
            chavesGruposUsuario = new ArvoreB(100, "chavesConvitesUsuario.idx");
            convitesPendentes = new ListaInvertida(100, "convitesPendentes.idx");
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
                    this.cancelarConvite();
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
                    "Para sair do cadastro e emissão de convites, deixe o campo e-mail em branco e tecle ENTER");
            Convite[] convitesExistentes = this.obterConvitesExistentesPorGrupo(grupoSelecionado);
            String email;

            do {
                System.out.print("\nE-mail: ");
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
                            System.out.print("Deseja reemitir o convite? (S/N)");
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
            convitesPendentes.create(conviteAEmitir.getEmail(), indiceConviteEmitido);
            System.out.println("\nConvite emitido com sucesso.");
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

    public int obterQuantidadeConvitesPendentes() throws IOException {
        return convitesPendentes.read(this.usuarioLogado.getEmail()).length;
    }

    public void listarConvites() throws Exception {
        System.out.println("ESCOLHA O GRUPO:\n");
        Grupo grupoSelecionado = crudGrupo.listarESelecionarGrupoAtivo();

        if (grupoSelecionado != null) {
            this.exibirConvitesGrupo(grupoSelecionado);
        }
    }

    public void exibirConvitesGrupo(Grupo grupo) throws Exception {
        exibirConvitesGrupo(grupo, false);
    }

    public void exibirConvitesGrupo(Grupo grupo, boolean somenteConvitesAtivos) throws Exception {
        int[] idsConvites = chavesGruposUsuario.lista(grupo.getID());
        Convite[] convites = new Convite[idsConvites.length];
        int contadorIndice = 0;

        for (int i = 0; i < idsConvites.length; i++) {
            Convite convite = (Convite) arquivoConvites.buscar(idsConvites[i]);
            if ((somenteConvitesAtivos && convite.getEstado() == 1) || !somenteConvitesAtivos) {
                convites[contadorIndice++] = convite;
            }
        }

        if (somenteConvitesAtivos) {
            System.out.println("PARTICIPANTES DO GRUPO \"" + grupo.getNome() + "\"\n");
        } else {
            System.out.println("CONVITES DO GRUPO \"" + grupo.getNome() + "\"\n");
        }

        if (convites.length == 0) {
            if (somenteConvitesAtivos) {
                System.out.println("Ainda não existem participações ativas neste grupo.");
            } else {
                System.out.println("Ainda não foi emitido nenhum convite para este grupo.");
            }
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

    // #region UPDATE

    // #endregion

    // #region DELETE

    public void cancelarConvite() throws Exception {
        System.out.println("ESCOLHA O GRUPO:\n");
        Grupo grupoSelecionado = crudGrupo.listarESelecionarGrupoAtivo();
        int[] idsConvites = chavesGruposUsuario.lista(grupoSelecionado.getID());
        Convite[] convites = new Convite[idsConvites.length];

        Util.limparTela();

        for (int i = 0; i < idsConvites.length; i++) {
            convites[i] = (Convite) arquivoConvites.buscar(idsConvites[i]);
        }

        System.out.println("CONVITES DO GRUPO \"" + grupoSelecionado.getNome() + "\"\n");

        if (convites.length == 0) {
            System.out.println("Ainda não foi emitido nenhum convite para este grupo.");
        } else {
            for (int i = 0; i < convites.length; i++) {
                Convite convite = convites[i];

                SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dataString = dataFormat.format(new Date(convite.getMomentoConvite()));

                System.out.println((i + 1) + ". " + convite.getEmail() + " (" + dataString + " - "
                        + obterDescricaoEstadoConvite(convite.getEstado()) + ")");
            }
            System.out.println("\n0: Voltar para o menu anterior");
            System.out.print("\nÍndice do convite que deseja cancelar: ");
            int indiceConviteACancelar = Integer.parseInt(br.readLine()) - 1;

            if (indiceConviteACancelar >= 0 && indiceConviteACancelar < convites.length) {
                Convite conviteACancelar = convites[indiceConviteACancelar];
                System.out.print("\nConfirmar cancelamento do convite enviado para " + conviteACancelar.getEmail()
                        + "? (S/N): ");
                char confirmacao = br.readLine().charAt(0);

                if (confirmacao == 's' || confirmacao == 'S') {

                    try {
                        conviteACancelar.setEstado((byte) 3); // 3 = cancelado
                        arquivoConvites.atualizar(conviteACancelar);
                        convitesPendentes.delete(conviteACancelar.getEmail(), conviteACancelar.getID());
                        System.out.println("\nConvite cancelado com sucesso.");
                    } catch (Exception e) {
                        System.out.println(
                                "\nNão foi possível cancelar o convite selecionado no momento. Tente novamente.");
                    }
                }
            } else {
                System.out.println("\nÍndice inválido. Tente novamente.");
            }
        }

        Util.mensagemContinuar();
    }

    // #endregion

    public void verificarConvitesPendentes() throws Exception {
        int[] idsConvitesPendentes;
        char opcao = 'v';
        int indiceConvite = -1;
        
        do {
            idsConvitesPendentes = convitesPendentes.read(this.usuarioLogado.getEmail());
            Convite[] convites = new Convite[idsConvitesPendentes.length];
            Grupo[] grupos = new Grupo[idsConvitesPendentes.length];
            String[] nomesCriadoresGrupos = new String[idsConvitesPendentes.length];

            if (idsConvitesPendentes.length > 0) {
                for (int i = 0; i < idsConvitesPendentes.length; i++) {
                    convites[i] = (Convite) arquivoConvites.buscar(idsConvitesPendentes[i]);
                    grupos[i] = crudGrupo.obterGrupoPorId(convites[i].getIdGrupo());
                    nomesCriadoresGrupos[i] = crudUsuario.obterUsuarioPorId(grupos[i].getIdUsuarioCriador()).getNome();
                }

                System.out.println("VOCÊ FOI CONVIDADO PARA PARTICIPAR DOS GRUPOS ABAIXO.");
                System.out.println("ESCOLHA QUAL CONVITE DESEJA ACEITAR OU RECUSAR:\n");

                for (int i = 0; i < idsConvitesPendentes.length; i++) {
                    System.out.println((i + 1) + ". " + grupos[i].getNome());

                    Date data = new Date(convites[i].getMomentoConvite());
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dataString = dataFormat.format(data);

                    System.out.println(
                            "   Convidado em " + dataString + ", às " + data.getHours() + ":" + data.getMinutes());

                    System.out.println("   por " + nomesCriadoresGrupos[i] + "");
                }

                System.out.println("\nInsira 0 para voltar ao menu anterior");

                System.out.print("\nConvite: ");
                indiceConvite = Integer.parseInt(br.readLine()) - 1;

                Util.limparTela();

                if (indiceConvite >= 0 && indiceConvite < idsConvitesPendentes.length) {
                    System.out.println(grupos[indiceConvite].getNome());
                    Date data = new Date(convites[indiceConvite].getMomentoConvite());
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dataString = dataFormat.format(data);

                    System.out.println(
                            "Convidado em " + dataString + ", às " + data.getHours() + ":" + data.getMinutes());

                    nomesCriadoresGrupos[indiceConvite] = crudUsuario
                            .obterUsuarioPorId(grupos[indiceConvite].getIdUsuarioCriador()).getNome();
                    System.out.println("por " + nomesCriadoresGrupos[indiceConvite] + "\n");

                    System.out.print("Você deseja aceitar (A), recusar (R) ou voltar (V)? ");
                    opcao = br.readLine().charAt(0);

                    if (opcao == 'a' || opcao == 'A') {
                        try {

                            convites[indiceConvite].setEstado((byte) 1); // aceito
                            arquivoConvites.atualizar(convites[indiceConvite]);
                            crudParticipacao.inserirParticipacao(convites[indiceConvite], this.usuarioLogado.getID());
                            convitesPendentes.delete(this.usuarioLogado.getEmail(), convites[indiceConvite].getID());
                            System.out.println("\nConvite aceito com sucesso.");
                            Util.mensagemContinuar();
                        } catch (Exception e) {
                            System.out.println("\nNão foi possível aceitar o convite no momento. Tente novamente.");
                            Util.mensagemContinuar();
                        }
                    } else if (opcao == 'r' || opcao == 'R') {
                        try {
                            convites[indiceConvite].setEstado((byte) 2); // recuso
                            arquivoConvites.atualizar(convites[indiceConvite]);
                            convitesPendentes.delete(this.usuarioLogado.getEmail(), convites[indiceConvite].getID());
                            System.out.println("\nConvite recuso com sucesso.");
                            Util.mensagemContinuar();
                        } catch (Exception e) {
                            System.out.println("\nNão foi possível recusar o convite no momento. Tente novamente.");
                            Util.mensagemContinuar();
                        }
                    } else if (opcao == 'v' || opcao == 'V') {
                        Util.limparTela();
                    } else if (opcao != 'v' && opcao != 'V') {
                        System.out.println("\nOpção inválida. Tente novamente.");
                        Util.mensagemContinuar();
                    }
                }
            } else {
                System.out.println("Você não possui convites pendentes.");
                Util.mensagemContinuar();
            }
        } while ((opcao == 'v' || opcao == 'V' || opcao == 'a' || opcao == 'A' || opcao == 'r' || opcao == 'R')
                && indiceConvite >= 0 && idsConvitesPendentes.length > 0);
    }
}
