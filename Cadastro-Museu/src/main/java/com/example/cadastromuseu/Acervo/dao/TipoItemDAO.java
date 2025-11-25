package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.TipoItem;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoItemDAO {

    public List<TipoItem> listar() throws SQLException {
        List<TipoItem> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_item";
        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            lista.add(new TipoItem(rs.getInt("id"), rs.getString("nome")));
        }
        return lista;
    }
}

