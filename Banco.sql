CREATE DATABASE museu_biblioteca;
USE museu_biblioteca;

-- ================================
-- TABELA USUÁRIOS DO SISTEMA
-- ================================
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo ENUM('bibliotecario', 'pesquisador', 'comunidade') NOT NULL
);

-- ================================
-- AUTORES
-- ================================
CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL
);

-- ================================
-- CATEGORIAS
-- ================================
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL
);

-- ================================
-- EDITORAS
-- ================================
CREATE TABLE editora (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL
);

-- ================================
-- LIVROS
-- ================================
CREATE TABLE livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    ano_publicacao INT,
    isbn VARCHAR(20) UNIQUE,
    id_editora INT,
    id_categoria INT,
    FOREIGN KEY (id_editora) REFERENCES editora(id),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id)
);

-- Tabela de relação N:N entre livros e autores
CREATE TABLE livro_autor (
    id_livro INT,
    id_autor INT,
    PRIMARY KEY (id_livro, id_autor),
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_autor) REFERENCES autor(id)
);

-- ================================
-- EMPRÉSTIMOS
-- ================================
CREATE TABLE emprestimo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_livro INT NOT NULL,
    id_usuario INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE,
    data_devolvido DATE,
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

INSERT INTO editora (nome) VALUES ('Principis');
INSERT INTO editora (nome) VALUES ('Companhia das Letras');

INSERT INTO categoria (descricao) VALUES ('Ficção Científica');
INSERT INTO categoria (descricao) VALUES ('Política');
INSERT INTO categoria (descricao) VALUES ('Biografia');

INSERT INTO usuario (nome, email, senha, tipo) 
VALUES ('Admin', 'admin@museu.com', '123', 'bibliotecario');

SELECT * FROM LIVRO;
SELECT * FROM EMPRESTIMO;

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

-- Inserções na tabela tipo_item
INSERT INTO tipo_item (nome) VALUES 
('Pintura'), 
('Escultura'), 
('Documento Histórico'), 
('Artefato Arqueológico'),
('Fotografia'),
('Móvel'),
('Vestuário');

-- Inserções na tabela localizacao
INSERT INTO localizacao (sala, estante, prateleira) VALUES 
('Sala 101', 'A', '3'),          -- Sala de exposição principal
('Depósito Central', 'B', '1'),  -- Armazenamento
('Corredor Leste', 'Exposição', NULL), -- Exposição em um corredor, sem estante
('Sala de Restauro', 'Mesa R5', NULL);
