module com.example.cadastromuseu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.cadastromuseu to javafx.fxml;
    exports com.example.cadastromuseu;
    exports com.example.cadastromuseu.Biblioteca.controller;
    opens com.example.cadastromuseu.Biblioteca.controller to javafx.fxml;
    opens com.example.cadastromuseu.Biblioteca.model to javafx.base;
}