package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.UsuarioDAO;
import com.example.cadastromuseu.Biblioteca.model.Usuario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagem;

    private UsuarioDAO usuarioDao = new UsuarioDAO();

    // ----------------------------------------------------------------------
    // LOGIN: Tenta autenticar o usuário e realiza a navegação condicional
    // ----------------------------------------------------------------------
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String senha = txtSenha.getText();

        // Validação básica para evitar chamadas vazias ao banco
        if (email.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Preencha o email e a senha.");
            return;
        }

        try {
            Usuario usuarioLogado = usuarioDao.buscarPorCredenciais(email, senha);

            if (usuarioLogado != null) {
                navegarParaHome(usuarioLogado, event);
            } else {
                // Caso em que o DAO retorna null (email não existe ou senha errada)
                lblMensagem.setText("Email ou senha inválidos.");
            }
        } catch (java.sql.SQLException e) {
            // NOVO: Captura específica para erros de Banco de Dados/Conexão
            lblMensagem.setText("ERRO DE BANCO DE DADOS. Veja o console para detalhes.");
            System.err.println("--- ERRO SQL NO LOGIN ---");
            e.printStackTrace();
            System.err.println("--------------------------");
        } catch (Exception e) {
            // Captura outros erros inesperados (I/O, etc.)
            lblMensagem.setText("Erro inesperado durante o login.");
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // ACESSO COMUNIDADE: Acesso direto à consulta (sem login)
    // ----------------------------------------------------------------------
    @FXML
    private void handleAcessoComunidade(ActionEvent event) {
        // Cria um objeto Usuario temporário para simular o acesso público
        Usuario usuarioComunidade = new Usuario();
        usuarioComunidade.setTipo("comunidade");

        // Navega diretamente, restringindo o acesso na próxima tela (Home)
        navegarParaHome(usuarioComunidade, event);
    }

    // ----------------------------------------------------------------------
    // NAVEGAÇÃO CONDICIONAL
    // ----------------------------------------------------------------------
    private void navegarParaHome(Usuario usuario, ActionEvent event) {
        try {
            // Carrega o FXML da tela principal (Home)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/Home.fxml"));
            Parent root = loader.load();

            // Opcional: Passar o objeto Usuario para o HomeController para gerenciar permissões
            HomeController homeController = loader.getController();
            homeController.setUsuarioLogado(usuario);

            // Troca a cena na janela atual
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Sistema de Gestão - Museu Treze de Maio");
            stage.show();

        } catch (IOException e) {
            lblMensagem.setText("Erro ao carregar a tela principal.");
            e.printStackTrace();
        }
    }
}