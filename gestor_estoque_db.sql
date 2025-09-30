SET SQL_SAFE_UPDATES = 0;
CREATE DATABASE gestor_estoque;
USE gestor_estoque;

CREATE TABLE usuarios (
	id_usuario INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nome_usuario VARCHAR(45) NOT NULL UNIQUE,
    email_usuario VARCHAR(45) NOT NULL UNIQUE,
    senha_usuario VARCHAR(45) NOT NULL UNIQUE,
    perfil_usuario VARCHAR(45) NOT NULL,
    data_cadastro DATETIME NOT NULL,
    ultimo_acesso DATETIME NOT NULL,
    usuario_ativo TINYINT NOT NULL
);

CREATE TABLE permissoes (
	id_permissoes INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    permissao_acesso VARCHAR(255) NOT NULL,
    descricao_acesso VARCHAR(100) NOT NULL,
    nivel_acesso VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE fornecedores (
	id_fornecedores INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nome_fornecedor VARCHAR(45) NOT NULL UNIQUE,
    cnpj_fornecedor VARCHAR(45) NOT NULL UNIQUE,
    contato_fornecedor INT NOT NULL UNIQUE,
    email_fornecedor VARCHAR(45) NOT NULL UNIQUE,
    data_cadastro DATETIME NOT NULL
);

CREATE TABLE cadastro_de_produtos (
	id_produto INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_fornecedores INT,
    FOREIGN KEY (id_fornecedores) REFERENCES fornecedores(id_fornecedores) ON DELETE CASCADE,
    nome_produto VARCHAR(45) NOT NULL UNIQUE,
    codigo_produto VARCHAR(45) NOT NULL UNIQUE,
    quantidade_estoque INT NOT NULL,
    valor_compra DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    valor_venda DECIMAL(8,2) NOT NULL DEFAULT 0.00,
	validade_produto DATE,
    data_cadastro DATETIME NOT NULL
);

CREATE TABLE movimentacao (
	id_movimentacao INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    tipo_movimentacao VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE relatorio (
	id_relatorio INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    tipo_movimentacao INT,
    FOREIGN KEY (tipo_movimentacao) REFERENCES movimentacao(id_movimentacao) ON DELETE CASCADE,
    data_inicial DATE NOT NULL,
    data_final DATE NOT NULL,
    data_relatorio DATETIME NOT NULL,
    quantidade_produtos INT NOT NULL CHECK (quantidade_produtos >= 0),
    descricao_produtos VARCHAR(255) NOT NULL DEFAULT 'Nao foram informados os produtos adquiridos/vendidos',
    CHECK(data_final >= data_inicial)
);

/* ==================================================================== */

/* Inserindo registros nas tabelas */

INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, perfil_usuario, data_cadastro, ultimo_acesso, usuario_ativo)
VALUES
('João Silva', 'joao.silva@gmail.com', 'senha123', 'Administrador', NOW(), NOW(), 1),
('Maria Souza', 'maria.souza@gmail.com', 'senha456', 'Vendedor', NOW(), NOW(), 1),
('Carlos Oliveira', 'carlos.oliveira@gmail.com', 'senha789', 'Gerente', NOW(), NOW(), 1),
('Ana Costa', 'ana.costa@gmail.com', 'senha321', 'Vendedor', NOW(), NOW(), 1),
('Paulo Almeida', 'paulo.almeida@gmail.com', 'senha654', 'Administrador', NOW(), NOW(), 1);

INSERT INTO permissoes (id_usuario, permissao_acesso, descricao_acesso, nivel_acesso, status)
VALUES
(1, 'Acesso Total', 'Acesso completo a todos os módulos', 'Admin', 'Ativo'),
(2, 'Acesso Restrito', 'Acesso apenas ao módulo de vendas', 'Vendedor', 'Ativo'),
(3, 'Acesso Completo', 'Acesso completo aos relatórios e ao estoque', 'Gerente', 'Ativo'),
(4, 'Acesso Restrito', 'Acesso apenas ao módulo de vendas', 'Vendedor', 'Ativo'),
(5, 'Acesso Total', 'Acesso completo a todos os módulos', 'Admin', 'Ativo');

INSERT INTO fornecedores (nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor, data_cadastro)
VALUES
('Fornecedor A', '12.345.678/0001-90', 123456789, 'fornecedorA@gmail.com', NOW()),
('Fornecedor B', '98.765.432/0001-12', 987654321, 'fornecedorB@gmail.com', NOW()),
('Fornecedor C', '23.456.789/0001-23', 234567890, 'fornecedorC@gmail.com', NOW()),
('Fornecedor D', '34.567.890/0001-34', 345678901, 'fornecedorD@gmail.com', NOW()),
('Fornecedor E', '45.678.901/0001-45', 456789012, 'fornecedorE@gmail.com', NOW());

INSERT INTO cadastro_de_produtos (id_usuario, id_fornecedores, nome_produto, codigo_produto, quantidade_estoque, valor_compra, valor_venda, validade_produto, data_cadastro)
VALUES
(1, 1, 'Produto X', '12345', 100, 50.00, 75.00, '2025-12-31', NOW()),
(2, 2, 'Produto Y', '67890', 200, 30.00, 50.00, '2025-06-30', NOW()),
(3, 3, 'Produto Z', '54321', 150, 40.00, 60.00, '2025-08-15', NOW()),
(4, 4, 'Produto W', '98765', 50, 25.00, 40.00, '2025-04-20', NOW()),
(5, 5, 'Produto V', '11223', 80, 60.00, 90.00, '2025-05-10', NOW());

INSERT INTO movimentacao (id_usuario, tipo_movimentacao)
VALUES
(1, 'entrada'),
(1, 'saida'),
(3, 'outros');

INSERT INTO relatorio (id_usuario, tipo_movimentacao, data_inicial, data_final, data_relatorio, quantidade_produtos, descricao_produtos)
VALUES
(1, 1, '2025-01-01', '2025-01-31', NOW(), 50, 'Produtos adquiridos em janeiro de 2025- 1xCaixa de salgados elma chips'),
(2, 2, '2025-02-01', '2025-02-28', NOW(), 30, 'Produtos vendidos em fevereiro de 2025- 2xCaixas de leite'),
(3, 1, '2025-03-01', '2025-03-31', NOW(), 20, 'Produtos adquiridos em março de 2025 5xEngradados de coca cola'),
(4, 2, '2025-04-01', '2025-04-30', NOW(), 40, 'Produtos vendidos em abril de 2025- 1xCaixa de leite'),
(5, 1, '2025-05-01', '2025-05-31', NOW(), 60, 'Produtos adquiridos em maio de 2025- 2xGarrafas de coca cola');

/* ==================================================================== */

/* Checagem dos valores inseridos nas tabelas utilizando o comando SELECT */

SELECT*FROM usuarios;
SELECT*FROM permissoes;
SELECT*FROM fornecedores;
SELECT*FROM cadastro_de_produtos;
SELECT*FROM movimentacao;
SELECT*FROM relatorio;

/* ==================================================================== */

/* Checagem registros especificos utilizando o comando WHERE */

SELECT * FROM usuarios WHERE nome_usuario = 'João Silva';
SELECT * FROM cadastro_de_produtos WHERE valor_compra > 50.00 AND quantidade_estoque > 10;

/* ==================================================================== */

/* Atualizando registros das tabelas atraves do comando UPDATE */

UPDATE usuarios SET perfil_usuario = 'Gerente' WHERE id_usuario = 1;
UPDATE permissoes SET 
	permissao_acesso = 'Acesso Completo', 
	descricao_acesso = 'Acesso completo aos relatórios e ao estoque',
	nivel_acesso = 'Gerente' 
WHERE id_permissoes = 2;
UPDATE fornecedores SET nome_fornecedor = 'Padaria Seu Paulo' WHERE id_fornecedores = 2;
UPDATE cadastro_de_produtos SET valor_venda = 50.00 WHERE id_produto = 3;
UPDATE movimentacao SET tipo_movimentacao = 'devoulucao' WHERE id_movimentacao = 3;
UPDATE relatorio SET quantidade_produtos = 100 WHERE id_relatorio = 2;

/* ==================================================================== */

/* Removendo registros das tabelas atraves do comando DELETE */

DELETE FROM usuarios WHERE id_usuario = 3;
DELETE FROM permissoes WHERE id_permissoes = 3;
DELETE FROM fornecedores WHERE id_fornecedores = 3;
DELETE FROM cadastro_de_produtos WHERE id_produto = 3;
DELETE FROM movimentacao WHERE id_movimentacao = 3;
DELETE FROM relatorio WHERE id_relatorio = 3;








