-- FocusFlow Database Migration V1: Criação da Estrutura Inicial do Banco
-- Flyway gerencia a ordem e aplicação deste script

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabela usuarios
-- Em um backend próprio, nós gerenciamos o usuário, senha hasheada com BCrypt e suas roles de acesso
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nm_usuario VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    dt_criacao TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Tabela materias
CREATE TABLE IF NOT EXISTS materias (
    id_materia UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    nm_materia VARCHAR(150) NOT NULL
);

-- 3. Tabela assuntos
CREATE TABLE IF NOT EXISTS assuntos (
    id_assunto UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_materia UUID NOT NULL REFERENCES materias(id_materia) ON DELETE CASCADE,
    nm_assunto VARCHAR(150) NOT NULL
);

-- 4. Tabela estatisticas
CREATE TABLE IF NOT EXISTS estatisticas (
    id_estatistica UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_assunto UUID NOT NULL REFERENCES assuntos(id_assunto) ON DELETE CASCADE,
    qtd_certas INT NOT NULL DEFAULT 0,
    qtd_erradas INT NOT NULL DEFAULT 0,
    qtd_minutos INT NOT NULL DEFAULT 0,
    qtd_total INT GENERATED ALWAYS AS (qtd_certas + qtd_erradas) STORED,
    dt_registro TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. Tabela revisoes (Repetição Espaçada)
CREATE TABLE IF NOT EXISTS revisoes (
    id_revisao UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_assunto UUID NOT NULL REFERENCES assuntos(id_assunto) ON DELETE CASCADE,
    dt_revisao DATE NOT NULL,
    nivel_ciclo INT NOT NULL CHECK (nivel_ciclo BETWEEN 1 AND 4),
    fl_concluida BOOLEAN DEFAULT false NOT NULL,
    dt_conclusao DATE
);

-- 6. Tabela cronograma (Cronograma Semanal de Estudos)
CREATE TABLE IF NOT EXISTS cronograma (
    id_cronograma UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    dia_semana INT NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),
    id_materia UUID REFERENCES materias(id_materia) ON DELETE SET NULL,
    titulo_estudo VARCHAR(255) NOT NULL,
    horario_inicio TIME,
    horario_fim TIME,
    observacao TEXT,
    fl_concluido BOOLEAN DEFAULT false NOT NULL,
    ordem INT DEFAULT 0 NOT NULL,
    dt_criacao TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Índices para otimização de busca frequente
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_materias_usuario ON materias(id_usuario);
CREATE INDEX IF NOT EXISTS idx_assuntos_materia ON assuntos(id_materia);
CREATE INDEX IF NOT EXISTS idx_estatisticas_usuario ON estatisticas(id_usuario);
CREATE INDEX IF NOT EXISTS idx_revisoes_usuario ON revisoes(id_usuario);
CREATE INDEX IF NOT EXISTS idx_cronograma_usuario ON cronograma(id_usuario);
