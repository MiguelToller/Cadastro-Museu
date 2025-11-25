package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Tag;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public List<Tag> listar() throws SQLException {
        List<Tag> lista = new ArrayList<>();
        String sql = "SELECT * FROM tag";
        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            lista.add(new Tag(rs.getInt("id"), rs.getString("nome")));
        }
        return lista;
    }
}

