package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Emprestimo;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public void registrar(Emprestimo e) throws SQLException {
        String sql = "INSERT INTO emprestimo(id_livro,id_usuario,data_emprestimo,data_devolucao) VALUES(?,?,?,?)";
        PreparedStatement ps = Conexao.getConnection().prepareStatement(sql);

        ps.setInt(1, e.getLivro().getId());
        ps.setInt(2, e.getUsuario().getId());
        ps.setDate(3, Date.valueOf(e.getDataEmprestimo()));
        ps.setDate(4, Date.valueOf(e.getDataDevolucao()));
        ps.executeUpdate();
    }

    public void registrarDevolucao(int idEmprestimo) throws SQLException {
        String sql = "UPDATE emprestimo SET data_devolvido = CURRENT_DATE WHERE id = ?";
        PreparedStatement ps = Conexao.getConnection().prepareStatement(sql);
        ps.setInt(1, idEmprestimo);
        ps.executeUpdate();
    }

    public List<Emprestimo> listar() throws SQLException {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo";

        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            Emprestimo e = new Emprestimo();
            e.setId(rs.getInt("id"));
            e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
            e.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());
            lista.add(e);
        }
        return lista;
    }
}
