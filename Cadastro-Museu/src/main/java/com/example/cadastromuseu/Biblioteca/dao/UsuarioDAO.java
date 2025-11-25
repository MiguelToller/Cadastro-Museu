package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario usuario) throws SQLException {
        Connection con = Conexao.getConnection();
        String sql = "INSERT INTO usuario(nome, email) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, usuario.getNome());
        ps.setString(2, usuario.getEmail());
        ps.executeUpdate();
    }


    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        ResultSet rs = Conexao.getConnection().createStatement()
                .executeQuery("SELECT * FROM usuario");


        while (rs.next()) {
            lista.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email")
            ));
        }
        return lista;
    }
}
