package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.dao.LocalizacaoDAO;
import com.example.cadastromuseu.Acervo.model.Localizacao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

// IMPORTANTE: Removemos os imports de FilteredList e SortedList para evitar o erro de compilação.

public class GestaoLocalizacaoController implements Initializable {

    // 🖥️ Componentes FXML
    @FXML private TableView<Localizacao> tvLocalizacoes;
    @FXML private TableColumn<Localizacao, Integer> colID;
    @FXML private TableColumn<Localizacao, String> colSala;
    @FXML private TableColumn<Localizacao, String> colEstante;
    @FXML private TableColumn<Localizacao, String> colPrateleira;

    @FXML private TextField txtSala;
    @FXML private TextField txtEstante;
    @FXML private TextField txtPrateleira;

    // CAMPO DE FILTRO
    @FXML private TextField txtFiltro;

    // 💾 DAO
    private final LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO();

    // Lista Mestre (Contém todos os dados do banco para ser filtrada manualmente)
    private ObservableList<Localizacao> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar Colunas
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colEstante.setCellValueFactory(new PropertyValueFactory<>("estante"));
        colPrateleira.setCellValueFactory(new PropertyValueFactory<>("prateleira"));

        // 2. Carregar Dados Iniciais
        carregarLocalizacoes();

        // 3. Configurar a Lógica de Filtro Manual
        configurarFiltroManual();

        // Exibe todos os dados inicialmente
        tvLocalizacoes.setItems(masterData);
    }

    private void carregarLocalizacoes() {
        // Limpa a lista e recarrega todos os dados do banco
        masterData.clear();
        masterData.addAll(localizacaoDAO.listarTodos());
    }

    /**
     * Configura a filtragem manual da tabela, sem usar FilteredList/SortedList.
     * Isso resolve o problema de importação de dependência.
     */
    private void configurarFiltroManual() {
        if (txtFiltro == null) {
            System.err.println("ERRO: O campo txtFiltro não foi injetado (null). Verifique o fx:id no FXML.");
            return;
        }

        // Adiciona o ouvinte (listener) ao campo de texto
        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {

            // Lista temporária para armazenar os resultados filtrados
            ObservableList<Localizacao> filteredList = FXCollections.observableArrayList();

            // Se o campo de busca estiver vazio, exibe a lista completa
            if (newValue == null || newValue.isEmpty()) {
                filteredList.addAll(masterData);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();

                // Itera sobre a lista Mestra e aplica o filtro
                for (Localizacao localizacao : masterData) {

                    // Lógica de busca: verifica se o termo está em Sala, Estante ou Prateleira

                    // Verifica Sala
                    if (localizacao.getSala() != null && localizacao.getSala().toLowerCase().contains(lowerCaseFilter)) {
                        filteredList.add(localizacao);
                    }
                    // Verifica Estante
                    else if (localizacao.getEstante() != null && localizacao.getEstante().toLowerCase().contains(lowerCaseFilter)) {
                        filteredList.add(localizacao);
                    }
                    // Verifica Prateleira
                    else if (localizacao.getPrateleira() != null && localizacao.getPrateleira().toLowerCase().contains(lowerCaseFilter)) {
                        filteredList.add(localizacao);
                    }
                }
            }

            // Atualiza a TableView com a nova lista filtrada
            tvLocalizacoes.setItems(filteredList);

            // Se o sorting foi perdido (pois SortedList foi removido), você pode re-aplicar o comparator
            // tvLocalizacoes.getSortOrder().forEach(col -> col.sort());
        });
    }

    // --- MÉTODOS DE CRUD (Adicionar, Remover, Fechar) ---

    @FXML
    private void handleAdicionarLocalizacao() {
        String sala = txtSala.getText().trim();
        String estante = txtEstante.getText().trim();
        String prateleira = txtPrateleira.getText().trim();

        if (sala.isEmpty()) {
            alerta("Atenção", "A Sala é obrigatória para cadastrar a localização.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Cria um objeto temporário para inserir (ID 0)
            Localizacao novaLocalizacao = new Localizacao(0, sala, estante, prateleira);
            localizacaoDAO.inserir(novaLocalizacao);

            // Recarrega todos os dados para refletir o novo ID gerado
            carregarLocalizacoes();

            // Força a atualização do filtro, caso haja algum texto
            configurarFiltroManual();

            // Limpa os campos
            txtSala.clear();
            txtEstante.clear();
            txtPrateleira.clear();

        } catch (SQLException e) {
            alerta("Erro de Banco", "Falha ao adicionar localização.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoverLocalizacao() {
        Localizacao selecionada = tvLocalizacoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            alerta("Atenção", "Selecione uma localização para remover.", Alert.AlertType.WARNING);
            return;
        }

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja remover a localização '" + selecionada.toString() + "'?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                localizacaoDAO.remover(selecionada.getId());

                // Recarrega todos os dados para atualizar a tela
                carregarLocalizacoes();

                // Força a atualização do filtro
                configurarFiltroManual();

            } catch (SQLException e) {
                alerta("Erro de Remoção", "Não foi possível remover. Verifique se há itens de acervo utilizando esta localização.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleFechar() {
        Stage stage = (Stage) tvLocalizacoes.getScene().getWindow();
        stage.close();
    }

    /**
     * Carrega a tela principal (Menu) e fecha a tela atual.
     * Necessita do Main.fxml no caminho correto.
     */
    @FXML
    private void handleVoltarMenu() {
        try {
            // Carrega o FXML da tela principal (Ajuste o caminho se necessário)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Main.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Menu Principal");
            stage.setScene(new Scene(root));
            stage.show();

            // Fecha a janela de Gestão de Localizações
            ((Stage) tvLocalizacoes.getScene().getWindow()).close();

        } catch (IOException e) {
            alerta("Erro de Navegação", "Não foi possível carregar a tela principal (Main.fxml). Verifique o caminho do arquivo.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void alerta(String titulo, String mensagem, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}