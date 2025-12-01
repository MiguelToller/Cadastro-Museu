package com.example.cadastromuseu.Acervo.controller;

import com.example.cadastromuseu.Acervo.model.ItemAcervo;
import com.example.cadastromuseu.Acervo.model.Pessoa;
import com.example.cadastromuseu.Acervo.model.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DetalhesItemAcervoController implements Initializable {

    // Componentes FXML
    @FXML private Label lblTitulo;
    @FXML private Label lblDetalhes;
    @FXML private ImageView imgItem;
    @FXML private TextArea txtaDescricao;
    @FXML private Label lblData;

    private ItemAcervo item;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Garantir que a TextArea não seja editável na visualização de detalhes
        txtaDescricao.setEditable(false);
    }

    /**
     * Define o ItemAcervo para a tela e aciona o preenchimento dos campos.
     * @param item O item de acervo a ser exibido.
     */
    public void setItem(ItemAcervo item) {
        this.item = item;
        if (item != null) {
            preencherCampos();
        }
    }

    private void preencherCampos() {
        // DADOS 1:N E SIMPLES
        lblTitulo.setText(item.getTitulo());

        String tipoNome = item.getTipoItem() != null ? item.getTipoItem().getNome() : "Não Definido";
        String localizacaoNome = item.getLocalizacao() != null ? item.getLocalizacao().toString() : "Não Definida";
        String dataItem = item.getDataItem() != null ? item.getDataItem().toString() : "S/ Data";

        lblDetalhes.setText(String.format("ID: %d | Tipo: %s | Localização: %s",
                item.getId(), tipoNome, localizacaoNome));
        lblData.setText("Data: " + dataItem);

        txtaDescricao.setText(item.getDescricao() != null ? item.getDescricao() : "Descrição indisponível.");

        // IMAGEM
        carregarImagem();
    }

    /**
     * Tenta carregar a imagem a partir do caminho do arquivo local (caminhoArquivo).
     * ⚠️ Use o console para verificar o caminho do arquivo!
     */
    private void carregarImagem() {
        String caminho = item.getCaminhoArquivo();

        if (caminho != null && !caminho.trim().isEmpty()) {
            try {
                File file = new File(caminho);
                if (file.exists()) {
                    String fileUrl = file.toURI().toString();
                    Image image = new Image(fileUrl);
                    imgItem.setImage(image);
                    // Sucesso: Mensagem informativa no console
                    System.out.println("✅ Imagem carregada com sucesso de: " + caminho);
                    return;
                } else {
                    // ❌ Falha: Mensagem de erro CLARA no console (debug)
                    System.err.println("❌ Falha no Carregamento da Imagem: Arquivo NÃO ENCONTRADO no caminho: " + caminho);
                }
            } catch (Exception e) {
                System.err.println("❌ Erro ao tentar carregar imagem do caminho local: " + caminho + ". Erro: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Aviso: O campo caminhoArquivo está vazio para o item " + item.getTitulo());
        }

        // Fallback: Carrega o placeholder (recurso interno)
        try {
            // Tenta carregar um ícone padrão (Verifique se o caminho do recurso está correto para seu projeto)
            Image placeholder = new Image(getClass().getResourceAsStream("/com/example/cadastromuseu/assets/placeholder.png"));
            imgItem.setImage(placeholder);
        } catch (Exception e) {
            System.err.println("❌ Falha ao carregar o placeholder (verifique se assets/placeholder.png existe): " + e.getMessage());
        }
    }

    @FXML
    private void handleFechar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }
}