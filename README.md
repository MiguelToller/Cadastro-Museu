# 🏛️ Sistema de Cadastro e Gerenciamento de Acervo (Museu)

Este projeto é uma aplicação JavaFX para o gerenciamento de itens de um acervo (museu), permitindo o cadastro, consulta, e gestão básica de itens, tipos de item, e localização. O sistema inclui um módulo de controle de acesso (login) e diferentes níveis de permissão (ex: Bibliotecário vs. Usuário Comum).

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Descrição |
| :--- | :--- |
| **Java** | Linguagem principal do projeto. |
| **JavaFX** | Framework para construção da interface gráfica (Desktop). |
| **JDBC** | API para conexão e manipulação do banco de dados. |
| **MySQL** | Sistema de Gerenciamento de Banco de Dados (SGBD) utilizado. |
| **Maven** | Ferramenta de gerenciamento de dependências e construção do projeto. |

---

## ⚙️ Configuração e Instalação

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### 1. Requisitos Prévios

* **JDK (Java Development Kit):** Versão 17 ou superior (o projeto utiliza JavaFX 17).
* **Maven:** Instalado e configurado.
* **MySQL Server:** Instalado e em execução (necessário para o banco de dados).

### 2. Configuração do Banco de Dados

1.  Crie um banco de dados chamado: `cadastro_museu` (ou o nome que você definiu).
2.  Execute o script SQL para criar as tabelas necessárias (`item_acervo`, `tipo_item`, `localizacao`, `usuario`, etc.).

    > **[⚠️ Importante]** Você deve ter um script SQL com a estrutura das tabelas. Se não tiver, crie-o manualmente com base nos seus modelos (DAO).

3.  Atualize os dados de conexão no arquivo de configuração (`Conexao.java` ou similar, localizado em `com.example.cadastromuseu.Util.conection`):

    ```java
    // Exemplo de configuração no Conexao.java
    private static final String URL = "jdbc:mysql://localhost:3306/cadastro_museu";
    private static final String USER = "seu_usuario"; // Mude aqui
    private static final String PASSWORD = "sua_senha"; // Mude aqui
    ```

### 3. Execução do Projeto

1.  **Clonar o Repositório:**
    ```bash
    git clone 'link do projeto'
    ```

2.  **Compilar e Empacotar (via Maven):**
    ```bash
    mvn clean install
    ```

3.  **Executar a Aplicação:**
    O projeto utiliza o módulo JavaFX (Módulos da Aplicação), geralmente iniciado pela classe `com.example.cadastromuseu.Main`.

    Se você estiver usando uma IDE (IntelliJ IDEA, Eclipse), basta rodar a classe `Main`.

---

## ✨ Funcionalidades Principais

### Módulo Acervo

* **Listagem de Itens:** Visualização paginada ou completa do acervo.
* **Gerenciamento (CRUD):**
    * **Cadastro:** Adição de novos itens com título, descrição, data, tipo e localização.
    * **Edição e Exclusão:** Disponível apenas para usuários com permissão (`bibliotecario`).
* **Consulta Pública:** Tela simplificada de busca e visualização de detalhes, acessível a usuários comuns (sem botões de CRUD).
* **Associação de Dados (1:N):** Vinculação de itens a um `TipoItem` e `Localizacao`.

### Controle de Acesso

* **Login:** Tela inicial de autenticação.
* **Permissões:** Separação de funcionalidades baseada no cargo do usuário (ex: `bibliotecario` tem acesso ao Gerenciamento; outros têm acesso à Consulta Pública).

---

## 📁 Estrutura do Projeto

A arquitetura do projeto segue o padrão **MVC (Model-View-Controller)**, organizado por módulos de negócio (`Acervo` e `Biblioteca`).

```
C:.
├───.idea
├───Cadastro-Museu
│   ├───src
│   │   └───main
│   │       ├───java
│   │       │   └───com
│   │       │       └───example
│   │       │           └───cadastromuseu
│   │       │               ├───Acervo
│   │       │               │   ├───controller
│   │       │               │   ├───dao
│   │       │               │   └───model
│   │       │               ├───Biblioteca
│   │       │               │   ├───controller
│   │       │               │   ├───dao
│   │       │               │   └───model
│   │       │               └───Util
│   │       │                   ├───conection
│   │       │                   └───controller
│   │       └───resources
│   │           └───com
│   │               └───example
│   │                   └───cadastromuseu
│   │                       ├───Acervo
│   │                       │   └───view
│   │                       ├───Biblioteca
│   │                       │   └───view
│   │                       └───Comum
│   │                           └───view
│   └───target
│       └───classes
│           └───com
│               └───example
│                   └───cadastromuseu
│                       ├───Acervo
│                       │   ├───controller
│                       │   ├───dao
│                       │   ├───model
│                       │   └───view
│                       ├───Biblioteca
│                       │   ├───controller
│                       │   ├───dao
│                       │   ├───model
│                       │   └───view
│                       ├───Comum
│                       │   └───view
│                       └───Util
│                           ├───conection
│                           └───controller
└───target
    └───generated-sources
        └───annotations
```

---

## 📝 Licença

Este projeto está licenciado sob os termos da licença MIT.  
Consulte o arquivo [LICENSE](LICENSE) para mais informações.

---

## 👤 Autores

**Luiz Miguel Toller Marconatto**  
Curso de Ciência da Computação – Universidade Franciscana (UFN)  

E-mail: luizmigueltoller@gmail.com  
GitHub: [@MiguelToller](https://github.com/MiguelToller)

---

**Gabriel Teixeira**  
Curso de Ciência da Computação – Universidade Franciscana (UFN)  

E-mail:  
GitHub: [@Teixeirx](https://github.com/Teixeirx)
