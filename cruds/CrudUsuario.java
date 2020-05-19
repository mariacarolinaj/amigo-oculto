package cruds;

import java.io.*;
import entidades.*;
import util.Util;

public class CrudUsuario {

  private static InputStream is = System.in;
  private static InputStreamReader isr = new InputStreamReader(is);
  private static BufferedReader br = new BufferedReader(isr);

  private static Arquivo<Usuario> arquivoUsuarios;
  private static Arquivo<ChaveSecundariaUsuario> arquivoChaveSecundariaUsuario;

  public CrudUsuario() {
    try {
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

      arquivoUsuarios = new Arquivo<>(Usuario.class.getConstructor(), "usuarios.db");
      arquivoChaveSecundariaUsuario = new Arquivo<>(ChaveSecundariaUsuario.class.getConstructor(),
          "chavesSecundariasUsuario.db");
    } catch (Exception e) {
      System.err.println("Não foi possível inicializar a base de dados dos usuários.");
      e.printStackTrace();
    }
  }

  /*
   * Método logar(): Realiza leitura do e-mail e senha, e busca pelo e-mail no
   * arquivo de chave secundária de usuário. Caso encontre um id relacionado ao
   * e-mail inserido, compara a senha informada com a existente: se o login for
   * realizado com sucesso retorna true; caso contrário retorna false.
   */

  public Usuario logar() throws Exception {
    String email, senha;
    System.out.println("ACESSO AO SISTEMA");
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    ChaveSecundariaUsuario chaveSecundaria = arquivoChaveSecundariaUsuario.buscarChaveSecundariaUsuario(email);

    Util.limparTela();

    if (chaveSecundaria != null) {
      Usuario usuario = (Usuario) arquivoUsuarios.buscar(chaveSecundaria.getIdUsuario());
      if (usuario != null) {
        if (usuario.getSenha().equals(senha)) {
          System.out.println("Bem vindo(a), " + usuario.getNome() + "\n");
          return usuario; // usuario logado
        } else {
          System.out.println("Senha incorreta!");
        }
      } else {
        // existe chave secundária, mas não tem usuário associado a ela
        System.out.println("O usuário informado não existe.");
      }
    } else {
      // usuário não encontrado nas chaves secundárias (e-mail incorreto/inexistente)
      System.out.println("O usuário informado não existe.");
    }

    Util.mensagemContinuar();

    return null; // não conseguiu logar
  }

  // #region CREATE

  /*
   * Método criarUsuario(): Realiza leitura do nome, e-mail e senha. Verifica se
   * já existe um usuário utilizando o e-mail informado e caso não exista,
   * armazena os dados do novo usuário.
   */

  public void criarUsuario() throws Exception {
    String nome, email, senha;
    System.out.println("NOVO USUÁRIO");
    System.out.print("\nNome: ");
    nome = br.readLine();
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    Usuario usuario = new Usuario(nome, email, senha);

    // verifica se o e-mail já foi utilizado
    boolean emailJaUtilizado = arquivoChaveSecundariaUsuario.buscarChaveSecundariaUsuario(email) != null;

    if (!emailJaUtilizado) {
      try {
        // armazena novo usuário
        int novoId = arquivoUsuarios.incluir(usuario);
        usuario.setID(novoId);
        // cria indice secundario
        arquivoChaveSecundariaUsuario.incluir(new ChaveSecundariaUsuario(usuario.getID(), usuario.getEmail()));
        Util.mensagemSucessoCadastro();
      } catch (Exception e) {
        Util.mensagemErroCadastro();
      }
    } else {
      System.out.print("\nO e-mail informado já está associado a outro usuário.");
      System.out.println("Tente novamente com um e-mail válido.");
    }

    Util.mensagemContinuar();
  }

  // #endregion

  // #region READ

  /*
   * Método buscarUsuario(): Realiza leitura de um ID e, caso o encontre no
   * arquivo de usuários, exibe seus dados na tela.
   */

  public void buscarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja visualizar: ");
    int id = Integer.parseInt(br.readLine());

    Usuario usuario = (Usuario) arquivoUsuarios.buscar(id); // busca de usuário pelo ID

