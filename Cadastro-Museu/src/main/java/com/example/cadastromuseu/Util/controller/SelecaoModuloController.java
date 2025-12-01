package com.example.cadastromuseu.Util.controller;

import com.example.cadastromuseu.Biblioteca.controller.HomeBibliotecaController;
import com.example.cadastromuseu.Acervo.controller.HomeAcervoController;
import com.example.cadastromuseu.Biblioteca.model.Usuario;
import com.example.cadastromuseu.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SelecaoModuloController {

    @FXML private Label lblUsuarioLogado;
    private Usuario usuarioLogado;

    // ----------------------------------------------------
    // INJEÇÃO E INICIALIZAÇÃO
    // ----------------------------------------------------

    /**
     * Injeta o objeto Usuario logado, passado pelo LoginController.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        if (usuarioLogado != null) {
            lblUsuarioLogado.setText("Logado como: " + usuario.getNome());
        }
    }

    // ----------------------------------------------------
    // NAVEGAÇÃO
    // ----------------------------------------------------

    @FXML
    private void handleAcessarBiblioteca() {
        // Passamos 'null' para o controllerInstance, pois ele será criado pelo FXML
        abrirHome("/com/example/cadastromuseu/Biblioteca/view/HomeBiblioteca.fxml", null, "Gestão da Biblioteca");
    }

    @FXML
    private void handleAcessarAcervo() {
        // Passamos 'null' para o controllerInstance
        abrirHome("/com/example/cadastromuseu/Acervo/view/HomeAcervo.fxml", null, "Gestão do Acervo Museológico");
    }

    /**
     * Carrega uma nova tela Home (Biblioteca ou Acervo), recupera o Controller
     * criado pelo FXML e injeta o Usuario Logado.
     */
    private void abrirHome(String fxmlPath, Object ignoredControllerInstance, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Carrega o FXML. O FXML cria a instância do Controller por conta própria
            // porque ele tem o atributo fx:controller="..."
            Parent root = loader.load();

            // 🚨 RECUPERAMOS o Controller que o FXMLLoader acabou de criar 🚨
            Object controllerInstance = loader.getController();

            // Chamada para injetar o usuário na Home do Módulo
            if (controllerInstance instanceof HomeBibliotecaController) {
                // Passa o usuário para o HomeBibliotecaController
                ((HomeBibliotecaController) controllerInstance).setUsuarioLogado(usuarioLogado);
            } else if (controllerInstance instanceof HomeAcervoController) {
                // Passa o usuário para o HomeAcervoController
                ((HomeAcervoController) controllerInstance).setUsuarioLogado(usuarioLogado);
            }

            // Fecha a tela atual e abre a nova
            Stage stage = (Stage) lblUsuarioLogado.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar o módulo: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Fecha a janela atual (Seleção de Módulos)
        Stage stage = (Stage) lblUsuarioLogado.getScene().getWindow();
        stage.close();

        // Retorna para o Login
        try {
            Main.showLoginScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbrirGestaoLocalizacao() {
        try {
            // 1. Carregar o FXML da tela de gestão
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cadastromuseu/Acervo/view/GestaoLocalizacao.fxml"));
            Parent root = loader.load();

            // 2. Criar o Stage (a nova janela)
            Stage stage = new Stage();
            stage.setTitle("Gerenciar Localizações do Acervo");

            // 3. Configurações da janela
            stage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a tela mãe
            stage.setResizable(false);

            // 4. Exibir
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera até a janela ser fechada

        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML de Gestão de Localização.");
            e.printStackTrace();
            // Adicionar um alerta para o usuário em caso de erro é uma boa prática
        }
    }
}