package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.dao.*;
import com.example.cadastromuseu.Acervo.model.ItemAcervo;
import com.example.cadastromuseu.Acervo.model.Localizacao;
import com.example.cadastromuseu.Acervo.model.TipoItem;
import com.example.cadastromuseu.Acervo.model.Pessoa;
import com.example.cadastromuseu.Acervo.model.Tag;
// IMPORT CORRIGIDO: Necessário para a lógica do handleAdicionarPessoa
// IMPORT PENDENTE: Você precisará criar esta classe SelecaoPessoaController
// se ainda não o fez.
// import com.example.cadastromuseu.Acervo.controller.SelecaoPessoaController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Import para carregar o FXML do modal
import javafx.fxml.Initializable;
import javafx.scene.Scene;       // Import para a cena do modal
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;    // Import para Modality
import javafx.stage.Stage;       // Import para o Stage (janela)

import java.io.File;
import java.io.IOException;     // Import necessário para o FXMLLoader
import java.net.URL;
import java.sql.SQLException;   // Import necessário para os catch blocks
import java.util.List;
import java.util.Optional;      // Import necessário para o TextInputDialog
import java.util.ResourceBundle;

public class EdicaoItemAcervoController implements Initializable {

    // 🖥️ 1. DECLARAÇÕES @FXML (COMPONENTS DA TELA)
    @FXML private Label lblTituloTela;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpDataItem;
    @FXML private TextField txtCaminhoArquivo;
    @FXML private ComboBox<TipoItem> cbTipoItem;
    @FXML private ComboBox<Localizacao> cbLocalizacao;
    @FXML private ListView<Pessoa> lvPessoasRelacionadas;
    @FXML private ListView<Tag> lvTags;

    // 2. DECLARAÇÕES DE DAOs
    private final ItemAcervoDAO itemAcervoDAO = new ItemAcervoDAO();
    private final TipoItemDAO tipoItemDAO = new TipoItemDAO();
    private final LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO();
    private final ItemPessoaDAO itemPessoaDAO = new ItemPessoaDAO();
    private final ItemTagDAO itemTagDAO = new ItemTagDAO();

    // 3. DECLARAÇÃO DO ESTADO DA EDIÇÃO
    private ItemAcervo itemEmEdicao;

