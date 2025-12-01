package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.Pessoa;
import com.example.cadastromuseu.Util.conection.Conexao; // CORREÇÃO AQUI

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    /**
     * Salva as pessoas e as suas relações com o Item de Acervo.
     * 1. Busca Pessoa por nome. 2. Insere se não existir. 3. Insere a relação N:N.
     * @param itemId O ID do item de acervo recém-criado.
     * @param pessoas Composto por strings no formato "Nome (tipo)".
     * @throws Exception Se ocorrer um erro no acesso ao banco de dados.
     */
    public void salvarRelacoes(long itemId, List<String> pessoas) throws Exception {
        if (pessoas.isEmpty()) {
            return;
        }

        // Usa try-with-resources para garantir que a conexão será fechada
        try (Connection conn = Conexao.getConnection()) {

            // Query para buscar Pessoa por nome
            String sqlSelectPessoa = "SELECT id FROM pessoa WHERE nome = ?";
            // Query para inserir nova Pessoa
            String sqlInsertPessoa = "INSERT INTO pessoa (nome, tipo) VALUES (?, ?)";
            // Query para inserir a relação N:N
            String sqlInsertRelacao = "INSERT INTO item_pessoa (id_item, id_pessoa, papel) VALUES (?, ?, ?)";

            PreparedStatement psSelect = conn.prepareStatement(sqlSelectPessoa);
            PreparedStatement psInsertPessoa = conn.prepareStatement(sqlInsertPessoa, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement psInsertRelacao = conn.prepareStatement(sqlInsertRelacao);

            for (String pessoaString : pessoas) {
                // 1. EXTRAIR DADOS (Ex: "Miguel (autor)" -> Nome="Miguel", Tipo="autor")
                // Encontrar o nome (tudo antes do último ' (') e o tipo (entre parênteses)
                String nome = pessoaString.substring(0, pessoaString.lastIndexOf(" (")).trim();
                String tipo = pessoaString.substring(pessoaString.lastIndexOf(" (") + 2, pessoaString.lastIndexOf(")")).trim();
                long pessoaId = -1;

                // 2. BUSCAR PESSOA EXISTENTE
                psSelect.setString(1, nome);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        pessoaId = rs.getLong("id");
                    }
                }

                // 3. SE NÃO EXISTE, INSERIR NOVA
                if (pessoaId == -1) {
                    psInsertPessoa.setString(1, nome);
                    psInsertPessoa.setString(2, tipo);
                    int linhasAfetadas = psInsertPessoa.executeUpdate();

                    if (linhasAfetadas > 0) {
                        try (ResultSet rs = psInsertPessoa.getGeneratedKeys()) {
                            if (rs.next()) {
                                pessoaId = rs.getLong(1);
                            }
                        }
                    }
                    if (pessoaId == -1) {
                        throw new Exception("Falha ao obter ID da Pessoa recém-inserida: " + nome);
                    }
                }

                // 4. INSERIR RELAÇÃO item_pessoa
                psInsertRelacao.setLong(1, itemId);
                psInsertRelacao.setLong(2, pessoaId);
                psInsertRelacao.setString(3, tipo); // O 'papel'
                psInsertRelacao.executeUpdate();
            }
        }
    }

    public List<Pessoa> listarTodos() throws SQLException {
        List<Pessoa> pessoas = new ArrayList<>();
        // Assumindo que a tabela é 'pessoa' e tem colunas 'id', 'nome' e 'tipo'.
        String sql = "SELECT id, nome, tipo FROM pessoa ORDER BY nome";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getInt("id"));
                pessoa.setNome(rs.getString("nome"));
                pessoa.setTipo(rs.getString("tipo")); // Tipo de cadastro (Ex: Professor, Estudante)
                pessoas.add(pessoa);
            }
        }
        return pessoas;
    }
}