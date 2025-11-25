package com.example.cadastromuseu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/example/cadastromuseu/Home.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Sistema Museu - Biblioteca");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
