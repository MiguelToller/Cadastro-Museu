package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public void inserir(Livro livro) throws SQLException {
        String sql = "INSERT INTO livro (titulo, ano_publicacao, isbn, id_editora, id_categoria) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setObject(2, livro.getAnoPublicacao());
            stmt.setString(3, livro.getIsbn());
            stmt.setInt(4, livro.getIdEditora());
            stmt.setInt(5, livro.getIdCategoria());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Livro> listar() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
                livro.setIsbn(rs.getString("isbn"));
                livro.setIdEditora(rs.getInt("id_editora"));
                livro.setIdCategoria(rs.getInt("id_categoria"));
                lista.add(livro);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Livro buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, ano_publicacao, isbn, id_editora, id_categoria FROM livro WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = new Livro();
                    livro.setId(rs.getInt("id"));
                    livro.setTitulo(rs.getString("titulo"));
                    livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
                    livro.setIsbn(rs.getString("isbn"));
                    livro.setIdEditora(rs.getInt("id_editora"));
                    livro.setIdCategoria(rs.getInt("id_categoria"));
                    return livro;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar livro por ID: " + e.getMessage());
        }
        return null; // Retorna null se não encontrar
    }

    public List<Livro> listarTodos() throws SQLException {
        List<Livro> livros = new ArrayList<>();
        // Ajuste a query para selecionar todas as colunas necessárias do Livro
        String sql = "SELECT id, titulo, ano_publicacao, isbn, id_editora, id_categoria FROM livro";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
                livro.setIsbn(rs.getString("isbn"));
                livro.setIdEditora(rs.getInt("id_editora"));
                livro.setIdCategoria(rs.getInt("id_categoria"));
                // IMPORTANTE: Em sistemas reais, você carregaria Editora e Categoria aqui.
                // Para simplificar, estamos apenas carregando os IDs (chaves estrangeiras).
                livros.add(livro);
            }
        }
        return livros;
    }
}
