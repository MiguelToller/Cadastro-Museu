package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Tag;
import com.example.cadastromuseu.Util.conection.Conexao; // Ajuste o pacote de Conexao
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemTagDAO {

    /**
     * Busca todas as Tags relacionadas a um ItemAcervo específico.
     */
    public List<Tag> buscarTagsPorItem(int idItemAcervo) throws SQLException {
        List<Tag> tags = new ArrayList<>();

        String sql = "SELECT t.id, t.nome FROM tag t " +
                "JOIN item_tag it ON t.id = it.id_tag " +
                "WHERE it.id_item = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idItemAcervo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tag tag = new Tag();
                    tag.setId(rs.getInt("id"));
                    tag.setNome(rs.getString("nome"));
                    tags.add(tag);
                }
            }
        }
        return tags;
    }

    /**
     * Sincroniza as relações N:M (Tags) para um ItemAcervo.
     * 1. Deleta todas as relações existentes para o idItem.
     * 2. Insere as novas relações, criando tags novas se necessário.
     */
    public void sincronizarRelacoes(int idItemAcervo, List<Tag> novasTags) throws SQLException {
        Connection conn = null; // ⬅️ Variável conn declarada aqui
        String sqlDelete = "DELETE FROM item_tag WHERE id_item = ?";
        String sqlInsertRelacao = "INSERT INTO item_tag (id_item, id_tag) VALUES (?, ?)";

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. DELETAR relações existentes
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, idItemAcervo);
                psDelete.executeUpdate();
            }

            // 2. INSERIR ou ATUALIZAR Tags na tabela principal e na relação
            for (Tag tag : novasTags) {
                int tagId = tag.getId();

                // Se a tag tiver ID 0 (é nova, criada na tela de edição)
                if (tagId == 0) {
                    tagId = salvarOuBuscarTag(conn, tag.getNome()); // Cria/Busca a tag e obtém o ID
                    tag.setId(tagId); // Atualiza o ID no objeto (opcional)
                }

                // 3. INSERIR a relação na tabela item_tag
                try (PreparedStatement psInsertRelacao = conn.prepareStatement(sqlInsertRelacao)) {
                    psInsertRelacao.setInt(1, idItemAcervo);
                    psInsertRelacao.setInt(2, tagId);
                    psInsertRelacao.executeUpdate();
                }
            }

            conn.commit(); // Confirma transação

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // ⬅️ Rollback acessível aqui
            }
            throw e;
        } finally {
            if (conn != null) {
                // Tenta restaurar o autoCommit e fechar a conexão
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    // Logar ou tratar erro de fechamento se necessário
                    closeEx.printStackTrace();
                }
            }
        }
    }

    /**
     * Tenta buscar uma tag por nome. Se não existir, a insere e retorna o ID.
     * Deve ser chamado DENTRO de uma transação.
     */
    private int salvarOuBuscarTag(Connection conn, String nomeTag) throws SQLException {
        String sqlSelect = "SELECT id FROM tag WHERE nome = ?";

        // 1. Tenta buscar se já existe
        try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
            psSelect.setString(1, nomeTag);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Tag já existe, retorna o ID
                }
            }
        }

        // 2. Se não existe, insere a nova Tag
        String sqlInsert = "INSERT INTO tag (nome) VALUES (?)";
        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psInsert.setString(1, nomeTag);
            psInsert.executeUpdate();

            try (ResultSet rs = psInsert.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado
                }
            }
        }
        throw new SQLException("Falha ao obter ID da Tag recém-criada.");
    }
}