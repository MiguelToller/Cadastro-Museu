package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Localizacao;
import com.example.cadastromuseu.Util.conection.Conexao; // Seu caminho de conexão

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalizacaoDAO {

    /**
     * Lista todas as localizações disponíveis no banco de dados.
     * @return Lista de objetos Localizacao.
     */
    public List<Localizacao> listarTodos() {
        List<Localizacao> lista = new ArrayList<>();
        // Seleciona todos os campos da localização estruturada
        String sql = "SELECT id, sala, estante, prateleira FROM localizacao ORDER BY sala, estante, prateleira";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Localizacao l = new Localizacao(
                        rs.getInt("id"),
                        rs.getString("sala"),
                        rs.getString("estante"),
                        rs.getString("prateleira")
                );
                lista.add(l);
            }
        } catch (SQLException e) {
            // Em caso de erro, imprime no console e retorna lista vazia
            System.err.println("Erro crítico ao listar localizações: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Insere uma nova Localização no banco de dados e atualiza o ID do objeto.
     * @param localizacao O objeto Localizacao a ser inserido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public void inserir(Localizacao localizacao) throws SQLException {
        String sql = "INSERT INTO localizacao (sala, estante, prateleira) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Setar Sala (geralmente NOT NULL)
            stmt.setString(1, localizacao.getSala());

            // 2. Setar Estante: Converte String vazia para NULL
            String estante = localizacao.getEstante().trim();
            if (estante.isEmpty()) {
                // Usa setNull() se a String estiver vazia
                stmt.setNull(2, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(2, estante);
            }

            // 3. Setar Prateleira: Converte String vazia para NULL
            String prateleira = localizacao.getPrateleira().trim();
            if (prateleira.isEmpty()) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, prateleira);
            }

            // ... (resto do código para executeUpdate e getGeneratedKeys) ...
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    localizacao.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Remove uma Localização do banco de dados pelo seu ID.
     * @param id O ID da localização a ser removida.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM localizacao WHERE id = ?";

        // Usa try-with-resources para garantir que a conexão e o statement sejam fechados
        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Localização com ID " + id + " não encontrada para remoção.");
            }
        }
    }
}