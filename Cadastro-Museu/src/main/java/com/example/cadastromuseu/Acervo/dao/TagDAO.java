package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Util.conection.Conexao; // CORREÇÃO AQUI
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class TagDAO {

    /**
     * Salva as tags e as suas relações com o Item de Acervo.
     * 1. Busca Tag por nome. 2. Insere se não existir. 3. Insere a relação N:N.
     * @param itemId O ID do item de acervo recém-criado.
     * @param tags Lista de strings (nomes das tags).
     * @throws Exception Se ocorrer um erro no acesso ao banco de dados.
     */
    public void salvarRelacoes(long itemId, List<String> tags) throws Exception {
        if (tags.isEmpty()) {
            return;
        }

        try (Connection conn = Conexao.getConnection()) {

            // Query para buscar Tag por nome
            String sqlSelectTag = "SELECT id FROM tag WHERE nome = ?";
            // Query para inserir nova Tag
            String sqlInsertTag = "INSERT INTO tag (nome) VALUES (?)";
            // Query para inserir a relação N:N
            String sqlInsertRelacao = "INSERT INTO item_tag (id_item, id_tag) VALUES (?, ?)";

            PreparedStatement psSelect = conn.prepareStatement(sqlSelectTag);
            PreparedStatement psInsertTag = conn.prepareStatement(sqlInsertTag, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement psInsertRelacao = conn.prepareStatement(sqlInsertRelacao);

            for (String nomeTag : tags) {
                long tagId = -1;

                // 1. BUSCAR TAG EXISTENTE
                psSelect.setString(1, nomeTag);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        tagId = rs.getLong("id");
                    }
                }

                // 2. SE NÃO EXISTE, INSERIR NOVA
                if (tagId == -1) {
                    psInsertTag.setString(1, nomeTag);
                    int linhasAfetadas = psInsertTag.executeUpdate();

                    if (linhasAfetadas > 0) {
                        try (ResultSet rs = psInsertTag.getGeneratedKeys()) {
                            if (rs.next()) {
                                tagId = rs.getLong(1);
                            }
                        }
                    }
                    if (tagId == -1) {
                        throw new Exception("Falha ao obter ID da Tag recém-inserida: " + nomeTag);
                    }
                }

                // 3. INSERIR RELAÇÃO item_tag
                psInsertRelacao.setLong(1, itemId);
                psInsertRelacao.setLong(2, tagId);
                psInsertRelacao.executeUpdate();
            }
        }
    }
}