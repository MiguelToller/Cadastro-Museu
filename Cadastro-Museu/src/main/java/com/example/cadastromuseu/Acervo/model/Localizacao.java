package com.example.cadastromuseu.Acervo.model;

public class Localizacao {
    private int id;
    private String sala;
    private String estante;
    private String prateleira;

    public Localizacao() {}

    public Localizacao(int id, String sala, String estante, String prateleira) {
        this.id = id;
        this.sala = sala;
        this.estante = estante;
        this.prateleira = prateleira;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }
    public String getEstante() { return estante; }
    public void setEstante(String estante) { this.estante = estante; }
    public String getPrateleira() { return prateleira; }
    public void setPrateleira(String prateleira) { this.prateleira = prateleira; }
}
