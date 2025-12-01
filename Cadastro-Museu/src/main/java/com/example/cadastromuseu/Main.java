package com.example.cadastromuseu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // ----------------------------------------------------
    // START PRINCIPAL
    // ----------------------------------------------------
    @Override
    public void start(Stage stage) throws Exception {
        // Inicializa a aplicação abrindo a tela de Login
        showLoginScreen();
    }

    // ----------------------------------------------------
    // NOVO MÉTODO ESTÁTICO PARA EXIBIR O LOGIN (showLoginScreen)
    // ----------------------------------------------------
    /**
     * Carrega e exibe a tela de Login em uma nova janela.
     * Este método é chamado para iniciar o app ou após o logout.
     */
    public static void showLoginScreen() throws IOException {
        // Cria um novo Stage (janela) para a tela de Login
        Stage loginStage = new Stage();

        FXMLLoader loader = new FXMLLoader(
                // Altera a forma de carregar o FXML para usar o caminho estático da Main
                Main.class.getResource("/com/example/cadastromuseu/Biblioteca/view/Login.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 600);

        loginStage.setTitle("Login - Museu Treze de Maio");
        loginStage.setScene(scene);
        loginStage.show();
    }

    // ----------------------------------------------------
    // MAIN
    // ----------------------------------------------------
    public static void main(String[] args) {
        launch();
    }
}