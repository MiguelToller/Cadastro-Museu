package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.dao.ItemAcervoDAO;
import com.example.cadastromuseu.Acervo.model.ItemAcervo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultaPublicaAcervoController implements Initializable {

    // 🖥️ 1. DECLARAÇÕES @FXML (COMPONENTES DA TELA)
    @FXML private TableView<ItemAcervo> tvItensAcervo;
    @FXML private TableColumn<ItemAcervo, String> tcId;
    @FXML private TableColumn<ItemAcervo, String> tcTitulo;
    @FXML private TableColumn<ItemAcervo, String> tcTipo;
    @FXML private TableColumn<ItemAcervo, String> tcData;
    @FXML private TableColumn<ItemAcervo, String> tcLocalizacao;
    @FXML private TextField txtPesquisa;

    // 💾 2. DECLARAÇÕES DE DAOs E DADOS
    private final ItemAcervoDAO itemAcervoDAO = new ItemAcervoDAO();
    private ObservableList<ItemAcervo> itensAcervo;

    // ----------------------------------------------------
    // INICIALIZAÇÃO E SUPORTE
    // ----------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        carregarItensAcervo();
        // Listener para pesquisa em tempo real
        txtPesquisa.textProperty().addListener((obs, oldValue, newValue) -> handlePesquisar());
    }

    private void configurarColunas() {
        tcId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );
        tcTitulo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitulo())
        );
        tcTipo.setCellValueFactory(data -> {
            if (data.getValue().getTipoItem() != null) {
                return new SimpleStringProperty(data.getValue().getTipoItem().getNome());
            }
            return new SimpleStringProperty("-");
        });
        tcData.setCellValueFactory(data -> {
            if (data.getValue().getDataItem() != null) {
                return new SimpleStringProperty(data.getValue().getDataItem().toString());
            }
            return new SimpleStringProperty("-");
        });
        tcLocalizacao.setCellValueFactory(data -> {
            if (data.getValue().getLocalizacao() != null) {
                // Usando a representação em String da Localização
                return new SimpleStringProperty(data.getValue().getLocalizacao().toString());
            }
            return new SimpleStringProperty("Não Definida");
        });
    }

    /**
     * Carrega todos os itens do acervo do banco de dados (Apenas dados 1:N).
     */
    public void carregarItensAcervo() {
        try {
            // Este método usa o DAO que só carrega 1:N, o que é suficiente para a tabela.
            List<ItemAcervo> listaItens = itemAcervoDAO.listarTodos();
            itensAcervo = FXCollections.observableArrayList(listaItens);
            tvItensAcervo.setItems(itensAcervo);
        } catch (Exception e) {
            alerta("Erro de Carregamento", "Falha ao carregar itens do acervo.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // HANDLERS (AÇÕES)
    // ----------------------------------------------------

    @FXML
    private void handlePesquisar() {
        String termo = txtPesquisa.getText().toLowerCase();

        if (itensAcervo == null) {
            carregarItensAcervo();
        }

        ObservableList<ItemAcervo> itensFiltrados = FXCollections.observableArrayList();

        for (ItemAcervo item : itensAcervo) {
            boolean matchTitulo = item.getTitulo().toLowerCase().contains(termo);
            boolean matchTipo = item.getTipoItem() != null && item.getTipoItem().getNome().toLowerCase().contains(termo);

            if (matchTitulo || matchTipo) {
                itensFiltrados.add(item);
            }
        }

        tvItensAcervo.setItems(itensFiltrados);
    }

    /**
     * Ação de fechar a janela.
     */
    @FXML
    private void handleFechar() {
        Stage stage = (Stage) tvItensAcervo.getScene().getWindow();
        stage.close();
    }

    /**
     * Abre a tela dedicada de Detalhes para o item selecionado.
     * Não precisa carregar o item completo, pois a tela de detalhes foi simplificada.
     */
    @FXML
    private void handleVisualizarDetalhes() {
        ItemAcervo selecionado = tvItensAcervo.getSelectionModel().getSelectedItem();

        if (selecionado != null) {
            try {
                // Carrega o FXML DEDICADO DE DETALHES
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/DetalhesItemAcervo.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Detalhes do Item: " + selecionado.getTitulo());
                Parent root = loader.load();
                stage.setScene(new Scene(root));

                DetalhesItemAcervoController controller = loader.getController();

                // Passa o item da tabela. Como a tela de detalhes foi simplificada (sem N:M), este item é suficiente.
                controller.setItem(selecionado);

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                alerta("Erro", "Não foi possível carregar a tela de detalhes: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            alerta("Atenção", "Selecione um item para visualizar os detalhes.", Alert.AlertType.WARNING);
        }
    }

    // ----------------------------------------------------
    // UTILITIES
    // ----------------------------------------------------
    private void alerta(String titulo, String mensagem, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}