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
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class GestaoItemAcervoController implements Initializable {

    // 🖥️ 1. DECLARAÇÕES @FXML (COMPONENTS DA TELA)
    @FXML private TableView<ItemAcervo> tvItensAcervo;
    @FXML private TableColumn<ItemAcervo, String> tcId;
    @FXML private TableColumn<ItemAcervo, String> tcTitulo;
    @FXML private TableColumn<ItemAcervo, String> tcTipo;
    @FXML private TableColumn<ItemAcervo, String> tcData;
    @FXML private TableColumn<ItemAcervo, String> tcLocalizacao;
    @FXML private TextField txtPesquisa;

    // 💾 2. DECLARAÇÕES DE DAOs E DADOS
    private final ItemAcervoDAO itemAcervoDAO = new ItemAcervoDAO();
    private ObservableList<ItemAcervo> itensAcervo; // Lista principal para filtro

    // ----------------------------------------------------
    // INICIALIZAÇÃO
    // ----------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar as colunas
        configurarColunas();

        // 2. Carregar os dados iniciais
        carregarItensAcervo();

        // 3. Adicionar listener para pesquisa em tempo real (filtro)
        txtPesquisa.textProperty().addListener((obs, oldValue, newValue) -> handlePesquisar());
    }

    // ----------------------------------------------------
    // MÉTODOS DE SUPORTE
    // ----------------------------------------------------

    private void configurarColunas() {
        // Solução para tcId
        tcId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        // Solução para tcTitulo
        tcTitulo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitulo())
        );

        // Coluna Tipo (Relação 1:N)
        tcTipo.setCellValueFactory(data -> {
            if (data.getValue().getTipoItem() != null) {
                return new SimpleStringProperty(data.getValue().getTipoItem().getNome());
            }
            return new SimpleStringProperty("-");
        });

        // Coluna Data
        tcData.setCellValueFactory(data -> {
            if (data.getValue().getDataItem() != null) {
                return new SimpleStringProperty(data.getValue().getDataItem().toString());
            }
            return new SimpleStringProperty("-");
        });

        // Coluna Localização (Relação 1:N)
        tcLocalizacao.setCellValueFactory(data -> {
            if (data.getValue().getLocalizacao() != null) {
                return new SimpleStringProperty(data.getValue().getLocalizacao().toString());
            }
            return new SimpleStringProperty("Não Definida");
        });
    }

    /**
     * Carrega todos os itens do acervo do banco de dados e preenche a TableView.
     * Este é o método padrão de recarregamento.
     */
    public void carregarItensAcervo() {
        try {
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

        // Garante que a lista original esteja carregada para filtrar
        if (itensAcervo == null) {
            carregarItensAcervo();
        }

        // Filtra a lista
        ObservableList<ItemAcervo> itensFiltrados = FXCollections.observableArrayList();

        for (ItemAcervo item : itensAcervo) {
            boolean matchTitulo = item.getTitulo().toLowerCase().contains(termo);
            // Verifica o nome do Tipo de Item (evita NullPointerException)
            boolean matchTipo = item.getTipoItem() != null && item.getTipoItem().getNome().toLowerCase().contains(termo);

            if (matchTitulo || matchTipo) {
                itensFiltrados.add(item);
            }
        }

        tvItensAcervo.setItems(itensFiltrados);
    }

    /**
     * Abre a tela de Cadastro, usando o FXML dedicado (CadastroItemAcervo.fxml),
     * e não o FXML de Edição.
     */
    @FXML
    private void handleNovoItem() {
        try {
            // Carrega o FXML da tela de CADASTRO
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/CadastroItemAcervo.fxml"));
            Stage stage = new Stage();
            stage.setTitle("1. Cadastro de Novo Item de Acervo");
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            // NOTA: Assumimos que o CadastroItemAcervoController inicializa o item vazio.
            // Se precisar passar dados iniciais, o Controller deve ser obtido aqui.

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recarrega a tabela após o fechamento, para mostrar o item recém-criado
            carregarItensAcervo();

        } catch (IOException e) {
            alerta("Erro", "Não foi possível carregar a tela de cadastro: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Abre a nova tela dedicada de Detalhes (Visualização).
     */
    @FXML
    private void handleVisualizarDetalhes() {
        ItemAcervo selecionado = tvItensAcervo.getSelectionModel().getSelectedItem();

        if (selecionado != null) {
            try {
                // 1. Carrega o FXML DEDICADO DE DETALHES
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/DetalhesItemAcervo.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Visualizar Detalhes: " + selecionado.getTitulo());
                Parent root = loader.load();
                stage.setScene(new Scene(root));

                DetalhesItemAcervoController controller = loader.getController();

                // 2. Passa o item para o novo Controller
                controller.setItem(selecionado);

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Nenhuma atualização de tabela é necessária após visualização

            } catch (IOException e) {
                alerta("Erro", "Não foi possível carregar a tela de detalhes: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            alerta("Atenção", "Selecione um item para visualizar os detalhes.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEditarItem() {
        ItemAcervo selecionado = tvItensAcervo.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Nenhum Item Selecionado", "Selecione um item para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // 1. Carrega o FXML de Edição
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/EdicaoItemAcervo.fxml"));
            Parent root = loader.load();

            // 2. Obtém o Controller e injeta o item a ser editado
            EdicaoItemAcervoController controller = loader.getController();
            controller.setItemParaEdicao(selecionado);

            // 3. Abre a janela MODAL
            Stage stage = new Stage();
            stage.setTitle("Editar Item do Acervo: ID " + selecionado.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 4. Após a edição, atualiza a lista
            carregarItensAcervo();

        } catch (IOException e) {
            alerta("Erro", "Falha ao abrir tela de edição. Verifique o caminho do FXML.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExcluirItem() {
        ItemAcervo selecionado = tvItensAcervo.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Nenhum Item Selecionado", "Selecione um item para excluir.", Alert.AlertType.WARNING);
            return;
        }

        // Confirmação de exclusão
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação de Exclusão");
        confirm.setHeaderText("Excluir Item do Acervo?");
        confirm.setContentText("Tem certeza que deseja excluir o item '" + selecionado.getTitulo() + "'?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Tenta deletar
                boolean sucesso = itemAcervoDAO.deletar(selecionado.getId());

                if (sucesso) {
                    alerta("Sucesso", "Item excluído com sucesso!", Alert.AlertType.INFORMATION);
                    carregarItensAcervo(); // Atualiza a tabela
                } else {
                    alerta("Falha", "Não foi possível excluir o item. Ele pode não existir mais.", Alert.AlertType.WARNING);
                }

            } catch (SQLException e) {

                // TRATAMENTO DE ERRO DE CHAVE ESTRANGEIRA (Geralmente SQLState 23xxx)
                if (e.getSQLState().startsWith("23")) {
                    alerta(
                            "Erro de Integridade de Dados",
                            "Não é possível excluir o item '" + selecionado.getTitulo() + "' pois ele possui relações ativas (Pessoas ou Tags) no acervo.",
                            Alert.AlertType.ERROR
                    );
                } else {
                    alerta("Erro SQL", "Falha ao excluir item. Erro no banco de dados: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        }
    }

    // NOTA: O handleNovoCadastro não existe mais, foi substituído por handleNovoItem
    // Remova o método handleNovoCadastro, se ele estiver no seu FXML!

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