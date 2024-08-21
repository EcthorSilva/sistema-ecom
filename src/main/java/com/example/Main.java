package com.example;

import com.example.dao.*;

import java.util.Scanner;

import com.example.model.Usuario;
import com.example.service.LoginService;

public class Main {
    private static LoginService loginService = new LoginService();
    private static UsuarioDao usuarioDao = new UsuarioDao();

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.printf("\n=== Tela de Login ===\n\n");
            System.out.printf("Email: ");
            String email = input.nextLine();
            System.out.printf("Senha: ");
            String senha = input.nextLine();

            Usuario usuarioLogado = loginService.autenticar(email, senha);

            if (usuarioLogado == null) {
                System.out.printf("\nFalha na autenticação. Verifique seu email e senha.\n\n");
                return;
            }

            System.out.println("Bem-vindo, " + usuarioLogado.getNome());
            mostrarMenu(usuarioLogado, input);
        }
    }

    private static void mostrarMenu(Usuario usuarioLogado, Scanner input) {
        System.out.println("=== Menu Principal ===");
        System.out.println("1. Listar Produtos (PERSONAS)");
        if (usuarioLogado.getGrupo().equalsIgnoreCase("Administrador")) {
            System.out.println("2. Listar Usuários");
        }

        int opcao = input.nextInt();
        input.nextLine();

        switch (opcao) {
            case 1:
                //listarProdutos();
                break;
            case 2:
                if (usuarioLogado.getGrupo().equalsIgnoreCase("Administrador")) {
                    listarUsuarios(input);
                } else {
                    System.out.println("Opção inválida.");
                }
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    private static void listarUsuarios(Scanner input) {
        System.out.println("=== Lista de Usuários ===");
        for (Usuario usuario : usuarioDao.listarUsuarios()) {
            System.out.println("ID: " + usuario.getId() + ", Nome: " + usuario.getNome() + ", Email: " + usuario.getEmail() + ", Status: " + (usuario.isAtivo() ? "Ativo" : "Desativado") + ", Grupo: " + usuario.getGrupo());
        }

        System.out.print("Digite o ID do usuário para editar ou 0 para voltar: ");
        int id = input.nextInt();
        input.nextLine();

        if (id == 0) {
            return;
        }

        Usuario usuarioSelecionado = usuarioDao.buscarPorEmail(String.valueOf(id));
        if (usuarioSelecionado != null) {
            mostrarOpcoesUsuario(usuarioSelecionado, input);
        } else {
            System.out.println("Usuário não encontrado.");
        }
    }

    private static void mostrarOpcoesUsuario(Usuario usuarioSelecionado, Scanner scanner) {
        System.out.println("=== Opções de Usuário ===");
        System.out.println("1. Alterar Usuário");
        System.out.println("2. Alterar Senha");
        System.out.println("3. Ativar/Desativar Usuário");
        System.out.println("0. Voltar");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                alterarUsuario(usuarioSelecionado, scanner);
                break;
            case 2:
                alterarSenha(usuarioSelecionado, scanner);
                break;
            case 3:
                ativarDesativarUsuario(usuarioSelecionado, scanner);
                break;
            case 0:
                return;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    private static void alterarUsuario(Usuario usuarioSelecionado, Scanner scanner) {
        System.out.print("Novo Nome: ");
        usuarioSelecionado.setNome(scanner.nextLine());
        System.out.print("Novo CPF: ");
        usuarioSelecionado.setCpf(scanner.nextLine());

        System.out.print("Novo Grupo (Administrador/Estoquista): ");
        String grupo = scanner.nextLine();
        if (!grupo.equalsIgnoreCase("Administrador") && !grupo.equalsIgnoreCase("Estoquista")) {
            System.out.println("Grupo inválido.");
            return;
        }
        usuarioSelecionado.setGrupo(grupo);

        usuarioDao.atualizarUsuario(usuarioSelecionado);
        System.out.println("Usuário atualizado com sucesso.");
    }

    private static void alterarSenha(Usuario usuarioSelecionado, Scanner scanner) {
        System.out.print("Nova Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Confirme a Nova Senha: ");
        String confirmacaoSenha = scanner.nextLine();

        if (!senha.equals(confirmacaoSenha)) {
            System.out.println("As senhas não coincidem.");
            return;
        }

        usuarioSelecionado.setSenha(LoginService.encriptarSenha(senha));
        usuarioDao.atualizarSenha(usuarioSelecionado.getId(), usuarioSelecionado.getSenha());
        System.out.println("Senha atualizada com sucesso.");
    }

    private static void ativarDesativarUsuario(Usuario usuarioSelecionado, Scanner scanner) {
        System.out.println("Status atual: " + (usuarioSelecionado.isAtivo() ? "Ativo" : "Desativado"));
        System.out.print("Deseja " + (usuarioSelecionado.isAtivo() ? "Desativar" : "Ativar") + " este usuário? (S/N): ");
        String opcao = scanner.nextLine();

        if (opcao.equalsIgnoreCase("S")) {
            usuarioSelecionado.setAtivo(!usuarioSelecionado.isAtivo());
            usuarioDao.atualizarUsuario(usuarioSelecionado);
            System.out.println("Usuário " + (usuarioSelecionado.isAtivo() ? "ativado" : "desativado") + " com sucesso.");
        }
    }
}