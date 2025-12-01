package com.example.cadastromuseu.Acervo.model;


import java.time.LocalDate;
import java.util.List;

public class ItemAcervo {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate dataItem;
    private TipoItem tipoItem;
    private Localizacao localizacao;
    private String caminhoArquivo;

    private List<Pessoa> pessoas;
    private List<Tag> tags;

    public ItemAcervo() {}

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataItem() { return dataItem; }
    public void setDataItem(LocalDate dataItem) { this.dataItem = dataItem; }
    public TipoItem getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItem tipoItem) { this.tipoItem = tipoItem; }
    public Localizacao getLocalizacao() { return localizacao; }
    public void setLocalizacao(Localizacao localizacao) { this.localizacao = localizacao; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
    public List<Pessoa> getPessoas() { return pessoas; }
    public void setPessoas(List<Pessoa> pessoas) { this.pessoas = pessoas; }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() {
        return titulo;
    }
}
