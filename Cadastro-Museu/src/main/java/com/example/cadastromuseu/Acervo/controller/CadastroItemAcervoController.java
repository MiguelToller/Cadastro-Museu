package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.dao.ItemAcervoDAO;
import com.example.cadastromuseu.Acervo.dao.LocalizacaoDAO;
import com.example.cadastromuseu.Acervo.dao.TipoItemDAO;
import com.example.cadastromuseu.Acervo.dao.PessoaDAO; // Novo: Para salvar Pessoas e Tags
import com.example.cadastromuseu.Acervo.dao.TagDAO; // Novo
import com.example.cadastromuseu.Acervo.model.ItemAcervo;
import com.example.cadastromuseu.Acervo.model.Localizacao;
import com.example.cadastromuseu.Acervo.model.TipoItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastroItemAcervoController implements Initializable {

    // 1. Componentes Principais (1:N)
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpDataItem;
    @FXML private TextField txtCaminhoArquivo;

    @FXML private ComboBox<TipoItem> cbTipoItem;
    @FXML private ComboBox<Localizacao> cbLocalizacao;

    // 2. Componentes N:N - Pessoas Relacionadas
    @FXML private TextField txtPessoaNome;
    @FXML private ComboBox<String> cbPessoaTipo; // Para o ENUM ('autor', 'doacao', 'citado', 'outro')
    @FXML private ListView<String> lvPessoasRelacionadas; // Lista temporária (Nome (tipo))

    // 3. Componentes N:N - Tags/Palavras-Chave
    @FXML private TextField txtTag;
    @FXML private ListView<String> lvTags; // Lista temporária de tags

    // DAOs
    private final TipoItemDAO tipoItemDAO = new TipoItemDAO();
    private final LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO();
    private final ItemAcervoDAO itemAcervoDAO = new ItemAcervoDAO();

    // Novos DAOs para N:N (Precisa Criar!)
    private final PessoaDAO pessoaDAO = new PessoaDAO();
    private final TagDAO tagDAO = new TagDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarComboBoxes();
        // Inicializa o ComboBox de Tipo de Pessoa
        cbPessoaTipo.getItems().addAll("autor", "doacao", "citado", "outro");
    }

    // ----------------------------------------------------
    // LÓGICA DE INTERFACE
    // ----------------------------------------------------

    private void carregarComboBoxes() {
        try {
            // Carrega Tipos de Item
            List<TipoItem> tipos = tipoItemDAO.listarTodos();
            cbTipoItem.setItems(FXCollections.observableArrayList(tipos));

            // Carrega Localizações
            List<Localizacao> localizacoes = localizacaoDAO.listarTodos();
            localizacoes.add(0, new Localizacao(0, "Não Definida", "", ""));
            cbLocalizacao.setItems(FXCollections.observableArrayList(localizacoes));
            cbLocalizacao.getSelectionModel().selectFirst();
        } catch (Exception e) {
            alerta("Erro", "Falha ao carregar dados de apoio.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelecionarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Mídia");
        File selectedFile = fileChooser.showOpenDialog(((Stage) txtTitulo.getScene().getWindow()));

        if (selectedFile != null) {
            txtCaminhoArquivo.setText(selectedFile.getAbsolutePath());
        }
    }

    // --- Lógica N:N Pessoas ---
    @FXML
    private void handleAdicionarPessoa() {
        String nome = txtPessoaNome.getText().trim();
        String tipo = cbPessoaTipo.getValue();

        if (nome.isEmpty() || tipo == null) {
            alerta("Dados Incompletos", "Preencha o nome e selecione o tipo de relacionamento.", Alert.AlertType.WARNING);
            return;
        }

        // Formato para salvar na lista: "Nome (tipo)"
        lvPessoasRelacionadas.getItems().add(nome + " (" + tipo + ")");
        txtPessoaNome.clear();
        cbPessoaTipo.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRemoverPessoa() {
        String selected = lvPessoasRelacionadas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lvPessoasRelacionadas.getItems().remove(selected);
        }
    }

    // --- Lógica N:N Tags ---
    @FXML
    private void handleAdicionarTag() {
        String tag = txtTag.getText().trim().toLowerCase();

        if (tag.isEmpty() || lvTags.getItems().contains(tag)) {
            alerta("Tag Inválida", "A tag é vazia ou já existe na lista.", Alert.AlertType.WARNING);
            return;
        }

        lvTags.getItems().add(tag);
        txtTag.clear();
    }

    @FXML
    private void handleRemoverTag() {
        String selected = lvTags.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lvTags.getItems().remove(selected);
        }
    }

    // ----------------------------------------------------
    // AÇÃO PRINCIPAL: CADASTRAR ITEM (COM N:N)
    // ----------------------------------------------------

    @FXML
    private void handleSalvarItem() {
        if (!validarCampos()) {
            return;
        }

        ItemAcervo novoItem = new ItemAcervo();

        // 1. Captura e Seta Campos Simples
        novoItem.setTitulo(txtTitulo.getText());
        novoItem.setDescricao(txtDescricao.getText());
        novoItem.setDataItem(dpDataItem.getValue());
        novoItem.setCaminhoArquivo(txtCaminhoArquivo.getText());

        // 2. Seta Relações 1:N
        novoItem.setTipoItem(cbTipoItem.getSelectionModel().getSelectedItem());

        Localizacao localizacaoSelecionada = cbLocalizacao.getSelectionModel().getSelectedItem();
        if (localizacaoSelecionada != null && localizacaoSelecionada.getId() > 0) {
            novoItem.setLocalizacao(localizacaoSelecionada);
        } else {
            novoItem.setLocalizacao(null);
        }

        try {
            // 3. Chamar o DAO para inserir ItemAcervo (tabela item_acervo)
            long novoItemId = itemAcervoDAO.inserirRetornandoId(novoItem); // Novo método no DAO que retorna o ID

            if (novoItemId > 0) {
                // 4. Salvar Relações N:N

                // Salva Pessoas (pessoa e item_pessoa)
                // É necessário criar o método 'salvarRelacoesPessoa' no PessoaDAO
                pessoaDAO.salvarRelacoes(novoItemId, lvPessoasRelacionadas.getItems());

                // Salva Tags (tag e item_tag)
                // É necessário criar o método 'salvarRelacoesTag' no TagDAO
                tagDAO.salvarRelacoes(novoItemId, lvTags.getItems());

                alerta("Sucesso", "Item '" + novoItem.getTitulo() + "' cadastrado com sucesso!", Alert.AlertType.INFORMATION);

                // Fecha a janela
                Stage stage = (Stage) txtTitulo.getScene().getWindow();
                stage.close();
            } else {
                alerta("Erro", "Falha ao cadastrar o item. ID não retornado.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            alerta("Erro", "Falha crítica ao cadastrar o item. Verifique o log: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Descartar Alterações?");
        alert.setContentText("Qualquer informação não salva será perdida. Deseja continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) txtTitulo.getScene().getWindow();
            stage.close();
        }
    }

    // ----------------------------------------------------
    // VALIDAÇÃO E UTILITÁRIOS
    // ----------------------------------------------------

    private boolean validarCampos() {
        String msgErro = "";

        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) {
            msgErro += "O Título é obrigatório.\n";
        }
        if (cbTipoItem.getSelectionModel().isEmpty() || cbTipoItem.getSelectionModel().getSelectedItem() == null) {
            msgErro += "O Tipo de Item é obrigatório.\n";
        }

        // Validação adicional de listas N:N, se necessário, mas geralmente são opcionais.

        if (msgErro.length() == 0) {
            return true;
        } else {
            alerta("Campos Inválidos", msgErro, Alert.AlertType.WARNING);
            return false;
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