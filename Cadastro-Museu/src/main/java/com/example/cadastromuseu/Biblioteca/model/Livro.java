package com.example.cadastromuseu.Biblioteca.model;

public class Livro {

    private int id;
    private String titulo;
    private Integer anoPublicacao;
    private String isbn;
    private int idEditora;
    private int idCategoria;

    public Livro() {}

    public Livro(String titulo, Integer anoPublicacao, String isbn, int idEditora, int idCategoria) {
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.isbn = isbn;
        this.idEditora = idEditora;
        this.idCategoria = idCategoria;
    }

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getIdEditora() { return idEditora; }
    public void setIdEditora(int idEditora) { this.idEditora = idEditora; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    @Override
    public String toString() {
        return titulo;
    }
}

