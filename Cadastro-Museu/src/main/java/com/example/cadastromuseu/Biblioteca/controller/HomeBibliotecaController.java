package com.example.cadastromuseu.Biblioteca.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.cadastromuseu.Biblioteca.model.Usuario;
import com.example.cadastromuseu.Util.controller.SelecaoModuloController;
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

public class HomeBibliotecaController implements Initializable {

    @FXML private Button btnCadastrarLivro;
    @FXML private Button btnCadastrarUsuario;
    @FXML private Button btnRegistrarEmprestimo;
    @FXML private Button btnListarLivros;
    @FXML private Button btnGerenciarDevolucao;
    @FXML private Button btnSair;
    @FXML private Label lblUsuarioLogado;
    @FXML private Button btnGerenciarLivros;
    @FXML private Button btnGerenciarUsuarios;

    private Usuario usuarioLogado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Garante que, se for carregado direto (sem login), os botões estejam escondidos
        // Se for carregado via LoginController, o setUsuarioLogado irá sobrescrever.
        esconderBotoesAdministrativos(true);
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
            // Se a tela Home for carregada sem login, esconde tudo
            configurarBotoesAdministrativos(false);
            btnListarLivros.setVisible(false); // Esconde se não houver usuário logado
            btnListarLivros.setManaged(false);
            return;
        }

        String tipo = usuarioLogado.getTipo();

        // 1. Acesso Administrativo (Botões de CRUD/Gestão)
        boolean isBibliotecario = tipo.equals("bibliotecario");

        // Apenas o bibliotecário vê os botões de CRUD/Gestão
        configurarBotoesAdministrativos(isBibliotecario);

        // 2. Acesso à Consulta Pública (btnListarLivros)
        // Regra: Visível para COMUNIDADE e PESQUISADOR, mas NÃO para BIBLIOTECARIO.
        boolean isConsultaPublica = tipo.equals("comunidade") || tipo.equals("pesquisador");

        btnListarLivros.setVisible(isConsultaPublica);
        btnListarLivros.setManaged(isConsultaPublica);

        // 3. Acesso ao Módulo de Empréstimo
        // Acesso liberado para bibliotecário. Comunidade e Pesquisador não pode registrar.
        boolean acessoEmprestimo = isBibliotecario;

        btnRegistrarEmprestimo.setVisible(acessoEmprestimo);
        btnRegistrarEmprestimo.setManaged(acessoEmprestimo);
    }

    // Método auxiliar para manipular a visibilidade
    private void esconderBotoesAdministrativos(boolean esconder) {
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
    }


    // Método auxiliar para manipular a visibilidade dos botões ADMINISTRATIVOS
    private void configurarBotoesAdministrativos(boolean visivel) {
        // Esses botões DEVEM SER VISÍVEIS apenas para o Bibliotecário

        // Cadastro/Gestão de Livro
        btnCadastrarLivro.setVisible(visivel);
        btnCadastrarLivro.setManaged(visivel);

        // Cadastro/Gestão de Usuário
        btnCadastrarUsuario.setVisible(visivel);
        btnCadastrarUsuario.setManaged(visivel);

        // Gerenciar Devoluções (Acesso administrativo)
        btnGerenciarDevolucao.setVisible(visivel);
        btnGerenciarDevolucao.setManaged(visivel);

        btnGerenciarLivros.setVisible(visivel);
        btnGerenciarLivros.setManaged(visivel);

        btnGerenciarUsuarios.setVisible(visivel);
        btnGerenciarUsuarios.setManaged(visivel);

        // ⚠️ Se você tem botões Gerenciar Livros/Usuários sem fx:id no FXML, eles não serão controlados.
        // Garanta que todos os botões de gestão tenham fx:id e sejam controlados aqui.
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

    @FXML
    private void handleAbrirGestaoLivros(ActionEvent event) {
        // 1. Verifique se o usuário atual é um Bibliotecário, se aplicável!
        // if (!usuarioLogado.getTipo().equals("bibliotecario")) { return; }

        try {
            // Carrega o FXML de Gestão de Livros
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/GestaoLivros.fxml"));
            Parent root = loader.load();

            // Cria uma nova janela (Stage)
            Stage stage = new Stage();
            stage.setTitle("Gestão Administrativa de Livros");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a Home
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao abrir a tela de Gestão de Livros.");
            e.printStackTrace();
            // Opcional: Mostrar um alerta de erro
            // alerta("Erro", "Falha ao carregar a tela de gestão.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAbrirGestaoUsuarios(ActionEvent event) {
        // ⚠️ Se você tiver controle de acesso, adicione a verificação aqui:
        // Ex: if (!usuarioLogado.getTipo().equals("bibliotecario")) { return; }

        try {
            // Carrega o FXML da Gestão de Usuários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Biblioteca/view/GestaoUsuarios.fxml"));
            Parent root = loader.load();

            // Cria uma nova janela (Stage)
            Stage stage = new Stage();
            stage.setTitle("Gestão de Usuários");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao abrir a tela de Gestão de Usuários.");
            e.printStackTrace();
            // Opcional: Mostrar um alerta de erro ao usuário
            // alerta("Erro", "Falha ao carregar a tela de gestão de usuários.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Carrega a tela SelecaoModulo e fecha a tela atual,
     * injetando o Usuario Logado de volta no controller de destino.
     */
    @FXML
    private void handleVoltarMenu(ActionEvent event) {
        try {
            // Caminho que você corrigiu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Comum/view/SelecaoModulo.fxml"));
            Parent root = loader.load();

            // ⚠️ OBTÉM E INJETA O USUÁRIO DE VOLTA NO NOVO CONTROLLER ⚠️
            // 1. Obtém o controller da nova tela
            SelecaoModuloController selecaoController = loader.getController();

            // 2. Passa o usuário logado para ele
            if (selecaoController != null) {
                selecaoController.setUsuarioLogado(this.usuarioLogado);
            }
            // -------------------------------------------------------------

            Stage stage = new Stage();
            stage.setTitle("Seleção de Módulos");
            stage.setScene(new Scene(root));
            stage.show();

            // Fecha a janela atual usando o ActionEvent
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            alerta("Não foi possível carregar a tela de Seleção de Módulos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


}