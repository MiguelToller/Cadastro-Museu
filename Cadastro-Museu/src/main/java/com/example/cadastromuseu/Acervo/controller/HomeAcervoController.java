package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Biblioteca.model.Usuario;
import com.example.cadastromuseu.Main;
import com.example.cadastromuseu.Util.controller.SelecaoModuloController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeAcervoController implements Initializable {

    // Componentes FXML
    @FXML private Label lblUsuarioLogadoAcervo;
    @FXML private Button btnCadastrarItemAcervo;
    @FXML private Button btnGerenciarAcervo;
    @FXML private Button btnGerenciarLocalizacoes;
    @FXML private Button btnListarAcervo;
    @FXML private Button btnVoltarMenuPrincipal;

    // Variável para armazenar o usuário que fez login
    private Usuario usuarioLogado;

    // Construtor vazio (obrigatório para FXML)
    public HomeAcervoController() {}

    /**
     * Método chamado pela tela de seleção de módulo para injetar o usuário.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;

        // Atualiza a interface e as permissões assim que o usuário é injetado
        if (usuarioLogado != null) {
            lblUsuarioLogadoAcervo.setText("Logado como: " + usuarioLogado.getNome() + " (" + usuarioLogado.getTipo() + ")");
            configurarPermissoes();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada a fazer na inicialização, pois o usuário será injetado após o carregamento FXML
    }

    // ----------------------------------------------------
    // LÓGICA DE PERMISSÃO
    // ----------------------------------------------------

    /**
     * Configura a visibilidade dos botões de CRUD com base no cargo do usuário.
     * Somente o cargo "bibliotecario" deve ter acesso a essas funções.
     */
    private void configurarPermissoes() {
        // 1. Define se o usuário é um bibliotecário
        boolean isBibliotecario = "bibliotecario".equalsIgnoreCase(usuarioLogado.getTipo());

        // 2. Botões de Gerenciamento (CRUD) -> Visível SÓ para bibliotecário
        btnCadastrarItemAcervo.setVisible(isBibliotecario);
        btnCadastrarItemAcervo.setManaged(isBibliotecario);

        btnGerenciarAcervo.setVisible(isBibliotecario);
        btnGerenciarAcervo.setManaged(isBibliotecario);

        btnGerenciarLocalizacoes.setVisible(isBibliotecario);
        btnGerenciarLocalizacoes.setManaged(isBibliotecario);

        // 3. Botão de Consulta Pública -> Visível SÓ para quem NÃO é bibliotecário
        boolean isConsultaPublica = !isBibliotecario;

        btnListarAcervo.setVisible(isConsultaPublica); // Oculta se for bibliotecário
        btnListarAcervo.setManaged(isConsultaPublica);

    }

    // ----------------------------------------------------
    // AÇÕES DO MÓDULO ACERVO (CORRIGIDAS)
    // ----------------------------------------------------

    /**
     * Ação para btnCadastrarItemAcervo: Abre a tela de Cadastro de Novo Item (janela modal).
     */
    @FXML
    private void abrirCadastroItemAcervo() {
        abrirNovaJanela("/com/example/cadastromuseu/Acervo/view/CadastroItemAcervo.fxml", "1. Cadastro de Item do Acervo");
    }

    /**
     * Ação para btnGerenciarAcervo: Abre a tela de Consulta/Gerenciamento (CRUD).
     */
    @FXML
    private void abrirGestaoAcervo() {
        // Usa a lógica de abrirGestaoAcervo (ConsultaItemAcervo.fxml) que permite
        // que a janela principal continue interagindo.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/GestaoItemAcervo.fxml"));
            Parent root = loader.load();

            // Opcional: Obtém o Controller para passar dados se necessário
            // ConsultaItemAcervoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("2. Gerenciamento de Itens (CRUD)");
            stage.setScene(new Scene(root));
            stage.show(); // Usamos show() e não showAndWait() para esta tela

        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível carregar a tela de Gerenciamento.");
            System.err.println("Erro ao carregar a tela ConsultaItemAcervo.fxml: " + e.getMessage());
        }
    }

    /**
     * Ação para btnGerenciarLocalizacoes: Abre a tela de Gerenciamento de Localizações.
     */
    @FXML
    private void abrirGestaoLocalizacoes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/GestaoLocalizacao.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Localizações do Acervo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML de Gestão de Localização.");
            e.printStackTrace();
        }
    }

    /**
     * Ação para btnListarAcervo: Abre a tela de Consulta Pública do Acervo.
     */
    @FXML
    private void listarItensAcervo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/ConsultaPublicaAcervo.fxml"));
            Parent root = loader.load();

            // Não precisa passar o Controller, pois esta é uma tela de consulta simples.

            Stage stage = new Stage();
            stage.setTitle("Consulta Pública do Acervo");
            stage.setScene(new Scene(root));
            stage.show(); // Usamos show() e não showAndWait()

        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível carregar a tela de Consulta Pública.");
            System.err.println("Erro ao carregar a tela ConsultaPublicaAcervo.fxml: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // NAVEGAÇÃO
    // ----------------------------------------------------

    /**
     * Carrega a tela SelecaoModulo e fecha a tela atual,
     * injetando o Usuario Logado de volta no controller de destino.
     */
    @FXML
    private void handleVoltarMenuPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Comum/view/SelecaoModulo.fxml"));
            Parent root = loader.load();

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
            alerta(Alert.AlertType.ERROR, "Seleção de Módulos", "Não foi possível carregar a tela de Seleção de Módulos.");
            e.printStackTrace();
        }
    }

    /**
     * Realiza o Logout e volta para a tela de Login.
     */
    @FXML
    private void handleLogout() {
        Stage stage = (Stage) lblUsuarioLogadoAcervo.getScene().getWindow();
        stage.close();

        // Chama o método para exibir a tela de login novamente
        try {
            Main.showLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ----------------------------------------------------
    // UTILITÁRIOS
    // ----------------------------------------------------

    /**
     * Abre uma nova janela, bloqueando a janela principal (Modal).
     */
    private void abrirNovaJanela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível carregar a tela: " + titulo);
            System.err.println("Erro ao carregar a tela " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void alerta(Alert.AlertType type, String titulo, String mensagem) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

}