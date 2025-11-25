package com.example.cadastromuseu.Acervo.dao;

import com.example.cadastromuseu.Acervo.model.*;
import com.example.cadastromuseu.Biblioteca.dao.Conexao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemAcervoDAO {

    public void inserir(ItemAcervo item) throws SQLException {
        String sql = """
            INSERT INTO item_acervo (titulo, descricao, data_item, id_tipo, id_localizacao, caminho_arquivo)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        Connection con = Conexao.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, item.getTitulo());
        ps.setString(2, item.getDescricao());
        ps.setDate(3, Date.valueOf(item.getDataItem()));
        ps.setInt(4, item.getTipoItem().getId());
        ps.setInt(5, item.getLocalizacao().getId());
        ps.setString(6, item.getCaminhoArquivo());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) item.setId(rs.getInt(1));

        // pessoas vinculadas
        for (Pessoa p : item.getPessoas()) {
            PreparedStatement pa = con.prepareStatement("INSERT INTO item_pessoa VALUES (?, ?, ?)");
            pa.setInt(1, item.getId());
            pa.setInt(2, p.getId());
            pa.setString(3, p.getTipo());
            pa.executeUpdate();
        }

        // tags vinculadas
        for (Tag t : item.getTags()) {
            PreparedStatement pt = con.prepareStatement("INSERT INTO item_tag VALUES (?, ?)");
            pt.setInt(1, item.getId());
            pt.setInt(2, t.getId());
            pt.executeUpdate();
        }
    }

    public List<ItemAcervo> listar() throws SQLException {
        List<ItemAcervo> lista = new ArrayList<>();

        String sql = """
            SELECT ia.*, ti.nome AS tipo_nome
            FROM item_acervo ia
            JOIN tipo_item ti ON ia.id_tipo = ti.id
        """;

        ResultSet rs = Conexao.getConnection().createStatement().executeQuery(sql);

        while (rs.next()) {
            ItemAcervo item = new ItemAcervo();

            item.setId(rs.getInt("id"));
            item.setTitulo(rs.getString("titulo"));
            item.setDescricao(rs.getString("descricao"));
            item.setDataItem(rs.getDate("data_item").toLocalDate());

            item.setTipoItem(new TipoItem(
                    rs.getInt("id_tipo"),
                    rs.getString("tipo_nome")
            ));

            lista.add(item);
        }
        return lista;
    }
}

