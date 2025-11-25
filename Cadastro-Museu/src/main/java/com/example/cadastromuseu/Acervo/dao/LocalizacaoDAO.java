package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Localizacao;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalizacaoDAO {

    public List<Localizacao> listar() throws SQLException {
        List<Localizacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM localizacao";
        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            Localizacao l = new Localizacao(
                    rs.getInt("id"),
                    rs.getString("sala"),
                    rs.getString("estante"),
                    rs.getString("prateleira")
            );
            lista.add(l);
        }
        return lista;
    }
}