    // ----------------------------------------------------
    // INICIALIZAÇÃO E CARREGAMENTO
    // ----------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarCombos();
    }

    private void carregarCombos() {
        try {
            ObservableList<TipoItem> tipos = FXCollections.observableArrayList(tipoItemDAO.listarTodos());
            cbTipoItem.setItems(tipos);

            ObservableList<Localizacao> localizacoes = FXCollections.observableArrayList(localizacaoDAO.listarTodos());
            cbLocalizacao.setItems(localizacoes);
        } catch (Exception e) {
            alerta("Erro de Inicialização", "Falha ao carregar Tipos de Itens ou Localizações.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void carregarRelacoesDoItem(int idItemAcervo) {
        try {
            ObservableList<Pessoa> pessoasRelacionadas = FXCollections.observableArrayList(itemPessoaDAO.buscarPessoasPorItem(idItemAcervo));
            lvPessoasRelacionadas.setItems(pessoasRelacionadas);

            ObservableList<Tag> tagsRelacionadas = FXCollections.observableArrayList(itemTagDAO.buscarTagsPorItem(idItemAcervo));
            lvTags.setItems(tagsRelacionadas);

        } catch (SQLException e) {
            alerta("Erro de Carregamento", "Falha ao carregar pessoas/tags relacionadas do banco.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void setItemParaEdicao(ItemAcervo item) {
        this.itemEmEdicao = item;

        if (itemEmEdicao != null) {
            lblTituloTela.setText("Editando Item ID: " + itemEmEdicao.getId() + " - " + itemEmEdicao.getTitulo());
            txtTitulo.setText(itemEmEdicao.getTitulo());
            txtDescricao.setText(itemEmEdicao.getDescricao());
            dpDataItem.setValue(itemEmEdicao.getDataItem());
            txtCaminhoArquivo.setText(itemEmEdicao.getCaminhoArquivo());

            if (itemEmEdicao.getTipoItem() != null) {
                cbTipoItem.getSelectionModel().select(itemEmEdicao.getTipoItem());
            }
            if (itemEmEdicao.getLocalizacao() != null) {
                cbLocalizacao.getSelectionModel().select(itemEmEdicao.getLocalizacao());
            }

            // Carregar Pessoas e Tags Atuais
            if (itemEmEdicao.getId() > 0) {
                carregarRelacoesDoItem(itemEmEdicao.getId());
            } else {
                // Se for um novo item (ID <= 0), inicializa as ListViews vazias para adição
                lvPessoasRelacionadas.setItems(FXCollections.observableArrayList());
                lvTags.setItems(FXCollections.observableArrayList());
            }
        }
    }

    private void coletarDadosDaTela() {
        itemEmEdicao.setTitulo(txtTitulo.getText());
        itemEmEdicao.setDescricao(txtDescricao.getText());
        itemEmEdicao.setDataItem(dpDataItem.getValue());
        itemEmEdicao.setCaminhoArquivo(txtCaminhoArquivo.getText());
        itemEmEdicao.setTipoItem(cbTipoItem.getSelectionModel().getSelectedItem());
        itemEmEdicao.setLocalizacao(cbLocalizacao.getSelectionModel().getSelectedItem());
    }

    // ----------------------------------------------------
    // HANDLERS (AÇÕES)
    // ----------------------------------------------------

    /**
     * Abre o seletor de arquivos para escolher a nova imagem do item.
     * Atualiza o campo txtCaminhoArquivo com o caminho absoluto.
     */
    @FXML
    private void handleEscolherArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Mídia");

        // Filtros de Extensão
        // Incluindo apenas formatos suportados pelo JavaFX (PNG, JPG, JPEG)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );

        Stage currentStage = (Stage) lblTituloTela.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(currentStage);

        if (arquivoSelecionado != null) {
            // Armazena o caminho absoluto do arquivo no TextField.
            txtCaminhoArquivo.setText(arquivoSelecionado.getAbsolutePath());
            System.out.println("Arquivo de mídia selecionado: " + arquivoSelecionado.getAbsolutePath());
        }
    }

    @FXML
    private void handleAtualizar() {
        // ... (Verificação de Item)
        if (itemEmEdicao == null || itemEmEdicao.getId() <= 0) {
            alerta("Erro", "Item não inicializado para edição.", Alert.AlertType.ERROR);
            return;
        }

        coletarDadosDaTela();

        // ... (Validação)
        if (itemEmEdicao.getTitulo().isEmpty() || itemEmEdicao.getTipoItem() == null) {
            alerta("Validação", "Título e Tipo de Item são obrigatórios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // 1. Atualizar dados principais do Item
            itemAcervoDAO.atualizar(itemEmEdicao);

            // 2. SINCRONIZAR RELAÇÕES N:M

            // 2.1 Pessoas
            List<Pessoa> pessoasParaSalvar = lvPessoasRelacionadas.getItems();
            itemPessoaDAO.sincronizarRelacoes(itemEmEdicao.getId(), pessoasParaSalvar);

            // 2.2 Tags
            List<Tag> tagsParaSalvar = lvTags.getItems();
            itemTagDAO.sincronizarRelacoes(itemEmEdicao.getId(), tagsParaSalvar);


            alerta("Sucesso", "Item ID " + itemEmEdicao.getId() + " atualizado!", Alert.AlertType.INFORMATION);

            fecharJanela();

        } catch (SQLException e) {
            alerta("Erro de Banco", "Falha ao atualizar item: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // --- MANIPULAÇÃO DE PESSOAS ---

    @FXML
    private void handleAdicionarPessoa() {
        try {
            // Abre a janela de seleção
            URL fxmlUrl = getClass().getResource("/com/example/cadastromuseu/Acervo/view/SelecaoPessoa.fxml");

            if (fxmlUrl == null) {
                // Este alerta ajudará a debugar se o caminho está errado
                alerta("Erro Crítico de FXML", "Arquivo SelecaoPessoa.fxml não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());

            // É necessário que esta classe SelecaoPessoaController esteja visível (importada)
            SelecaoPessoaController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Adicionar Pessoa Relacionada");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Processa o resultado
            Pessoa novaPessoa = controller.getPessoaSelecionada();

            if (novaPessoa != null) {
                // Verifica duplicidade pelo ID
                if (lvPessoasRelacionadas.getItems().stream().anyMatch(p -> p.getId() == novaPessoa.getId())) {
                    alerta("Atenção", "Esta pessoa já está relacionada ao item.", Alert.AlertType.WARNING);
                } else {
                    // Adiciona o objeto com o papel definido
                    lvPessoasRelacionadas.getItems().add(novaPessoa);
                }
            }

        } catch (IOException e) {
            alerta("Erro", "Não foi possível carregar a tela de seleção de Pessoa. Verifique o FXML/Controller.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoverPessoa() {
        Pessoa selecionada = lvPessoasRelacionadas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            lvPessoasRelacionadas.getItems().remove(selecionada);
        } else {
            alerta("Atenção", "Selecione uma pessoa para remover.", Alert.AlertType.WARNING);
        }
    }

    // --- MANIPULAÇÃO DE TAGS ---

    @FXML
    private void handleAdicionarTag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adicionar Nova Tag");
        dialog.setHeaderText("Insira a nova Tag para o item.");
        dialog.setContentText("Nome da Tag:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nomeTag -> {
            if (!nomeTag.trim().isEmpty()) {
                // Cria um objeto Tag com o ID 0
                Tag novaTag = new Tag(0, nomeTag.trim());

                // Verifica duplicidade pelo nome
                if (lvTags.getItems().stream().anyMatch(t -> t.getNome().equalsIgnoreCase(nomeTag.trim()))) {
                    alerta("Atenção", "Esta tag já está adicionada ao item.", Alert.AlertType.WARNING);
                } else {
                    lvTags.getItems().add(novaTag);
                }
            }
        });
    }

    @FXML
    private void handleRemoverTag() {
        Tag selecionada = lvTags.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            lvTags.getItems().remove(selecionada);
        } else {
            alerta("Atenção", "Selecione uma tag para remover.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    // ----------------------------------------------------
    // UTILITIES
    // ----------------------------------------------------

    private void fecharJanela() {
        Stage stage = (Stage) lblTituloTela.getScene().getWindow();
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