    if (usuario != null) {
      System.out.println("\nNome: " + usuario.getNome());
      System.out.println("E-mail: " + usuario.getEmail());
      System.out.println("Senha: " + usuario.getSenha());
    } else {
      System.out.println("Usuario não encontrado.");
    }
  }

  public Usuario obterUsuarioPorId(int id) throws Exception {
    return (Usuario) arquivoUsuarios.buscar(id);
  }

  public ChaveSecundariaUsuario obterDadosUsuarioPorEmail(String email) throws Exception {
    return (ChaveSecundariaUsuario) arquivoChaveSecundariaUsuario.buscarChaveSecundariaUsuario(email);
  }

  // #endregion

  // #region UPDATE

  /*
   * Método atualizarUsuario(): realiza leitura do ID do usuário que se deseja
   * alterar as informações. Caso o usuário exista, exibe um menu para que ele
   * escolha qual informação deseja alterar e, ao sair dessa sessão, armazena os
   * dados alterados.
   */

  public void atualizarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja atualizar: ");
    int id = Integer.parseInt(br.readLine());
    int opcao;
    Usuario usuarioExistente = (Usuario) arquivoUsuarios.buscar(id);

    Util.limparTela();

    if (usuarioExistente != null) {
      Usuario usuarioAtualizado = usuarioExistente.clone();

      do {
        opcao = exibirMenuAtualizacaoDados();

        switch (opcao) {
          case 1:
            System.out.println("Novo nome: ");
            String novoNome = br.readLine();
            usuarioAtualizado.setNome(novoNome);

            break;
          case 2:
            System.out.println("Novo e-mail: ");
            String novoEmail = br.readLine();
            usuarioAtualizado.setEmail(novoEmail);

            break;
          case 3:
            System.out.println("Nova senha: ");
            String novaSenha = br.readLine();
            usuarioAtualizado.setSenha(novaSenha);

            break;
          case 0:
            // verifica se houve alguma mudança; se sim, atualiza o usuario
            if (!usuarioAtualizado.equals(usuarioExistente)) {
              boolean atualizado = (boolean) arquivoUsuarios.atualizar(usuarioAtualizado);
              if (atualizado) {
                Util.mensagemSucessoAtualizacao();
              } else {
                Util.mensagemErroAtualizacao();
              }
              Util.mensagemContinuar();
            }
            // volta pro menu anterior
            break;
          default:
            Util.mensagemTenteNovamente();
            Util.mensagemContinuar();
        }
      } while (opcao != 0);
    } else {
      System.out.println("Usuário não encontrado.");
      Util.mensagemContinuar();
    }
  }

  /*
   * Método exibirMenuAtualizacaoDados(): exibe menu com as opções de atualização
   * dos dados do usuário.
   */

  private int exibirMenuAtualizacaoDados() throws IOException {
    int opcao;
    System.out.println("Informe qual atributo deseja alterar:");
    System.out.println("1: Nome");
    System.out.println("2: E-mail");
    System.out.println("3: Senha");
    System.out.println("0: Salvar alterações e voltar para o menu anterior");

    opcao = Integer.parseInt(br.readLine());

    Util.limparTela();

    return opcao;
  }

  // #endregion

  // #region DELETE

  /*
   * Método deletarUsuario(): realiza leitura do ID do usuário que deseja excluir.
   * Busca por ele no arquivo de usuários e, caso encontre seu registro o apaga e
   * depois apaga sua chave secundária do arquivo correspondente.
   */

  public void deletarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja remover: ");
    int id = Integer.parseInt(br.readLine());

    Usuario usuarioExistente = (Usuario) arquivoUsuarios.buscar(id);

    Util.limparTela();

    if (usuarioExistente != null) {
      boolean excluido = arquivoUsuarios.excluir(id);
      // procura pelo ID correspondente na chave secundária e em seguida a apaga
      int idChaveSecundariaAExcluir = arquivoChaveSecundariaUsuario
          .buscarChaveSecundariaUsuario(usuarioExistente.getEmail()).getID();
      arquivoChaveSecundariaUsuario.excluir(idChaveSecundariaAExcluir);

      if (excluido) {
        Util.mensagemSucessoExclusao();
      } else {
        Util.mensagemErroExclusao();
      }
    } else {
      System.out.println("Usuário não encontrado.");
    }

    Util.mensagemContinuar();
  }

  // #endregion
}
