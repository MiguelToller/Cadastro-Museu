package com.example.cadastromuseu.Acervo.model;

public class Pessoa {
    private int id;
    private String nome;
    private String tipo; // autor, doacao, citado, outro

    public Pessoa() {}

    public Pessoa(int id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        // Se o tipo existir, formata como "Nome (Tipo)"; caso contrário, apenas "Nome"
        return nome + (tipo != null && !tipo.isEmpty() ? " (" + tipo + ")" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pessoa pessoa = (Pessoa) o;
        // Duas Pessoas são iguais se os IDs forem iguais
        return id == pessoa.id;
    }

    @Override
    public int hashCode() {
        return id; // Usa o ID como código hash
    }
}

