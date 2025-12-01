package com.example.cadastromuseu.Acervo.model;

public class Tag {
    private int id;
    private String nome;

    public Tag() {}

    public Tag(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Verifica se o objeto é nulo ou de uma classe diferente
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;
        // Duas Tags são iguais se os IDs forem iguais
        return id == tag.id;
    }

    @Override
    public int hashCode() {
        return id; // Usa o ID como código hash
    }
}

