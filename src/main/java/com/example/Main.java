package com.example;

import com.example.dao.*;

import java.util.Scanner;

import com.example.model.Usuario;
import com.example.service.LoginService;

public class Main {
    private static LoginService loginService = new LoginService();
    private static UsuarioDao usuarioDao = new UsuarioDao();

    public static void main(String[] args) {
        clearConsole();
        try (Scanner input = new Scanner(System.in)) {
            System.out.printf("\n--- Tela de Login ---\n\n");
            System.out.printf("Email: ");
            String email = input.nextLine();
            System.out.printf("Senha: ");
            String senha = input.nextLine();

            Usuario usuarioLogado = loginService.autenticar(email, senha); // validando se o usuario existe e se esta ativo

            if (usuarioLogado == null) {
                System.out.printf("\nFalha na autenticação. Verifique seu email e senha.\n\n");
                return;
            }
            clearConsole();            
            menuBackoffice(usuarioLogado, input);
        }
    }

    private static void menuBackoffice(Usuario usuarioLogado, Scanner input) {
        boolean sair = false;

        while (!sair) {
            System.out.printf("--- Menu Principal Backoffice ---\n\n");
    
            System.out.printf("1. Listar Produtos\n2. Listar Usuários");
            System.out.printf("\n\nDigite a opção desejada: ");
            int opcao = input.nextInt();
            input.nextLine();
    
            switch (opcao) {
                case 1:
                    // função para listar produtos
                    break;
                case 2:
                    clearConsole();
                    listarUsuarios(usuarioLogado, input);
                    break;
                default:
                    clearConsole();
                    System.out.printf("Opção inválida.\n\n");
                    break;
            }
        }
    }

    // até o momento só lista os usuarios
    private static void listarUsuarios(Usuario usuarioLogado, Scanner input) {
        boolean sair = false;

        while (!sair) {
            System.out.println("--- Lista de Usuários ---\n");

            System.out.printf("%-5s %-20s %-30s %-10s %-10s%n", "ID", "Nome", "Email", "Status", "Grupo");
            for (Usuario usuario : usuarioDao.listarUsuarios()) {
                System.out.printf("%-5d %-20s %-30s %-10s %-10s%n",
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.isAtivo() ? "Ativo" : "Desativado",
                        usuario.getGrupo());
            }

            System.out.printf("\nDigite o ID do usuário para editar ou 0 para voltar ou i para incluir: ");

            int id = input.nextInt();
            input.nextLine();

            Usuario usuarioSelecionado = usuarioDao.buscarPorEmail(String.valueOf(id));

            if(id == 0){
                clearConsole();
                sair = true;
            } else if (usuarioSelecionado != null) {
                clearConsole();
                mostrarOpcoesUsuario(usuarioSelecionado, input);
            } else {
                clearConsole();
                System.out.printf("\nUsuário %d não encontrado.\n\n", id);
            }
        }
        menuBackoffice(usuarioLogado, input);
    }

    private static void mostrarOpcoesUsuario(Usuario usuarioSelecionado, Scanner scanner) {
        System.out.printf("--- Opções de Usuário ---\n");

        System.out.printf("\n1. Alterar Usuário\n2. Alterar Senha\n3. Ativar/Desativar Usuário\n0. Voltar\n");
        System.out.printf("\n\nDigite a opção desejada: ");
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


    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}