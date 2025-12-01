package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Editora;
import com.example.cadastromuseu.Util.conection.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditoraDAO {

    /**
     * Busca todas as editoras no banco de dados.
     * @return Uma lista de objetos Editora.
     */
    public List<Editora> listar() throws SQLException {
        List<Editora> listaEditoras = new ArrayList<>();
        String sql = "SELECT id, nome FROM editora ORDER BY nome"; // Assume o nome da tabela 'editora'

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Editora editora = new Editora();
                editora.setId(rs.getInt("id"));
                editora.setNome(rs.getString("nome"));
                listaEditoras.add(editora);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar editoras: " + e.getMessage());
            throw e;
        }
        return listaEditoras;
    }

    // Opcional: Adicionar método inserir para cadastrar novas editoras, se houver tela para isso.
    // ...
}