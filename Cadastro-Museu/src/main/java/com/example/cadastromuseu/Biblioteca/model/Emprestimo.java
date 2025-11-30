package com.example.cadastromuseu.Biblioteca.model;

import java.time.LocalDate;

public class Emprestimo {
    private int id;
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private LocalDate dataDevolvido;

    public Emprestimo() {}


    public Emprestimo(int id, Usuario usuario, Livro livro, LocalDate dataEmprestimo, LocalDate dataDevolucao, LocalDate dataDevolvido) {
        this.id = id;
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.dataDevolvido = dataDevolvido;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
    public LocalDate getDataDevolvido() { return dataDevolvido; }
    public void setDataDevolvido(LocalDate localDate) { this.dataDevolvido = dataDevolvido; }

    // Método auxiliar para verificar o status
    public boolean isPendente() {
        return dataDevolvido == null;
    }
}
