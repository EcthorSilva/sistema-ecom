package com.example;

import com.example.model.Usuario;
import com.example.service.LoginService;
import com.example.service.UserService;

import java.util.Scanner;

public class Main {
    private static LoginService loginService = new LoginService();
    private static UserService userService = new UserService();
    
    public static void main(String[] args) {
        clearConsole();
        try (Scanner input = new Scanner(System.in)) {
            System.out.printf("\n--- Tela de Login ---\n\n");
            System.out.printf("Email: ");
            String email = input.nextLine();
            System.out.printf("Senha: ");
            String senha = input.nextLine();

            Usuario usuarioLogado = loginService.autenticar(email, senha);

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

            if(usuarioLogado.getGrupo().equalsIgnoreCase("administrador")) {
                System.out.printf("1. Listar Produtos\n2. Listar Usuários\n3. Sair");
            } else {
                System.out.printf("1. Listar Produtos\n2. Sair");
            }
            // System.out.printf("1. Listar Produtos\n2. Listar Usuários");
            System.out.printf("\n\nDigite a opção desejada: ");
            int opcao = input.nextInt();
            input.nextLine();

            switch (opcao) {
                case 1:
                    // função para listar produtos
                    break;
                case 2:
                    clearConsole();
                    userService.listarUsuarios(usuarioLogado, input);
                    break;
                default:
                    clearConsole();
                    System.out.printf("Opção inválida.\n\n");
                    break;
            }
        }
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}