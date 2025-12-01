package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Autor;
import com.example.cadastromuseu.Util.conection.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    public void inserir(Autor autor) throws SQLException {
        Connection con = Conexao.getConnection();
        String sql = "INSERT INTO autor(nome, nacionalidade) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, autor.getNome());
        ps.setString(2, autor.getNacionalidade());
        ps.executeUpdate();
    }


    public List<Autor> listar() throws SQLException {
        List<Autor> lista = new ArrayList<>();
        Connection con = Conexao.getConnection();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM autor");


        while (rs.next()) {
            lista.add(new Autor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("nacionalidade")
            ));
        }
        return lista;
    }
}
