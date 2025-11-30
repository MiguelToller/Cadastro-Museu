package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.UsuarioDAO;
import com.example.cadastromuseu.Biblioteca.model.Usuario;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioController implements Initializable {

    // Campos FXML (Lembre-se de ligar txtSenha e cbPerfilAcesso no FXML!)
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<String> cbPerfilAcesso; // Mapeia para o campo 'tipo' no Model

    private UsuarioDAO dao = new UsuarioDAO();

    // ----------------------------------------------------------------------
    // INITIALIZE: Carrega as opções do ENUM para o ComboBox
    // ----------------------------------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Estas strings DEVEM ser idênticas aos valores ENUM no seu SQL!
        List<String> tiposUsuario = List.of("bibliotecario", "pesquisador", "comunidade");
        cbPerfilAcesso.setItems(FXCollections.observableArrayList(tiposUsuario));
    }

    // ----------------------------------------------------------------------
    // SALVAR: Pega os dados, valida e insere no banco
    // ----------------------------------------------------------------------
    @FXML
    public void salvar(ActionEvent event) {

        // Validação de campos obrigatórios
        if (txtNome.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() ||
                txtSenha.getText().trim().isEmpty() ||
                cbPerfilAcesso.getSelectionModel().isEmpty()) {

            alerta("Erro", "Preencha todos os campos e selecione o Perfil de Acesso.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setNome(txtNome.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setSenha(txtSenha.getText());
            // Usa o setter do campo 'tipo' (que mapeia para a coluna 'tipo' no banco)
            usuario.setTipo(cbPerfilAcesso.getSelectionModel().getSelectedItem());

            dao.inserir(usuario);

            alerta("Sucesso", "Usuário cadastrado: " + usuario.getNome() + " (Tipo: " + usuario.getTipo() + ")", Alert.AlertType.INFORMATION);
            limparCampos();

        } catch (SQLException e) {
            alerta("Erro de BD", "Falha ao inserir. Verifique a unicidade do Email ou a conexão. Erro: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // NAVEGAÇÃO E UTILS
    // ----------------------------------------------------------------------

    @FXML
    public void voltar(ActionEvent event) {
        // Método para fechar a janela (já configurado nas interações anteriores)
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtSenha.setText("");
        cbPerfilAcesso.getSelectionModel().clearSelection();
    }

    private void alerta(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}