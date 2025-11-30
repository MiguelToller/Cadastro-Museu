package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.LivroDAO;
import com.example.cadastromuseu.Biblioteca.model.Livro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListagemLivrosController implements Initializable {

    @FXML private TableView<Livro> tableViewLivros;
    @FXML private TableColumn<Livro, Integer> colId;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, Integer> colAno;
    @FXML private TableColumn<Livro, String> colISBN;

    private LivroDAO livroDAO = new LivroDAO();
    private ObservableList<Livro> listaLivros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar as colunas para mapear os campos do objeto Livro
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao")); // Nome exato do getter no Model: getAnoPublicacao()
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        colId.setStyle("-fx-alignment: CENTER;");
        colTitulo.setStyle("-fx-alignment: CENTER;");
        colAno.setStyle("-fx-alignment: CENTER;");
        colISBN.setStyle("-fx-alignment: CENTER;");

        // 2. Carregar os dados
        carregarLivros();
    }

    public void carregarLivros() {
        try {
            List<Livro> livros = livroDAO.listarTodos();
            listaLivros = FXCollections.observableArrayList(livros);
            tableViewLivros.setItems(listaLivros);

        } catch (SQLException e) {
            // Lógica para mostrar erro ao usuário (alerta)
            System.err.println("Erro ao carregar livros: " + e.getMessage());
        }
    }

    @FXML
    public void handleVoltar(ActionEvent event) {
        // Fecha a janela atual
        Stage stage = (Stage) tableViewLivros.getScene().getWindow();
        stage.close();
    }

    // Futuro: Implementar a lógica de edição aqui, se o usuário logado for bibliotecário.
}