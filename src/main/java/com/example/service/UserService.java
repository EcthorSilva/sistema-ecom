package com.example.service;

import java.util.Scanner;

import com.example.Main;
import com.example.dao.UsuarioDao;
import com.example.model.Usuario;

public class UserService {
    private UsuarioDao usuarioDao = new UsuarioDao();

    public void listarUsuarios(Usuario usuarioLogado, Scanner input) {
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

            String entrada = input.nextLine().trim();

            if (entrada.equals("0")) {
                Main.clearConsole();
                sair = true;
            } else if (entrada.equals("i")) {
                incluirUsuario(usuarioLogado, input);
            } else {
                int id = Integer.parseInt(entrada);
                Usuario usuarioSelecionado = usuarioDao.buscarPorId(id);

                if (usuarioSelecionado != null) {
                    Main.clearConsole();
                    listarDadosDoUsuario(usuarioSelecionado, usuarioLogado, input);
                } else {
                    System.out.printf("\nUsuário %d não encontrado.\n\n", id);
                }
            }
        }
    }

    public void incluirUsuario(Usuario usuarioLogado, Scanner input) {
        System.out.printf("--- Incluir Usuário ---\n\n");

        System.out.printf("Nome:");
        String nome = input.nextLine();
        System.out.printf("CPF: ");
        String cpf = input.nextLine();
        if (usuarioDao.buscarPorCpf(cpf) != null) {
            Main.clearConsole();
            System.out.printf("\nCPF já cadastrado.\n\n");
            return;
        }

        System.out.printf("Email: ");
        String email = input.nextLine();
        if (usuarioDao.buscarPorEmail(email) != null) {
            Main.clearConsole();
            System.out.printf("\nEmail já cadastrado.\n\n");
            return;
        }

        System.out.printf("Senha: ");
        String senha = input.nextLine();
        System.out.printf("Confirmar senha: ");
        String confsenha = input.nextLine();

        if (!senha.equals(confsenha)) {
            System.out.println("As senhas não coincidem. Tente novamente.");
            return;
        }

        System.out.printf("Grupo (Administrador/Estoquista): ");
        String grupo = input.nextLine();

        if (!grupo.equalsIgnoreCase("Administrador") && !grupo.equalsIgnoreCase("Estoquista")) {
            System.out.println("Grupo inválido.");
            return;
        }

        Usuario usuario = new Usuario(nome, cpf, email, LoginService.encriptarSenha(senha), grupo, true);
        usuarioDao.inserirUsuario(usuario);
        System.out.println("Usuário inserido com sucesso.");
    }

    public void listarDadosDoUsuario(Usuario usuarioSelecionado, Usuario usuarioLogado, Scanner scanner) {
        System.out.printf("--- Opções de Usuário ---\n\n");

        System.out.printf("%-5s %-20s %-20s %-20s %-10s %-10s%n", "ID", "Nome", "CPF", "Email", "Status", "Grupo");
        System.out.printf("%-5d %-20s %-20s %-20s %-10s %-10s%n",
                        usuarioSelecionado.getId(),
                        usuarioSelecionado.getNome(),
                        usuarioSelecionado.getCpf(),
                        usuarioSelecionado.getEmail(),
                        usuarioSelecionado.isAtivo() ? "Ativo" : "Desativado",
                        usuarioSelecionado.getGrupo());

        System.out.printf("\n1. Alterar Usuário\n2. Alterar Senha\n3. Ativar/Desativar Usuário\n0. Voltar\n");
        System.out.printf("\n\nDigite a opção desejada: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                Main.clearConsole();
                alterarUsuario(usuarioSelecionado, usuarioLogado, scanner);
                break;
            case 2:
                Main.clearConsole();
                alterarSenha(usuarioSelecionado, scanner);
                break;
            case 3:
                Main.clearConsole();
                ativarDesativarUsuario(usuarioSelecionado, scanner);
                break;
            case 0:
                Main.clearConsole();
                listarUsuarios(usuarioSelecionado, scanner);
                return;
            default:
                Main.clearConsole();
                System.out.println("Opção inválida.");
                break;
        }
    }

    public void alterarUsuario(Usuario usuarioSelecionado, Usuario usuarioLogado, Scanner scanner) {
        if (usuarioSelecionado.getId() == usuarioLogado.getId()) {
            System.out.println("Você não pode alterar seus próprios dados.");
            return;
        }
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

    public void alterarSenha(Usuario usuarioSelecionado, Scanner scanner) {
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

    public void ativarDesativarUsuario(Usuario usuarioSelecionado, Scanner scanner) {
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
