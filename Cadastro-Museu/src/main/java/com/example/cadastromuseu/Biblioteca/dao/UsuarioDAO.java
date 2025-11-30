package com.example.cadastromuseu.Biblioteca.dao;

import com.example.cadastromuseu.Biblioteca.model.Usuario;
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


}