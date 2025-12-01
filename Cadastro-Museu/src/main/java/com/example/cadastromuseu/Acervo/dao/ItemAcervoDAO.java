package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.ItemAcervo;
import com.example.cadastromuseu.Acervo.model.Localizacao;
import com.example.cadastromuseu.Acervo.model.TipoItem;
import com.example.cadastromuseu.Util.conection.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemAcervoDAO {

    // ... (Seu método listarTodos continua aqui) ...
    // ... (Seu método inserir original pode ser mantido, mas não será usado pelo Controller) ...

    // ----------------------------------------------------
    // MÉTODO INSERIR (Retorna o ID gerado)
    // ----------------------------------------------------
    /**
     * Insere um novo ItemAcervo no banco de dados e retorna a chave primária gerada.
     * * @param item O objeto ItemAcervo a ser inserido.
     * @return O ID (long) do item inserido; -1 em caso de falha.
     */
    public long inserirRetornandoId(ItemAcervo item) throws SQLException {
        String sql = "INSERT INTO item_acervo (titulo, descricao, data_item, id_tipo, id_localizacao, caminho_arquivo) VALUES (?, ?, ?, ?, ?, ?)";
        long generatedId = -1;

        // Utilizamos throws SQLException para que o Controller possa tratar a exceção
        // e avisar o usuário se a conexão falhar.
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Setar os valores simples
            stmt.setString(1, item.getTitulo());
            stmt.setString(2, item.getDescricao());

            // 2. Mapeamento de LocalDate para SQL Date
            stmt.setDate(3, item.getDataItem() != null ? Date.valueOf(item.getDataItem()) : null);

            // 3. Mapeamento das Chaves Estrangeiras (Relações 1:N)
            stmt.setInt(4, item.getTipoItem().getId());

            if (item.getLocalizacao() != null && item.getLocalizacao().getId() > 0) {
                stmt.setInt(5, item.getLocalizacao().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, item.getCaminhoArquivo());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                // 4. Recuperar o ID gerado
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getLong(1);
                        item.setId((int) generatedId); // Opcional: Atualiza o objeto com o ID
                    }
                }
            }

            return generatedId;
        }
        // Não é necessário o catch aqui, pois a exceção é lançada (throws SQLException)
    }

    // ----------------------------------------------------
    // MÉTODO LISTAR TODOS (Seu código original continua...)
    // ----------------------------------------------------
    public List<ItemAcervo> listarTodos() {
        // ... (Seu método listarTodos original aqui) ...
        // [CÓDIGO OMITIDO PARA BREVIDADE]
        List<ItemAcervo> itens = new ArrayList<>();
        // Query que faz JOIN para buscar as informações de TipoItem e Localizacao
        String sql = "SELECT ia.id, ia.titulo, ia.descricao, ia.data_item, ia.caminho_arquivo, " +
                "ti.id AS tipo_id, ti.nome AS tipo_nome, " +
                "l.id AS local_id, l.sala, l.estante, l.prateleira " +
                "FROM item_acervo ia " +
                "JOIN tipo_item ti ON ia.id_tipo = ti.id " +
                "LEFT JOIN localizacao l ON ia.id_localizacao = l.id " + // LEFT JOIN, pois localização pode ser NULL
                "ORDER BY ia.titulo";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ItemAcervo item = new ItemAcervo();
                item.setId(rs.getInt("id"));
                item.setTitulo(rs.getString("titulo"));
                item.setDescricao(rs.getString("descricao"));
                item.setCaminhoArquivo(rs.getString("caminho_arquivo"));

                // Mapeamento de LocalDate
                Date sqlDate = rs.getDate("data_item");
                if (sqlDate != null) {
                    item.setDataItem(sqlDate.toLocalDate());
                }

                // Mapeamento do TipoItem (Relação 1:N)
                TipoItem tipo = new TipoItem(rs.getInt("tipo_id"), rs.getString("tipo_nome"));
                item.setTipoItem(tipo);

                // Mapeamento da Localizacao (Relação 1:N, pode ser NULL)
                if (rs.getInt("local_id") > 0) { // Verifica se a localização existe
                    Localizacao loc = new Localizacao(
                            rs.getInt("local_id"),
                            rs.getString("sala"),
                            rs.getString("estante"),
                            rs.getString("prateleira")
                    );
                    item.setLocalizacao(loc);
                }

                itens.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens de acervo: " + e.getMessage());
        }
        return itens;
    }

    public boolean atualizar(ItemAcervo item) throws SQLException {
        String sql = "UPDATE item_acervo SET titulo=?, descricao=?, data_item=?, id_tipo=?, id_localizacao=?, caminho_arquivo=? WHERE id=?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Setar os valores
            stmt.setString(1, item.getTitulo());
            stmt.setString(2, item.getDescricao());

            // 2. Mapeamento de LocalDate para SQL Date
            stmt.setDate(3, item.getDataItem() != null ? Date.valueOf(item.getDataItem()) : null);

            // 3. Mapeamento das Chaves Estrangeiras (Relações 1:N)
            stmt.setInt(4, item.getTipoItem().getId());

            if (item.getLocalizacao() != null && item.getLocalizacao().getId() > 0) {
                stmt.setInt(5, item.getLocalizacao().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, item.getCaminhoArquivo());

            // 4. Cláusula WHERE
            stmt.setInt(7, item.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    public boolean deletar(int idItem) throws SQLException {
        String sql = "DELETE FROM item_acervo WHERE id = ?";

        try (Connection conn = Conexao.getConnection()) {

            // 1. Tentar deletar o Item Principal.
            // Se houver relações ativas no banco (ON DELETE RESTRICT),
            // o banco lançará a SQLException aqui.
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idItem);
                int linhasAfetadas = stmt.executeUpdate();

                return linhasAfetadas > 0;
            }

        }
    }
}