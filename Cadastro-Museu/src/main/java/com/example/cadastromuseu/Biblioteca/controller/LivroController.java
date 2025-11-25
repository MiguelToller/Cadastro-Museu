package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.LivroDAO;
import com.example.cadastromuseu.Biblioteca.model.Livro;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LivroController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAno;
    @FXML private TextField txtIsbn;

    private LivroDAO dao = new LivroDAO();

    @FXML
    public void salvar() {
        try {
            Livro livro = new Livro();
            livro.setTitulo(txtTitulo.getText());
            livro.setAnoPublicacao(Integer.parseInt(txtAno.getText()));
            livro.setIsbn(txtIsbn.getText());

            dao.inserir(livro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
