package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.TipoItem;
import com.example.cadastromuseu.Util.conection.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoItemDAO {

    // ----------------------------------------------------
    // MÉTODO LISTAR TODOS
    // ----------------------------------------------------
    public List<TipoItem> listarTodos() {
        List<TipoItem> tipos = new ArrayList<>();
        String sql = "SELECT id, nome FROM tipo_item ORDER BY nome";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoItem tipo = new TipoItem();
                tipo.setId(rs.getInt("id"));
                tipo.setNome(rs.getString("nome"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos de item: " + e.getMessage());
        }
        return tipos;
    }

    // ----------------------------------------------------
    // MÉTODOS CRUD BÁSICOS
    // ----------------------------------------------------

    public boolean inserir(TipoItem tipo) {
        String sql = "INSERT INTO tipo_item (nome) VALUES (?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tipo.getNome());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tipo.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir tipo de item: " + e.getMessage());
            return false;
        }
    }
}