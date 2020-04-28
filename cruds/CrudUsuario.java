package cruds;

import java.io.*;
import entidades.*;

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

  private void inicializarBaseDados() {
    try {
      // tenta abrir os arquivos da base de dados caso existam; se não existirem, são
      // criados
      arquivoUsuarios = new Arquivo<>(Usuario.class.getConstructor(), "usuarios.db");
      arquivoChaveSecundariaUsuario = new Arquivo<>(ChaveSecundariaUsuario.class.getConstructor(),
          "chavesSecundariasUsuario.db");
    } catch (Exception e) {
      System.err.println("Não foi possível inicializar a base de dados dos usuários.");
      e.printStackTrace();
    }
  }

  public boolean logar() throws Exception {
    String email, senha;
    System.out.println("\nACESSO AO SISTEMA");
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    ChaveSecundariaUsuario chaveSecundaria = arquivoChaveSecundariaUsuario.buscarChaveSecundariaUsuario(email);

    if (chaveSecundaria != null) {
      Usuario usuario = (Usuario) arquivoUsuarios.buscar(chaveSecundaria.getIdUsuario());
      if (usuario != null) {
        if (usuario.getSenha().equals(senha)) {
          System.out.println("\nBem vindo(a), " + usuario.getNome());
          return true; // usuario logado
        } else {
          System.out.println("\nSenha incorreta!");
        }
      } else {
        // existe chave secundária, mas não tem usuário associado a ela
        System.out.println("\nO usuário informado não existe.");
      }
    } else {
      // usuário não encontrado nas chaves secundárias (e-mail incorreto/inexistente)
      System.out.println("\nO usuário informado não existe.");
    }

    return false; // não conseguiu logar
  }

  // #region CREATE

  public void criarUsuario() throws Exception {
    String nome, email, senha;
    System.out.print("\nNome: ");
    nome = br.readLine();
    System.out.print("\nE-mail: ");
    email = br.readLine();
    System.out.print("\nSenha: ");
    senha = br.readLine();

    Usuario usuario = new Usuario(nome, email, senha);

    try {
      // armazena novo usuário
      int novoId = arquivoUsuarios.incluir(usuario);
      usuario.setID(novoId);
      // cria indice secundario
      arquivoChaveSecundariaUsuario.incluir(new ChaveSecundariaUsuario(usuario.getID(), usuario.getEmail()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("\nUsuário cadastrado com sucesso.");
  }

  // #endregion

  // #region READ

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

  // #endregion

  // #region UPDATE
  public void atualizarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja atualizar: ");
    int id = Integer.parseInt(br.readLine());
    int opcao;
    Usuario usuarioExistente = (Usuario) arquivoUsuarios.buscar(id);

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
      System.out.println("UsuÁrio não encontrado.");
    }
  }

  private int exibirMenuAtualizacaoDados() throws IOException {
    int opcao;
    System.out.println("Informe qual atributo deseja alterar:");
    System.out.println("1: Nome");
    System.out.println("2: E-mail");
    System.out.println("3: Senha");
    System.out.println("0: Salvar alterações e voltar para o menu anterior");

    opcao = Integer.parseInt(br.readLine());
    return opcao;
  }

  // #endregion

  // #region DELETE
  public void deletarUsuario() throws Exception {
    System.out.println("Informe o ID do usuário que deseja remover: ");
    int id = Integer.parseInt(br.readLine());

    Usuario usuarioExistente = (Usuario) arquivoUsuarios.buscar(id);

    if (usuarioExistente != null) {
      boolean excluido = arquivoUsuarios.excluir(id);
      if (excluido) {
        System.out.println("O usuario de ID " + id + " foi excluído com sucesso.");
      } else {
        System.out.println("Não foi possível excluir este usuário. Tente novamente.");
      }
    } else {
      System.out.println("Usuário não encontrado.");
    }
  }
  // #endregion
}
