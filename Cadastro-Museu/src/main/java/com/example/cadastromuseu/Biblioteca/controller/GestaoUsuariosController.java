package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.UsuarioDAO;
import com.example.cadastromuseu.Biblioteca.model.Usuario;

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

public class GestaoUsuariosController implements Initializable {

    @FXML private TableView<Usuario> tableViewUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TextField txtFiltro;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar as colunas para mapear os campos do objeto Usuario
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colId.setStyle("-fx-alignment: CENTER;");
        colTipo.setStyle("-fx-alignment: CENTER;");

        carregarUsuarios();
    }

    public void carregarUsuarios() {
        try {
            // Chamada ao listar() do DAO
            List<Usuario> usuarios = usuarioDAO.listar();
            ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList(usuarios);
            tableViewUsuarios.setItems(listaUsuarios);
        } catch (SQLException e) {
            alerta("Erro de Conexão", "Falha ao carregar a lista de usuários.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // C - CREATE (Novo Usuário)
    // ----------------------------------------------------------------------
    @FXML
    private void handleNovoUsuario(ActionEvent event) {
        // Abre o formulário de cadastro, passando 'null' para o modo Novo
        abrirFormularioCadastro(null);
    }

    // ----------------------------------------------------------------------
    // U - UPDATE (Editar Usuário)
    // ----------------------------------------------------------------------
    @FXML
    private void handleEditarUsuario(ActionEvent event) {
        Usuario usuarioSelecionado = tableViewUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSelecionado != null) {
            // Abre o formulário de cadastro, passando o objeto para o modo Edição
            abrirFormularioCadastro(usuarioSelecionado);
        } else {
            alerta("Aviso", "Selecione um usuário na tabela para editar.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Método auxiliar que abre a tela de Cadastro/Edição (UsuarioController).
     */
    private void abrirFormularioCadastro(Usuario usuarioParaEditar) {
        try {
            // Carrega o FXML da tela de Cadastro/Edição de Usuário (seu CadastroUsuario.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/CadastroUsuario.fxml"));
            Parent root = loader.load();

            UsuarioController controller = loader.getController();

            // Configura o modo Edição/Cadastro e injeta a referência
            controller.setUsuario(usuarioParaEditar);
            controller.setGestaoUsuariosController(this);

            Stage stage = new Stage();
            stage.setTitle(usuarioParaEditar == null ? "Cadastrar Novo Usuário" : "Editar Usuário");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            alerta("Erro de Interface", "Não foi possível carregar a tela de Cadastro/Edição.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // D - DELETE (Excluir Usuário)
    // ----------------------------------------------------------------------
    @FXML
    private void handleExcluirUsuario(ActionEvent event) {
        Usuario usuarioSelecionado = tableViewUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSelecionado != null) {
            // Confirmação de Exclusão
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação de Exclusão");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Tem certeza que deseja excluir o usuário: " + usuarioSelecionado.getNome() + "?");

            Optional<ButtonType> resultado = confirmacao.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    usuarioDAO.excluir(usuarioSelecionado.getId());
                    alerta("Sucesso", "Usuário excluído com sucesso!", Alert.AlertType.INFORMATION);
                    carregarUsuarios(); // Recarrega a tabela após a exclusão
                } catch (SQLException e) {
                    alerta("Erro", "Não foi possível excluir o usuário. Verifique se ele tem empréstimos pendentes.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            alerta("Aviso", "Selecione um usuário na tabela para excluir.", Alert.AlertType.WARNING);
        }
    }

    // ----------------------------------------------------------------------
    // UTILS
    // ----------------------------------------------------------------------

    @FXML
    private void handleFiltrarUsuarios() {
        String termo = txtFiltro.getText().trim();
        try {
            if (termo.isEmpty()) {
                carregarUsuarios(); // Se vazio, recarrega a lista completa
            } else {
                // Reutiliza o método de filtro do DAO
                List<Usuario> usuariosFiltrados = usuarioDAO.listarPorFiltro(termo);

                // Atualiza a TableView com a lista filtrada
                tableViewUsuarios.setItems(FXCollections.observableArrayList(usuariosFiltrados));
            }
        } catch (SQLException e) {
            // Mostra o erro SQL ao usuário para diagnóstico
            alerta("Erro de Filtro SQL", "Falha na pesquisa de usuários. Mensagem: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        Stage stage = (Stage) tableViewUsuarios.getScene().getWindow();
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