module com.example.cadastromuseu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Para o MySQL
    requires com.google.protobuf; // Se você estiver usando o MySQL Connector/J 8+
    requires org.apache.commons.dbcp2;
    requires java.desktop; // Para o pool de conexões (DBCP)

    // 🚨 PACOTES DE VIEW/CONTROLLER PRECISAM ESTAR ABERTOS PARA O FXML 🚨

    // Abre o pacote do Login/Seleção de Módulos
    opens com.example.cadastromuseu.Util.controller to javafx.fxml;

    // Abre o pacote da Biblioteca
    opens com.example.cadastromuseu.Biblioteca.controller to javafx.fxml;

    // Abre o pacote do Acervo (QUE ESTAVA CAUSANDO O ERRO!)
    opens com.example.cadastromuseu.Acervo.controller to javafx.fxml;

    // Se a classe Main estiver em com.example.cadastromuseu, abra-a também:
    opens com.example.cadastromuseu to javafx.fxml;

    // Exporta o pacote principal para a execução
    exports com.example.cadastromuseu;

    // Exporta outros pacotes, se necessário (ex: Models)
    opens com.example.cadastromuseu.Biblioteca.model to javafx.base;

    // acesse o seu pacote de modelos para carregar dados na TableView.
    opens com.example.cadastromuseu.Acervo.model to javafx.base;
}