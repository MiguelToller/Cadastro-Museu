package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Usuario;
import com.example.cadastromuseu.Util.conection.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ----------------------------------------------------------------------
    // INSERIR
    // ----------------------------------------------------------------------
    public void inserir(Usuario usuario) throws SQLException {
        Connection con = Conexao.getConnection();
        // SQL ajustado para incluir 'senha' e 'tipo'
        String sql = "INSERT INTO usuario(nome, email, senha, tipo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getTipo()); // Mapeia o campo 'tipo'

            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------------
    // BUSCAR POR ID (Necessário para a listagem de Empréstimos)
    // ----------------------------------------------------------------------
    public Usuario buscarPorId(int id) throws SQLException {
        // Busca todas as colunas, incluindo 'senha' e 'tipo'
        String sql = "SELECT id, nome, email, senha, tipo FROM usuario WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setTipo(rs.getString("tipo")); // Mapeia o campo 'tipo'
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------------
    // LISTAR (Necessário para o ComboBox de Empréstimos)
    // ----------------------------------------------------------------------
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        // Note: Geralmente não é uma boa prática listar senhas, mas aqui listamos todas as colunas
        String sql = "SELECT id, nome, email, senha, tipo FROM usuario";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipo(rs.getString("tipo"));

                lista.add(usuario);
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------------
    // FILTRO
    // ----------------------------------------------------------------------
    public List<Usuario> listarPorFiltro(String termo) throws SQLException {
        List<Usuario> lista = new ArrayList<>();

        // 1. Converte o termo para usar no LIKE (case-insensitive)
        String termoPesquisa = "%" + termo.toLowerCase() + "%";

        // 2. Query: Busca por NOME, EMAIL ou TIPO, ignorando maiúsculas/minúsculas
        String sql = "SELECT id, nome, email, senha, tipo FROM usuario " +
                "WHERE LOWER(nome) LIKE ? OR LOWER(email) LIKE ? OR LOWER(tipo) LIKE ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 3. Define os parâmetros
            ps.setString(1, termoPesquisa); // Para a coluna NOME
            ps.setString(2, termoPesquisa); // Para a coluna EMAIL
            ps.setString(3, termoPesquisa); // Para a coluna TIPO

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setTipo(rs.getString("tipo"));

                    lista.add(usuario);
                }
            }
        }
        return lista;
    }

    /**
     * Busca um usuário pelo email e valida a senha.
     * @return O objeto Usuario completo se as credenciais forem válidas, ou null.
     */
    public Usuario buscarPorCredenciais(String email, String senha) throws SQLException {
        // Busca todas as colunas necessárias, especialmente 'senha' e 'tipo'
        String sql = "SELECT id, nome, email, senha, tipo FROM usuario WHERE email = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 1. O usuário existe. Agora, verifica a senha.
                    String senhaArmazenada = rs.getString("senha");

                    // ATENÇÃO: Em sistemas reais, a senha seria HASHED (criptografada).
                    // Aqui, fazemos uma comparação de texto simples.
                    if (senhaArmazenada.equals(senha)) {
                        // Senha correta: monta e retorna o objeto Usuario.
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setSenha(senhaArmazenada);
                        usuario.setTipo(rs.getString("tipo"));
                        return usuario;
                    }
                }
                // Usuário não encontrado ou senha incorreta
                return null;
            }
        }
    }

    // Dentro de UsuarioDAO.java

    // ======================================================================
    // U - UPDATE (Atualizar Usuário Existente)
    // ======================================================================
    public void atualizar(Usuario usuario) throws SQLException {
        // Note: Usamos o ID no WHERE para saber qual registro atualizar.
        // O campo 'senha' é opcional. Se a senha for vazia no formulário, não a atualizamos.

        // Assumindo que o Controller enviará a senha apenas se ela for alterada.
        // Se a senha for nula/vazia no objeto, atualizamos SÓ os outros campos.
        String sql;

        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            // Atualiza todos os campos, incluindo a senha
            sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, tipo = ? WHERE id = ?";
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getEmail());
                stmt.setString(3, usuario.getSenha()); // Senha nova
                stmt.setString(4, usuario.getTipo());
                stmt.setInt(5, usuario.getId());
                stmt.executeUpdate();
            }
        } else {
            // Atualiza apenas nome, email e tipo (Mantém a senha antiga)
            sql = "UPDATE usuario SET nome = ?, email = ?, tipo = ? WHERE id = ?";
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getEmail());
                stmt.setString(3, usuario.getTipo());
                stmt.setInt(4, usuario.getId());
                stmt.executeUpdate();
            }
        }
    }

    // ======================================================================
    // D - DELETE (Excluir Usuário)
    // ======================================================================
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Captura erros de FK (se o usuário tiver empréstimos ativos)
            throw new SQLException("Erro ao excluir usuário. Verifique se ele possui empréstimos pendentes. Detalhes: " + e.getMessage());
        }
    }


}