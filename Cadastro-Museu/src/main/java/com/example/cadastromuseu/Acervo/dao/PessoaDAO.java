package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Pessoa;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    public List<Pessoa> listar() throws SQLException {
        List<Pessoa> lista = new ArrayList<>();
        String sql = "SELECT * FROM pessoa";
        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            lista.add(new Pessoa(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("tipo")
            ));
        }
        return lista;
    }
}

