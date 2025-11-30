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
    private Usuario usuarioParaEdicao; // O objeto a ser editado (null se for novo)
    private GestaoUsuariosController gestaoUsuariosController;

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

        // --- Validação de campos obrigatórios ---
        // A validação de SENHA deve ser diferente para edição.
        boolean isCadastroNovo = (usuarioParaEdicao == null);

        if (txtNome.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() ||
                cbPerfilAcesso.getSelectionModel().isEmpty() ||
                (isCadastroNovo && txtSenha.getText().trim().isEmpty())) { // Senha só é obrigatória no cadastro

            alerta("Erro", "Preencha todos os campos. Senha é obrigatória no cadastro de novos usuários.", Alert.AlertType.WARNING);
            return;
        }

        // --- Persistência de Dados (CREATE ou UPDATE) ---
        try {
            // 1. Cria ou reutiliza o objeto Usuario
            Usuario usuario = isCadastroNovo ? new Usuario() : usuarioParaEdicao;

            // 2. Preenche os campos
            usuario.setNome(txtNome.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setTipo(cbPerfilAcesso.getSelectionModel().getSelectedItem());

            // 3. Lógica da Senha
            if (!txtSenha.getText().trim().isEmpty()) {
                // Se o campo Senha for preenchido, atualiza a senha.
                usuario.setSenha(txtSenha.getText());
            } else if (isCadastroNovo) {
                // A validação acima já deveria ter parado aqui, mas garante.
                alerta("Erro", "Senha é obrigatória para novo cadastro.", Alert.AlertType.ERROR);
                return;
            } else {
                // Se estiver em modo edição e a senha estiver vazia,
                // setamos como null para que o DAO ignore a atualização da senha.
                usuario.setSenha(null);
            }

            // 4. Chamada ao DAO
            if (isCadastroNovo) {
                dao.inserir(usuario);
                alerta("Sucesso", "Usuário cadastrado: " + usuario.getNome() + " (Tipo: " + usuario.getTipo() + ")", Alert.AlertType.INFORMATION);
                limparCampos(); // Limpa se for novo cadastro
            } else {
                dao.atualizar(usuario); // Chama o novo método
                alerta("Sucesso", "Usuário atualizado: " + usuario.getNome() + " (ID: " + usuario.getId() + ")", Alert.AlertType.INFORMATION);
            }

            // 5. Atualiza e fecha a janela (se não for novo cadastro que limpa campos)
            if (gestaoUsuariosController != null) {
                gestaoUsuariosController.carregarUsuarios();
            }
            if (!isCadastroNovo) {
                ((Stage) txtNome.getScene().getWindow()).close(); // Fecha após edição
            }

        } catch (SQLException e) {
            alerta("Erro de BD", "Falha ao salvar. Verifique a unicidade do Email ou a conexão. Erro: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        // O catch(Exception) genérico anterior foi removido, focando apenas no SQLException
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

    // Métodos de Configuração (Setters)
    public void setUsuario(Usuario usuario) {
        this.usuarioParaEdicao = usuario;

        if (usuario != null) {
            // Preenche os campos para Edição
            txtNome.setText(usuario.getNome());
            txtEmail.setText(usuario.getEmail());
            // ATENÇÃO: Nunca pré-preencha a senha por segurança!
            txtSenha.setText(""); // Deixe vazio; o usuário digita APENAS se quiser alterar.

            // Seleciona o perfil de acesso correto
            cbPerfilAcesso.getSelectionModel().select(usuario.getTipo());

            // Desabilita a edição do email (opcional, mas recomendado)
            // txtEmail.setDisable(true);
        }
    }

    public void setGestaoUsuariosController(GestaoUsuariosController controller) {
        this.gestaoUsuariosController = controller;
    }
}