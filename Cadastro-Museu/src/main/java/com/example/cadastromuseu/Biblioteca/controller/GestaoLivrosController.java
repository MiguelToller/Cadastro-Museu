package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.LivroDAO;
import com.example.cadastromuseu.Biblioteca.model.Livro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GestaoLivrosController implements Initializable {

    // Campos FXML da Tabela e Filtro
    @FXML private TableView<Livro> tableViewLivros;
    @FXML private TableColumn<Livro, Integer> colId;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, Integer> colAno;
    @FXML private TableColumn<Livro, String> colISBN;
    @FXML private TextField txtFiltro;

    private LivroDAO livroDAO = new LivroDAO();
    private ObservableList<Livro> listaLivros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mapeamento e Estilo das Colunas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        colId.setStyle("-fx-alignment: CENTER;");
        colAno.setStyle("-fx-alignment: CENTER;");
        colISBN.setStyle("-fx-alignment: CENTER;");

        carregarLivros();
    }

    /**
     * Carrega a lista completa de livros na TableView.
     */
    public void carregarLivros() {
        try {
            // Chamada ao listarTodos do DAO (agora com Update e Delete!)
            List<Livro> livros = livroDAO.listarTodos();
            listaLivros = FXCollections.observableArrayList(livros);
            tableViewLivros.setItems(listaLivros);
        } catch (SQLException e) {
            alerta("Erro de Conexão", "Falha ao carregar a lista de livros. Verifique o console.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // C - CREATE (Novo Livro) & U - UPDATE (Editar Livro)
    // ----------------------------------------------------------------------

    @FXML
    private void handleNovoLivro(ActionEvent event) {
        // Passa 'null' para indicar novo cadastro
        abrirFormularioCadastro(null);
    }

    @FXML
    private void handleEditarLivro(ActionEvent event) {
        Livro livroSelecionado = tableViewLivros.getSelectionModel().getSelectedItem();

        if (livroSelecionado != null) {
            // Passa o objeto Livro para preenchimento (Modo Edição)
            abrirFormularioCadastro(livroSelecionado);
        } else {
            alerta("Aviso", "Selecione um livro na tabela para editar.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Método auxiliar que abre a tela de Cadastro/Edição (LivroController).
     */
    private void abrirFormularioCadastro(Livro livroParaEditar) {
        try {
            // Carrega o FXML da tela de Cadastro/Edição de Livro (seu CadastroLivro.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/CadastroLivro.fxml"));
            Parent root = loader.load();

            // Pega o controller da tela de cadastro
            LivroController controller = loader.getController();

            // Configura o modo Edição/Cadastro e injeta a referência
            controller.setLivro(livroParaEditar);
            controller.setGestaoLivrosController(this);

            Stage stage = new Stage();
            stage.setTitle(livroParaEditar == null ? "Cadastrar Novo Livro" : "Editar Livro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            alerta("Erro de Interface", "Não foi possível carregar a tela de Cadastro/Edição.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // D - DELETE (Excluir Livro)
    // ----------------------------------------------------------------------
    @FXML
    private void handleExcluirLivro(ActionEvent event) {
        Livro livroSelecionado = tableViewLivros.getSelectionModel().getSelectedItem();

        if (livroSelecionado != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação de Exclusão");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Tem certeza que deseja excluir o livro: " + livroSelecionado.getTitulo() + "?");

            Optional<ButtonType> resultado = confirmacao.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    livroDAO.excluir(livroSelecionado.getId());
                    alerta("Sucesso", "Livro excluído com sucesso!", Alert.AlertType.INFORMATION);
                    carregarLivros(); // Recarrega a tabela após a exclusão
                } catch (SQLException e) {
                    alerta("Erro", "Não foi possível excluir o livro. Verifique se ele está associado a empréstimos ativos.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            alerta("Aviso", "Selecione um livro na tabela para excluir.", Alert.AlertType.WARNING);
        }
    }

    // ----------------------------------------------------------------------
    // MÉTODOS AUXILIARES E FILTRO
    // ----------------------------------------------------------------------

    @FXML
    private void handleFiltrarLivros() {
        String termo = txtFiltro.getText().trim();
        try {
            if (termo.isEmpty()) {
                carregarLivros();
            } else {
                // Reutiliza o método de filtro do DAO
                List<Livro> livrosFiltrados = livroDAO.listarPorFiltro(termo);
                tableViewLivros.setItems(FXCollections.observableArrayList(livrosFiltrados));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao filtrar livros: " + e.getMessage());
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        Stage stage = (Stage) tableViewLivros.getScene().getWindow();
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