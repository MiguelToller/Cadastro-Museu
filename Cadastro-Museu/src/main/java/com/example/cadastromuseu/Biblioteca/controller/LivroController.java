package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.CategoriaDAO;
import com.example.cadastromuseu.Biblioteca.dao.EditoraDAO;
import com.example.cadastromuseu.Biblioteca.dao.LivroDAO;
import com.example.cadastromuseu.Biblioteca.model.Categoria;
import com.example.cadastromuseu.Biblioteca.model.Editora;
import com.example.cadastromuseu.Biblioteca.model.Livro;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LivroController implements Initializable {

    // Campos FXML (ligados ao CadastroLivro.fxml)
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAno;
    @FXML private TextField txtIsbn;

    // ComboBoxes para chaves estrangeiras, usando os objetos Model
    @FXML private ComboBox<Editora> cbEditora;
    @FXML private ComboBox<Categoria> cbCategoria;

    // Instâncias dos DAOs para acesso aos dados
    private LivroDAO livroDao = new LivroDAO();
    private EditoraDAO editoraDao = new EditoraDAO();
    private CategoriaDAO categoriaDao = new CategoriaDAO();

    // Atributos de Controle para Edição e Referência
    private Livro livroParaEdicao;
    private GestaoLivrosController gestaoLivrosController;

    // ----------------------------------------------------------------------
    // 1. INITIALIZE: Carrega os dados nas ComboBoxes quando a tela é carregada
    // ----------------------------------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Carrega Editoras
            cbEditora.setItems(FXCollections.observableArrayList(editoraDao.listar()));

            // Carrega Categorias
            cbCategoria.setItems(FXCollections.observableArrayList(categoriaDao.listar()));

        } catch (SQLException e) {
            alerta("Erro de Conexão", "Não foi possível carregar as listas de Editoras e Categorias.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // 2. SALVAR: Executa a lógica de validação e persistência
    // ----------------------------------------------------------------------
    @FXML
    public void salvar(ActionEvent event) {

        // --- VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS ---
        if (txtTitulo.getText().trim().isEmpty() ||
                txtAno.getText().trim().isEmpty() ||
                cbEditora.getSelectionModel().isEmpty() ||
                cbCategoria.getSelectionModel().isEmpty()) {

            alerta("Erro de Cadastro", "Por favor, preencha o Título, o Ano e selecione a Editora e a Categoria.", Alert.AlertType.WARNING);
            return;
        }

        // --- VALIDAÇÃO DE TIPO DE DADO (ANO) ---
        Integer anoPublicacao;
        try {
            anoPublicacao = Integer.parseInt(txtAno.getText().trim());
        } catch (NumberFormatException e) {
            alerta("Erro de Formato", "O campo 'Ano' deve conter um número inteiro válido.", Alert.AlertType.ERROR);
            return;
        }

        // --- Persistência de Dados ---
        try {
            // 1. Obtém os objetos Model selecionados
            Editora editoraSelecionada = cbEditora.getSelectionModel().getSelectedItem();
            Categoria categoriaSelecionada = cbCategoria.getSelectionModel().getSelectedItem();

            // Reutiliza o objeto se estiver editando, ou cria um novo
            Livro livro = (livroParaEdicao != null) ? livroParaEdicao : new Livro();

            // 2. Cria o objeto Livro e preenche os campos simples
            livro.setTitulo(txtTitulo.getText().trim());
            livro.setAnoPublicacao(anoPublicacao);
            livro.setIsbn(txtIsbn.getText().trim());
            livro.setIdEditora(editoraSelecionada.getId());
            livro.setIdCategoria(categoriaSelecionada.getId());

            if (livroParaEdicao != null) {
                // Modo EDIÇÃO
                livroDao.atualizar(livro); // Chama o novo método do DAO
                alerta("Sucesso!", "Livro '" + livro.getTitulo() + "' ATUALIZADO com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Modo CADASTRO NOVO
                livroDao.inserir(livro);
                alerta("Sucesso!", "Livro '" + livro.getTitulo() + "' CADASTRADO com sucesso!", Alert.AlertType.INFORMATION);
            }

            // Ação final: Recarrega a tabela de gestão e fecha a janela
            if (gestaoLivrosController != null) {
                gestaoLivrosController.carregarLivros(); // Atualiza a tabela na tela de gestão
            }

            // Fechamento da janela
            ((Stage) txtTitulo.getScene().getWindow()).close();

        } catch (SQLException e) {
            alerta("Erro de Banco de Dados", "Falha ao salvar o livro. Erro: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            alerta("Erro Geral", "Ocorreu um erro inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // ----------------------------------------------------------------------

    private void limparCampos() {
        txtTitulo.setText("");
        txtAno.setText("");
        txtIsbn.setText("");
        cbEditora.getSelectionModel().clearSelection();
        cbCategoria.getSelectionModel().clearSelection();
    }

    private void alerta(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Métodos de Configuração (Setters)
    public void setLivro(Livro livro) {
        this.livroParaEdicao = livro;
        if (livro != null) {
            // Se estiver em modo Edição, preenche os campos
            txtTitulo.setText(livro.getTitulo());
            txtAno.setText(String.valueOf(livro.getAnoPublicacao()));
            txtIsbn.setText(livro.getIsbn());

            // Seleciona a Editora e Categoria nas ComboBoxes
            selecionarComboBoxes(livro.getIdEditora(), livro.getIdCategoria());
        }
    }

    public void setGestaoLivrosController(GestaoLivrosController controller) {
        this.gestaoLivrosController = controller;
    }

    // Método auxiliar para selecionar o item correto nas ComboBoxes
    private void selecionarComboBoxes(int idEditora, int idCategoria) {
        // Lógica para selecionar o item correto na cbEditora
        for (Editora e : cbEditora.getItems()) {
            if (e.getId() == idEditora) {
                cbEditora.getSelectionModel().select(e);
                break;
            }
        }
        // Lógica para selecionar o item correto na cbCategoria
        for (Categoria c : cbCategoria.getItems()) {
            if (c.getId() == idCategoria) {
                cbCategoria.getSelectionModel().select(c);
                break;
            }
        }
    }
}