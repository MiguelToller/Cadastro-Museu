package com.example.cadastromuseu.Biblioteca.controller;

import com.example.cadastromuseu.Biblioteca.dao.EmprestimoDAO;
import com.example.cadastromuseu.Biblioteca.dao.LivroDAO;
import com.example.cadastromuseu.Biblioteca.dao.UsuarioDAO;
import com.example.cadastromuseu.Biblioteca.model.Emprestimo;
import com.example.cadastromuseu.Biblioteca.model.Livro;
import com.example.cadastromuseu.Biblioteca.model.Usuario;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EmprestimoController implements Initializable {

    // ComboBoxes para selecionar Livro e Usuário (dependências)
    @FXML private ComboBox<Livro> cbLivro;
    @FXML private ComboBox<Usuario> cbUsuario;

    // DatePickers para as datas
    @FXML private DatePicker dpDataEmprestimo;
    @FXML private DatePicker dpDataDevolucao;

    // Instâncias de DAOs
    private EmprestimoDAO emprestimoDao = new EmprestimoDAO();
    private LivroDAO livroDao = new LivroDAO();
    private UsuarioDAO usuarioDao = new UsuarioDAO();

    // ----------------------------------------------------------------------
    // 1. INITIALIZE: Carrega os Livros e Usuários nas ComboBoxes
    // ----------------------------------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Carrega a lista de Livros disponíveis (assume que LivroDAO.listar() funciona)
            cbLivro.setItems(FXCollections.observableArrayList(livroDao.listar()));

            // Carrega a lista de Usuários (assume que UsuarioDAO.listar() funciona)
            cbUsuario.setItems(FXCollections.observableArrayList(usuarioDao.listar()));

            // Define a data de empréstimo padrão para hoje
            dpDataEmprestimo.setValue(LocalDate.now());

        } catch (SQLException e) {
            alerta("Erro de Conexão", "Não foi possível carregar as listas de Livros e Usuários.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // 2. REGISTRAR: Valida e salva o novo empréstimo
    // ----------------------------------------------------------------------
    @FXML
    public void registrarEmprestimo(ActionEvent event) {

        // --- VALIDAÇÃO DE CAMPOS ---
        if (cbLivro.getSelectionModel().isEmpty() ||
                cbUsuario.getSelectionModel().isEmpty() ||
                dpDataEmprestimo.getValue() == null ||
                dpDataDevolucao.getValue() == null) {

            alerta("Erro de Registro", "Por favor, selecione o Livro, o Usuário e defina as datas.", Alert.AlertType.WARNING);
            return;
        }

        // Validação básica de datas
        if (dpDataDevolucao.getValue().isBefore(dpDataEmprestimo.getValue())) {
            alerta("Erro de Data", "A Data de Devolução não pode ser anterior à Data de Empréstimo.", Alert.AlertType.WARNING);
            return;
        }

        // --- Persistência de Dados ---
        try {
            // Obtém os objetos Model selecionados
            Livro livroSelecionado = cbLivro.getSelectionModel().getSelectedItem();
            Usuario usuarioSelecionado = cbUsuario.getSelectionModel().getSelectedItem();

            // Cria o objeto Emprestimo
            Emprestimo novoEmprestimo = new Emprestimo();
            novoEmprestimo.setLivro(livroSelecionado);
            novoEmprestimo.setUsuario(usuarioSelecionado);
            novoEmprestimo.setDataEmprestimo(dpDataEmprestimo.getValue());
            novoEmprestimo.setDataDevolucao(dpDataDevolucao.getValue());

            // Chamada ao DAO de Empréstimo
            emprestimoDao.registrar(novoEmprestimo);

            alerta("Sucesso!", "Empréstimo do livro '" + livroSelecionado.getTitulo() +
                    "' para " + usuarioSelecionado.getNome() + " registrado com sucesso!", Alert.AlertType.INFORMATION);

            // Limpa os campos após o sucesso
            limparCampos();

        } catch (SQLException e) {
            // NOVO: Se o erro for a verificação de livro pendente, a mensagem será amigável
            if (e.getMessage().contains("O livro já está emprestado")) {
                alerta("Empréstimo Não Autorizado", e.getMessage(), Alert.AlertType.WARNING);
            } else {
                // Se for outro erro SQL (conexão, sintaxe, etc.)
                alerta("Erro de Banco de Dados", "Falha ao registrar o empréstimo. Erro: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } catch (Exception e) {
            alerta("Erro Geral", "Ocorreu um erro inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------
    // MÉTODOS AUXILIARES E DE NAVEGAÇÃO
    // ----------------------------------------------------------------------

    @FXML
    public void voltar(ActionEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void limparCampos() {
        cbLivro.getSelectionModel().clearSelection();
        cbUsuario.getSelectionModel().clearSelection();
        dpDataEmprestimo.setValue(LocalDate.now());
        dpDataDevolucao.setValue(null);
    }

    private void alerta(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}