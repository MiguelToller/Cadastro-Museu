package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.dao.PessoaDAO;
import com.example.cadastromuseu.Acervo.model.Pessoa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List; // Import para List
import java.util.ResourceBundle;

public class SelecaoPessoaController implements Initializable {

    // 🖥️ 1. DECLARAÇÕES @FXML
    @FXML private TableView<Pessoa> tvPessoas;
    @FXML private TableColumn<Pessoa, String> colNome;
    @FXML private TableColumn<Pessoa, String> colTipo;
    @FXML private TextField txtPapel; // O papel dela NO ITEM (Ex: Autor, Doador)

    // 💾 2. DAOs e Variáveis de Estado
    private Pessoa pessoaSelecionada; // Objeto que será retornado ao Controller principal
    private final PessoaDAO pessoaDAO = new PessoaDAO();

    // ----------------------------------------------------
    // INICIALIZAÇÃO
    // ----------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ESSENCIAL: Configura as colunas da tabela
        // As strings devem corresponder aos nomes exatos dos GETTERS (sem o "get")
        // na sua classe Pessoa (Ex: getNome() -> "nome")
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        carregarPessoas();
    }

    private void carregarPessoas() {
        try {
            // 1. Busca os dados no DAO
            List<Pessoa> listaDoBanco = pessoaDAO.listarTodos();

            // 2. DEBUG: Verifica se a lista está vazia
            if (listaDoBanco.isEmpty()) {
                System.out.println("DEBUG: Nenhuma pessoa encontrada no banco de dados.");
                // Opcional: Alerta se a tabela estiver vazia (descomente se quiser)
                // alerta("Atenção", "A tabela de pessoas no banco está vazia.", Alert.AlertType.INFORMATION);
            } else {
                System.out.println("DEBUG: " + listaDoBanco.size() + " pessoas carregadas com sucesso.");
            }

            // 3. Define a lista observável na Tabela
            ObservableList<Pessoa> pessoas = FXCollections.observableArrayList(listaDoBanco);
            tvPessoas.setItems(pessoas);

        } catch (SQLException e) {
            alerta("Erro de Banco", "Falha ao carregar lista de Pessoas.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // HANDLERS (AÇÕES)
    // ----------------------------------------------------

    @FXML
    private void handleConfirmar() {
        Pessoa selecionada = tvPessoas.getSelectionModel().getSelectedItem();
        String papel = txtPapel.getText().trim();

        if (selecionada == null) {
            alerta("Erro", "Selecione uma pessoa na tabela.", Alert.AlertType.WARNING);
            return;
        }
        if (papel.isEmpty()) {
            alerta("Erro", "Defina o **papel** (Ex: Autor, Doador) da pessoa neste item.", Alert.AlertType.WARNING);
            return;
        }

        // CRUCIAL: Modifica o objeto Pessoa selecionado para armazenar o papel/tipo
        // específico para este ItemAcervo antes de retorná-lo.
        // O objeto Pessoa deve ter um setter para o campo 'tipo' (setTipo).
        selecionada.setTipo(papel);

        // Define o objeto a ser retornado
        pessoaSelecionada = selecionada;
        fecharJanela();
    }

    @FXML
    private void handleCancelar() {
        pessoaSelecionada = null; // Garante que o item seja nulo se cancelar
        fecharJanela();
    }

    // ----------------------------------------------------
    // MÉTODO DE RETORNO (GETTER)
    // ----------------------------------------------------

    /**
     * Retorna o objeto Pessoa selecionado e configurado com o 'papel' definido.
     */
    public Pessoa getPessoaSelecionada() {
        return pessoaSelecionada;
    }

    // ----------------------------------------------------
    // UTILITIES
    // ----------------------------------------------------

    private void fecharJanela() {
        // Garante que o stage seja fechado a partir de qualquer componente
        Stage stage = (Stage) tvPessoas.getScene().getWindow();
        stage.close();
    }

    private void alerta(String titulo, String mensagem, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}