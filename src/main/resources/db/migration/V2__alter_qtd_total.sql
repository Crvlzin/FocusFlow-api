-- FocusFlow Database Migration V2: Permitir insercao de qtd_total (questoes em branco)
ALTER TABLE estatisticas ALTER COLUMN qtd_total DROP EXPRESSION IF EXISTS;
ALTER TABLE estatisticas ALTER COLUMN qtd_total SET DEFAULT 0;
