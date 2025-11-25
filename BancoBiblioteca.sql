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
