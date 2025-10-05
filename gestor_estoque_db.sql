SET SQL_SAFE_UPDATES = 0;
CREATE DATABASE gestor_estoque;
USE gestor_estoque;

-- ===========================
-- TABELA DE USUÁRIOS
-- ===========================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(45) NOT NULL UNIQUE,
    email_usuario VARCHAR(45) NOT NULL UNIQUE,
    senha_usuario VARCHAR(100) NOT NULL, -- melhor deixar espaço p/ hash
    perfil_usuario ENUM('Administrador', 'Gestor', 'Operador de Estoque') NOT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultimo_acesso DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_ativo TINYINT(1) DEFAULT 1
);

-- ===========================
-- TABELA DE PERMISSÕES
-- ===========================
CREATE TABLE permissoes (
    id_permissao INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    permissao_acesso VARCHAR(100) NOT NULL,
    descricao_acesso VARCHAR(100),
    nivel_acesso ENUM('Baixo', 'Médio', 'Alto') DEFAULT 'Baixo',
    status ENUM('Ativo', 'Inativo') DEFAULT 'Ativo',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- TABELA DE FORNECEDORES
-- ===========================
CREATE TABLE fornecedores (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    nome_fornecedor VARCHAR(100) NOT NULL UNIQUE,
    cnpj_fornecedor VARCHAR(20) NOT NULL UNIQUE,
    contato_fornecedor VARCHAR(20) NOT NULL,
    email_fornecedor VARCHAR(100) NOT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ===========================
-- TABELA DE PRODUTOS
-- ===========================
CREATE TABLE produtos (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    id_fornecedor INT NOT NULL,
    nome_produto VARCHAR(100) NOT NULL UNIQUE,
    codigo_produto VARCHAR(45) NOT NULL UNIQUE,
    quantidade_estoque INT NOT NULL DEFAULT 0,
    valor_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_venda DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    validade_produto DATE,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedores(id_fornecedor) ON DELETE CASCADE
);

-- ===========================
-- TABELA DE MOVIMENTAÇÕES
-- ===========================
CREATE TABLE movimentacoes (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo_movimentacao ENUM('Entrada', 'Saída', 'Ajuste') NOT NULL,
    data_movimentacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- TABELA DE RELATÓRIOS
-- ===========================
CREATE TABLE relatorios (
    id_relatorio INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_movimentacao INT,
    semana INT NOT NULL,
    ano INT NOT NULL,
    data_inicial DATE NOT NULL,
    data_final DATE NOT NULL,
    faturamento DECIMAL(10,2) DEFAULT 0.00,
    produtos_vendidos INT DEFAULT 0,
    data_relatorio DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantidade_produtos INT DEFAULT 0,
    descricao_produtos VARCHAR(255) DEFAULT 'Não foram informados os produtos adquiridos/vendidos',
    CHECK (data_final >= data_inicial),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_movimentacao) REFERENCES movimentacoes(id_movimentacao) ON DELETE SET NULL
);

/* Checagem dos valores inseridos nas tabelas utilizando o comando SELECT */

SELECT*FROM usuarios;
SELECT*FROM permissoes;
SELECT*FROM fornecedores;
SELECT*FROM cadastro_de_produtos;
SELECT*FROM movimentacao;
SELECT*FROM relatorio;

INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, perfil_usuario, usuario_ativo)
VALUES
('adm', 'admin@empresa.com', '12', 'Admin', 1),
('gestor', 'gestor@empresa.com', '123', 'Gestor', 1),
('operador', 'operador@empresa.com', '123', 'Operador', 1);








