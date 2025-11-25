
USE museu_acervo;

-- ================================
-- TIPO DE ITEM
-- ================================
CREATE TABLE tipo_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

-- ================================
-- LOCALIZAÇÃO FÍSICA
-- ================================
CREATE TABLE localizacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sala VARCHAR(50),
    estante VARCHAR(50),
    prateleira VARCHAR(50)
);

-- ================================
-- PESSOA RELACIONADA
-- ================================
CREATE TABLE pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    tipo ENUM('autor','doacao','citado','outro') NOT NULL
);

-- ================================
-- ITEM DE ACERVO
-- ================================
CREATE TABLE item_acervo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    data_item DATE,
    id_tipo INT NOT NULL,
    id_localizacao INT,
    caminho_arquivo VARCHAR(255),
    FOREIGN KEY (id_tipo) REFERENCES tipo_item(id),
    FOREIGN KEY (id_localizacao) REFERENCES localizacao(id)
);

-- Item ↔ Pessoa (N:N)
CREATE TABLE item_pessoa (
    id_item INT,
    id_pessoa INT,
    papel VARCHAR(50),
    PRIMARY KEY (id_item, id_pessoa),
    FOREIGN KEY (id_item) REFERENCES item_acervo(id),
    FOREIGN KEY (id_pessoa) REFERENCES pessoa(id)
);

-- ================================
-- PALAVRAS-CHAVE / TAGS
-- ================================
CREATE TABLE tag (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE item_tag (
    id_item INT,
    id_tag INT,
    PRIMARY KEY (id_item, id_tag),
    FOREIGN KEY (id_item) REFERENCES item_acervo(id),
    FOREIGN KEY (id_tag) REFERENCES tag(id)
);
