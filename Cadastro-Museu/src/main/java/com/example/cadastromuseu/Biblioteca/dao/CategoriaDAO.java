package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Categoria;
import com.example.cadastromuseu.Util.conection.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    /**
     * Busca todas as categorias no banco de dados.
     * @return Uma lista de objetos Categoria.
     */
    public List<Categoria> listar() throws SQLException {
        List<Categoria> listaCategorias = new ArrayList<>();
        String sql = "SELECT id, descricao FROM categoria ORDER BY descricao"; // Assume o nome da tabela 'categoria'

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setDescricao(rs.getString("descricao")); // Usa o atributo 'descricao' do seu Model
                listaCategorias.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            throw e;
        }
        return listaCategorias;
    }

    // Opcional: Adicionar método inserir para cadastrar novas categorias.
    // ...
}