package com.example.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.dao.UsuarioDao;
import com.example.model.Usuario;

public class LoginService {
    private UsuarioDao usuarioDAO = new UsuarioDao();

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario != null && usuario.isAtivo() && verificarSenha(senha, usuario.getSenha())) {
            return usuario;
        }
        return null; // Falha na autenticação
    }

    private boolean verificarSenha(String senhaDigitada, String senhaArmazenada) {
        String senhaEncriptada = encriptarSenha(senhaDigitada);
        return senhaEncriptada.equals(senhaArmazenada);
    }

    public static String encriptarSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
