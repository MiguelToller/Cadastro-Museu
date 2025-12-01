package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Pessoa;
import com.example.cadastromuseu.Util.conection.Conexao; // Ajuste o pacote de Conexao
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemPessoaDAO {

    /**
     * Busca todas as Pessoas relacionadas a um ItemAcervo específico.
     * O 'tipo' da Pessoa no modelo é preenchido com o 'papel' da relação N:M.
     */
    public List<Pessoa> buscarPessoasPorItem(int idItemAcervo) throws SQLException {
        List<Pessoa> pessoas = new ArrayList<>();

        // SQL: Faz JOIN da tabela 'pessoa' com a tabela intermediária 'item_pessoa'
        String sql = "SELECT p.id, p.nome, ip.papel " +
                "FROM pessoa p " +
                "JOIN item_pessoa ip ON p.id = ip.id_pessoa " +
                "WHERE ip.id_item = ?"; // Busca pelo ID do Item

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idItemAcervo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pessoa pessoa = new Pessoa();
                    pessoa.setId(rs.getInt("id"));
                    pessoa.setNome(rs.getString("nome"));
                    // O campo 'papel' da tabela intermediária é armazenado no campo 'tipo' do modelo Pessoa
                    pessoa.setTipo(rs.getString("papel"));
                    pessoas.add(pessoa);
                }
            }
        }
        return pessoas;
    }

    /**
     * Sincroniza as relações N:M para um ItemAcervo.
     * 1. Deleta todas as relações existentes para o idItem.
     * 2. Insere as novas relações (a lista de pessoas que está na ListView).
     */
    public void sincronizarRelacoes(int idItemAcervo, List<Pessoa> novasPessoas) throws SQLException {
        String sqlDelete = "DELETE FROM item_pessoa WHERE id_item = ?";
        String sqlInsert = "INSERT INTO item_pessoa (id_item, id_pessoa, papel) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação

            // 1. DELETAR relações existentes
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, idItemAcervo);
                psDelete.executeUpdate();
            }

            // 2. INSERIR novas relações
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                for (Pessoa pessoa : novasPessoas) {
                    psInsert.setInt(1, idItemAcervo);
                    psInsert.setInt(2, pessoa.getId());
                    // O campo 'tipo' no modelo Pessoa é o 'papel' na tabela intermediária
                    psInsert.setString(3, pessoa.getTipo());
                    psInsert.addBatch(); // Adiciona ao lote de execução
                }
                psInsert.executeBatch(); // Executa todas as inserções
            }

            conn.commit(); // Confirma transação
        } catch (SQLException e) {
            throw e;
        }
    }




}