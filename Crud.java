import java.io.*;

public class Crud {

  private static InputStream is = System.in;
  private static InputStreamReader isr = new InputStreamReader(is);
  private static BufferedReader br = new BufferedReader(isr);

  private static Arquivo<Usuario> arquivoUsuarios;
  private static Arquivo<ChaveSecundariaUsuario> arquivoChaveSecundariaUsuario;

  public static void main(String[] args) throws IOException {
    try {

      inicializarBaseDados();
      exibeMenuInicial();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void inicializarBaseDados() throws Exception, NoSuchMethodException {
    // tenta abrir os arquivos da base de dados caso existam; se não existirem, são
    // criados

    arquivoUsuarios = new Arquivo<>(Usuario.class.getConstructor(), "usuarios.db");
    arquivoChaveSecundariaUsuario = new Arquivo<>(ChaveSecundariaUsuario.class.getConstructor(),
        "chavesSecundariasUsuario.db");
  }

  private static void exibeMenuInicial() throws IOException, Exception {
    int opcao;

    do {
      System.out.println("\nAMIGO OCULTO 1.0\n");
      System.out.println("1: Acesso ao sistema");
      System.out.println("2: Novo usuário (primeiro acesso)");
      System.out.println("0: Sair\n");

      System.out.print("Ir para: ");

      opcao = Integer.parseInt(br.readLine());

      switch (opcao) {
        case 1:
          boolean sucessoAoLogar = logar();
          if (sucessoAoLogar) {
            exibeMenuUsuarioLogado();
          }
          break;
        case 2:
          criarUsuario();
          break;
        case 0: // sair do programa
          break;
        default:
          System.out.println("Opção inválida. Tente novamente.\n");
      }
    } while (opcao != 0);
  }

  public static boolean logar() throws Exception {
    String email, senha;
    System.out.println("\nACESSO AO SISTEMA");
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    ChaveSecundariaUsuario chaveSecundaria = arquivoChaveSecundariaUsuario.buscarChaveSecundariaUsuario(email);
    if (chaveSecundaria != null) {
      Usuario usuario = read(chaveSecundaria.getIdUsuario());
      if (usuario != null) {
        if (usuario.getSenha().equals(senha)) {
          // usuario logado
          System.out.println("\nBem vindo(a), " + usuario.getNome());
          return true;
        } else {
          System.out.println("\nSenha incorreta!\n");
        }
      } else {
        System.out.println("\nO usuário informado não existe.\n");
      }
    } else {
      System.out.println("\nO usuário informado não existe.\n");
    }

    return false;
  }

  public static void exibeMenuUsuarioLogado() throws NumberFormatException, IOException {
    int opcao;
    System.out.println("\nINICIO\n");
    System.out.println("1: Sugestões de presente");
    System.out.println("2: Grupos");
    System.out.println("3: Novos convites (0)");
    System.out.println("0: Sair");

    System.out.print("\nIr para: ");

    opcao = Integer.parseInt(br.readLine());

  }

  public static void criarUsuario() throws Exception {
    String nome, email, senha;
    System.out.print("\nNome: ");
    nome = br.readLine();
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    int idInserido = create(new Usuario(nome, email, senha)).getID();

    arquivoChaveSecundariaUsuario.incluir(new ChaveSecundariaUsuario(idInserido, email)); // cria indice secundario

    System.out.println("\nUsuário cadastrado com sucesso.");
  }

  public static Usuario create(Usuario usuario) {
    try {
      int novoId = arquivoUsuarios.incluir(usuario);
      usuario.setID(novoId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return usuario;
  }

  public static void buscarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja visualizar: ");
    int id = Integer.parseInt(br.readLine());

    Usuario usuario = read(id); // busca pelo usuario

    if (usuario != null) {
      System.out.println(
          "\nNome: " + usuario.getNome() + "\nE-mail: " + usuario.getEmail() + "\nSenha: " + usuario.getSenha());
    } else {
      System.out.println("Usuario não encontrado.");
    }
  }

  public static Usuario read(int id) throws Exception {
    return (Usuario) arquivoUsuarios.buscar(id);
  }

  public static void atualizarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja atualizar: ");
    int id = Integer.parseInt(br.readLine());
    int opcao;
    Usuario usuarioExistente = read(id);

    if (usuarioExistente != null) {
      Usuario usuarioAtualizado = usuarioExistente.clone();

      do {
        System.out.println("Informe qual atributo deseja alterar:");
        System.out.println("1: Nome");
        System.out.println("2: E-mail");
        System.out.println("3: Senha");
        System.out.println("0: Salvar alterações e voltar para o menu anterior");

        opcao = Integer.parseInt(br.readLine());

        switch (opcao) {
          case 1:
            System.out.println("Informe o novo nome do usuario: ");
            String novoNome = br.readLine();
            usuarioAtualizado.setNome(novoNome);

            break;
          case 2:
            System.out.println("Informe o novo e-mail do usuario: ");
            String novoEmail = br.readLine();
            usuarioAtualizado.setEmail(novoEmail);

            break;
          case 3:
            System.out.println("Informe a nova senha do usuario: ");
            String novaSenha = br.readLine();
            usuarioAtualizado.setSenha(novaSenha);

            break;
          case 0:
            // verifica se houve alguma mudança; se sim, atualiza o usuario
            if (!usuarioAtualizado.equals(usuarioExistente)) {
              boolean atualizado = update(usuarioAtualizado);
              if (atualizado) {
                System.out.println("Usuário atualizado com sucesso.");
              } else {
                System.out.println("Não foi possível atualizar os dados nesse momento. Tente novamente.");
              }
            }

            // volta pro menu anterior
            break;
          default:

            System.out.println("Opção inválida. Tente novamente.\n");
        }
      } while (opcao != 0);
    } else {
      System.out.println("Usuario não encontrado.");
    }
  }

  public static boolean update(Usuario usuario) throws Exception {
    return (boolean) arquivoUsuarios.atualizar(usuario);
  }

  public static void deletarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja remover: ");
    int id = Integer.parseInt(br.readLine());

    Usuario usuarioExistente = read(id);

    if (usuarioExistente != null) {
      boolean excluido = delete(id);
      if (excluido) {
        System.out.println("O usuario de ID " + id + " foi excluído com sucesso.");
      } else {
        System.out.println("Não foi possível excluir este usuário. Tente novamente.");
      }
    } else {
      System.out.println("Usuário não encontrado.");
    }
  }

  public static boolean delete(int idUsuario) throws Exception {
    return arquivoUsuarios.excluir(idUsuario);
  }

}
