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

    @Override
    public String toString() {
        // Se a localização for a opção 'Não Definida' (ID 0), retorna a string definida no Controller
        if (this.id == 0) {
            return "Não Definida";
        }

        // Constrói uma descrição completa
        StringBuilder sb = new StringBuilder();

        // Adiciona a Sala (sempre obrigatória)
        sb.append("Sala: ").append(sala);

        // Adiciona Estante e Prateleira se não forem nulas ou vazias
        if (estante != null && !estante.trim().isEmpty()) {
            sb.append(" | Estante: ").append(estante);
        }
        if (prateleira != null && !prateleira.trim().isEmpty()) {
            sb.append(" | Prateleira: ").append(prateleira);
        }

        return sb.toString();
    }
}
