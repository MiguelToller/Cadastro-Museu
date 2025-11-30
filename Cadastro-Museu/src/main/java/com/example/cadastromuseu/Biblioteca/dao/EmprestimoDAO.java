package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Emprestimo;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public void registrar(Emprestimo e) throws SQLException {
        if (verificarSePendente(e.getLivro().getId())) {
            throw new SQLException("O livro já está emprestado e pendente de devolução.");
        }

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
        // Inclua a coluna 'data_devolvido' no SELECT para saber o status do empréstimo
        String sql = "SELECT id, id_livro, id_usuario, data_emprestimo, data_devolucao, data_devolvido FROM emprestimo";

        LivroDAO livroDAO = new LivroDAO();    // Instâncias necessárias
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Emprestimo e = new Emprestimo();
                e.setId(rs.getInt("id"));

                // NOVIDADE: Busca e atribui os objetos completos
                int idLivro = rs.getInt("id_livro");
                e.setLivro(livroDAO.buscarPorId(idLivro));

                int idUsuario = rs.getInt("id_usuario");
                e.setUsuario(usuarioDAO.buscarPorId(idUsuario));

                e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
                e.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());

                // Mapeia se o livro foi devolvido
                Date dataDevolvido = rs.getDate("data_devolvido");
                if (dataDevolvido != null) {
                    // Seu Model Emprestimo não tem o campo dataDevolvido, mas precisaria ter
                    // para exibir o status. Por agora, vamos apenas listar os ativos.
                }

                lista.add(e);
            }
        }
        return lista;
    }

    // Dentro de EmprestimoDAO.java

    public List<Emprestimo> listarPendentes() throws SQLException {
        List<Emprestimo> lista = new ArrayList<>();
        // Filtra: data_devolvido IS NULL
        String sql = "SELECT id, id_livro, id_usuario, data_emprestimo, data_devolucao, data_devolvido FROM emprestimo WHERE data_devolvido IS NULL";

        LivroDAO livroDAO = new LivroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Emprestimo e = new Emprestimo();
                e.setId(rs.getInt("id"));

                // Busca os objetos completos
                e.setLivro(livroDAO.buscarPorId(rs.getInt("id_livro")));
                e.setUsuario(usuarioDAO.buscarPorId(rs.getInt("id_usuario")));

                e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
                e.setDataDevolucao(rs.getDate("data_devolucao").toLocalDate());

                // NOVO: Mapeia data_devolvido (será null para pendentes, mas é bom ter)
                Date dataDevolvido = rs.getDate("data_devolvido");
                if (dataDevolvido != null) {
                    e.setDataDevolvido(dataDevolvido.toLocalDate());
                }

                lista.add(e);
            }
        }
        return lista;
    }

    // Dentro de EmprestimoDAO.java

    /**
     * Verifica se um livro está atualmente emprestado (pendente de devolução).
     * @return true se o livro estiver em empréstimo ativo, false caso contrário.
     */
    public boolean verificarSePendente(int idLivro) throws SQLException {
        // Busca qualquer registro onde o id_livro seja o passado E a data_devolvido seja NULL
        String sql = "SELECT id FROM emprestimo WHERE id_livro = ? AND data_devolvido IS NULL";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idLivro);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Se encontrar uma linha, está pendente (true)
            }
        }
    }
}
