package com.example.cadastromuseu.Biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class HomeController {

    @FXML
    public void abrirCadastroLivro() {
        alerta("Tela de cadastro será aberta.");
    }

    @FXML
    public void listarLivros() {
        alerta("Tabela de livros será exibida.");
    }

    @FXML
    public void abrirEmprestimos() {
        alerta("Tela de empréstimos será aberta.");
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
