package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.EmprestimoDAO;
import com.example.cadastromuseu.Biblioteca.model.Emprestimo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DevolucaoEmprestimoController implements Initializable {

    @FXML private TableView<Emprestimo> tableViewPendentes;
    @FXML private TableColumn<Emprestimo, Integer> colId;
    @FXML private TableColumn<Emprestimo, String> colLivro;
    @FXML private TableColumn<Emprestimo, String> colUsuario;
    @FXML private TableColumn<Emprestimo, LocalDate> colDataEmprestimo;
    @FXML private TableColumn<Emprestimo, LocalDate> colDataDevolucao;

    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private ObservableList<Emprestimo> listaPendentes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mapeamento das colunas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colDataDevolucao.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));

        // Mapeamento de objetos aninhados (Livro e Usuário)
        colLivro.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLivro().getTitulo()));
        colUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsuario().getNome()));

        carregarEmprestimosPendentes();
    }

    // Método para carregar dados
    public void carregarEmprestimosPendentes() {
        try {
            listaPendentes = FXCollections.observableArrayList(emprestimoDAO.listarPendentes());
            tableViewPendentes.setItems(listaPendentes);
        } catch (SQLException e) {
            alerta("Erro", "Erro ao carregar empréstimos pendentes.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Método de Devolução
    @FXML
    private void handleRegistrarDevolucao(ActionEvent event) {
        Emprestimo emprestimoSelecionado = tableViewPendentes.getSelectionModel().getSelectedItem();

        if (emprestimoSelecionado == null) {
            alerta("Atenção", "Selecione um empréstimo na tabela para registrar a devolução.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Seu DAO registra a devolução e usa a data de hoje (CURRENT_DATE no SQL)
            emprestimoDAO.registrarDevolucao(emprestimoSelecionado.getId());

            alerta("Sucesso", "Devolução do livro '" + emprestimoSelecionado.getLivro().getTitulo() +
                    "' registrada com sucesso!", Alert.AlertType.INFORMATION);

            // Recarrega a tabela para remover o item devolvido
            carregarEmprestimosPendentes();

        } catch (SQLException e) {
            alerta("Erro de Devolução", "Falha ao registrar a devolução.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void alerta(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}