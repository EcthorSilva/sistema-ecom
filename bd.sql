CREATE DATABASE backoffice_db;
USE backoffice_db;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    grupo ENUM('Administrador', 'Estoquista') NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

INSERT INTO usuarios (nome, cpf, email, senha, grupo, status) 
VALUES 
('Administrador', '111.222.333-44', 'admin@example.com', 'senha_encriptada_aqui', 'Administrador', TRUE),
('Estoquista', '222.333.444-55', 'estoquista@example.com', 'senha_encriptada_aqui', 'Estoquista', TRUE);
