package com.example.cadastromuseu.Biblioteca.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.cadastromuseu.Biblioteca.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @FXML private Button btnCadastrarLivro;
    @FXML private Button btnCadastrarUsuario;
    @FXML private Button btnRegistrarEmprestimo;
    @FXML private Button btnListarLivros;
    @FXML private Button btnSair;
    @FXML private Label lblUsuarioLogado;

    private Usuario usuarioLogado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Garante que, se for carregado direto (sem login), os botões estejam escondidos
        // Se for carregado via LoginController, o setUsuarioLogado irá sobrescrever.
        esconderBotoesGerenciamento(true);
    }

    // Dentro de HomeController.java

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // 1. Fecha a janela Home atual
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // 2. Carrega a tela de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/Login.fxml"));
            Parent root = loader.load();

            // 3. Cria e exibe a nova janela de Login
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root, 800, 600)); // Usando o mesmo tamanho
            loginStage.setTitle("Login - Museu Treze de Maio");
            loginStage.show();

        } catch (IOException e) {
            alerta("Erro ao carregar a tela de Login.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;

        // Configura o rótulo com o nome e tipo do usuário
        if (lblUsuarioLogado != null && usuario.getNome() != null) {
            lblUsuarioLogado.setText("Logado como: " + usuario.getNome() + " (" + usuario.getTipo() + ")");
        }

        configurarPermissoes();
    }

    private void configurarPermissoes() {
        if (usuarioLogado == null) {
            // Caso a tela Home seja carregada sem login, esconde tudo
            esconderBotoesGerenciamento(false);
            return;
        }

        String tipo = usuarioLogado.getTipo();

        // Variável de controle booleana
        boolean acessoGerenciamento = tipo.equals("bibliotecario");

        // Chamada ao método que esconde/mostra
        esconderBotoesGerenciamento(!acessoGerenciamento);
    }

    // Método auxiliar para manipular a visibilidade
    private void esconderBotoesGerenciamento(boolean esconder) {
        // Usa setVisible(false) e setManaged(false) para remover o botão e o espaço que ele ocupa

        // Cadastrar Livro
        btnCadastrarLivro.setVisible(!esconder);
        btnCadastrarLivro.setManaged(!esconder);

        // Cadastrar Usuário
        btnCadastrarUsuario.setVisible(!esconder);
        btnCadastrarUsuario.setManaged(!esconder);

        // Registrar Empréstimo
        btnRegistrarEmprestimo.setVisible(!esconder);
        btnRegistrarEmprestimo.setManaged(!esconder);

        // O botão Listar Livros (Consulta) continua visível para todos por padrão.
    }

    @FXML
    public void abrirCadastroLivro(ActionEvent event) {
        try {
            // 1. Define o caminho do FXML
            String fxmlPath = "/com/example/cadastromuseu/Biblioteca/view/CadastroLivro.fxml";

            // 2. Carrega o FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 3. CRIA UM NOVO STAGE (JANELA)
            Stage stageCadastro = new Stage();
            stageCadastro.setTitle("Cadastro de Novo Livro"); // Título da janela
            stageCadastro.setScene(new Scene(root));

            // Configurações opcionais:
            stageCadastro.initModality(Modality.APPLICATION_MODAL); // Bloqueia a tela principal
            stageCadastro.setResizable(false);

            // 4. Exibe a nova janela
            stageCadastro.showAndWait(); // showAndWait espera até que a janela seja fechada

        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de Cadastro de Livro: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Mostrar um alerta de erro para o usuário
        }
    }

    // Os outros métodos devem ser implementados de forma similar:
    @FXML
    public void listarLivros(ActionEvent event) {
        try {
            String fxmlPath = "/com/example/cadastromuseu/Biblioteca/view/ListagemLivros.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Abre como janela modal
            Stage stageListagem = new Stage();
            stageListagem.setTitle("Acervo de Livros");
            stageListagem.setScene(new Scene(root, 800, 600)); // Tamanho ajustado
            stageListagem.initModality(Modality.APPLICATION_MODAL);

            stageListagem.showAndWait();

        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de Listagem de Livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirEmprestimos(ActionEvent event) {
        alerta("Funcionalidade 'Empréstimos' a ser implementada.", Alert.AlertType.INFORMATION);
        // Implementar a navegação para a tela de empréstimos
    }

    // Método auxiliar de alerta
    private void alerta(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.setHeaderText(null); // Sem cabeçalho
        alert.show();
    }

    @FXML
    private void abrirRegistroEmprestimo(ActionEvent event) {
        try {
            String fxmlPath = "/com/example/cadastromuseu/Biblioteca/view/CadastroEmprestimo.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stageEmprestimo = new Stage();
            stageEmprestimo.setTitle("Registrar Novo Empréstimo");
            stageEmprestimo.setScene(new Scene(root));
            stageEmprestimo.initModality(Modality.APPLICATION_MODAL);

            stageEmprestimo.showAndWait();

        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de Registro de Empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirCadastroUsuario(ActionEvent event) {
        try {
            String fxmlPath = "/com/example/cadastromuseu/Biblioteca/view/CadastroUsuario.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stageCadastro = new Stage();
            stageCadastro.setTitle("Cadastro de Usuário");
            stageCadastro.setScene(new Scene(root));
            stageCadastro.initModality(Modality.APPLICATION_MODAL);

            stageCadastro.showAndWait();

        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de Cadastro de Usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Dentro de HomeController.java

    @FXML
    private void abrirGestaoDevolucao(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/DevolucaoEmprestimo.fxml"));
            Parent root = loader.load();

            Stage stageDevolucao = new Stage();
            stageDevolucao.setTitle("Devolução de Livros");
            stageDevolucao.setScene(new Scene(root, 800, 500));
            stageDevolucao.initModality(Modality.APPLICATION_MODAL);
            stageDevolucao.showAndWait();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de Devolução: " + e.getMessage());
            e.printStackTrace();
        }
    }


}