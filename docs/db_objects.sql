-- Script de creación de objetos para PostgreSQL (refinado para las HU implementadas)
-- NOTA: Ajustar nombres de columnas si el namingStrategy de Hibernate difiere.

-- 1) Tablas principales
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  about TEXT,
  post_count INTEGER DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS categories (
  category_id SERIAL PRIMARY KEY,
  category_title VARCHAR(100) NOT NULL,
  category_description TEXT,
  post_count INTEGER DEFAULT 0
);

-- Tabla 'post' (nombre usado en la entidad)
CREATE TABLE IF NOT EXISTS post (
  post_id SERIAL PRIMARY KEY,
  post_title VARCHAR(100) NOT NULL,
  content TEXT,
  image_name VARCHAR(255),
  creation_date TIMESTAMP WITH TIME ZONE DEFAULT now(),
  category_id INTEGER REFERENCES categories(category_id) ON DELETE SET NULL,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
  comment_count INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS comments (
  id SERIAL PRIMARY KEY,
  content TEXT,
  post_id INTEGER REFERENCES post(post_id) ON DELETE CASCADE,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Roles (se implementa como tabla auxiliar porque JPA usa ElementCollection de Enum)
CREATE TABLE IF NOT EXISTS user_roles (
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role)
);

-- Índices recomendados
CREATE INDEX IF NOT EXISTS idx_post_category ON post(category_id);
CREATE INDEX IF NOT EXISTS idx_post_user ON post(user_id);
CREATE INDEX IF NOT EXISTS idx_post_title ON post USING gin (to_tsvector('spanish', post_title));
CREATE INDEX IF NOT EXISTS idx_comments_post ON comments(post_id);


-- 2) Tablas de auditoría sencillas
CREATE TABLE IF NOT EXISTS posts_audit (
  audit_id SERIAL PRIMARY KEY,
  post_id INTEGER,
  operation CHAR(1), -- I=insert, U=update, D=delete
  operation_ts TIMESTAMP WITH TIME ZONE DEFAULT now(),
  payload JSONB
);

CREATE TABLE IF NOT EXISTS comments_audit (
  audit_id SERIAL PRIMARY KEY,
  comment_id INTEGER,
  operation CHAR(1),
  operation_ts TIMESTAMP WITH TIME ZONE DEFAULT now(),
  payload JSONB
);


-- 3) Triggers y funciones para mantener contadores y auditoría

-- POSTS: after insert -> incrementar post_count en categories y users
CREATE OR REPLACE FUNCTION fn_post_after_insert() RETURNS trigger AS $$
BEGIN
  IF NEW.category_id IS NOT NULL THEN
    UPDATE categories SET post_count = COALESCE(post_count,0) + 1 WHERE category_id = NEW.category_id;
  END IF;
  IF NEW.user_id IS NOT NULL THEN
    UPDATE users SET post_count = COALESCE(post_count,0) + 1 WHERE id = NEW.user_id;
  END IF;
  -- Auditoría
  INSERT INTO posts_audit(post_id, operation, payload) VALUES (NEW.post_id, 'I', to_jsonb(NEW));
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_post_after_insert
AFTER INSERT ON post
FOR EACH ROW EXECUTE FUNCTION fn_post_after_insert();


-- POSTS: after delete -> decrementar contadores
CREATE OR REPLACE FUNCTION fn_post_after_delete() RETURNS trigger AS $$
BEGIN
  IF OLD.category_id IS NOT NULL THEN
    UPDATE categories SET post_count = GREATEST(COALESCE(post_count,0) - 1, 0) WHERE category_id = OLD.category_id;
  END IF;
  IF OLD.user_id IS NOT NULL THEN
    UPDATE users SET post_count = GREATEST(COALESCE(post_count,0) - 1, 0) WHERE id = OLD.user_id;
  END IF;
  -- Auditoría
  INSERT INTO posts_audit(post_id, operation, payload) VALUES (OLD.post_id, 'D', to_jsonb(OLD));
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_post_after_delete
AFTER DELETE ON post
FOR EACH ROW EXECUTE FUNCTION fn_post_after_delete();


-- POSTS: after update -> ajustar contadores si cambió category_id o user_id
CREATE OR REPLACE FUNCTION fn_post_after_update() RETURNS trigger AS $$
BEGIN
  -- ajustar category counts
  IF (OLD.category_id IS DISTINCT FROM NEW.category_id) THEN
    IF OLD.category_id IS NOT NULL THEN
      UPDATE categories SET post_count = GREATEST(COALESCE(post_count,0) - 1, 0) WHERE category_id = OLD.category_id;
    END IF;
    IF NEW.category_id IS NOT NULL THEN
      UPDATE categories SET post_count = COALESCE(post_count,0) + 1 WHERE category_id = NEW.category_id;
    END IF;
  END IF;
  -- ajustar user counts
  IF (OLD.user_id IS DISTINCT FROM NEW.user_id) THEN
    IF OLD.user_id IS NOT NULL THEN
      UPDATE users SET post_count = GREATEST(COALESCE(post_count,0) - 1, 0) WHERE id = OLD.user_id;
    END IF;
    IF NEW.user_id IS NOT NULL THEN
      UPDATE users SET post_count = COALESCE(post_count,0) + 1 WHERE id = NEW.user_id;
    END IF;
  END IF;
  -- Auditoría de update
  INSERT INTO posts_audit(post_id, operation, payload) VALUES (NEW.post_id, 'U', jsonb_build_object('old', to_jsonb(OLD), 'new', to_jsonb(NEW)));
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_post_after_update
AFTER UPDATE ON post
FOR EACH ROW EXECUTE FUNCTION fn_post_after_update();


-- COMMENTS: after insert -> incrementar comment_count en post
CREATE OR REPLACE FUNCTION fn_comment_after_insert() RETURNS trigger AS $$
BEGIN
  IF NEW.post_id IS NOT NULL THEN
    UPDATE post SET comment_count = COALESCE(comment_count,0) + 1 WHERE post_id = NEW.post_id;
  END IF;
  INSERT INTO comments_audit(comment_id, operation, payload) VALUES (NEW.id, 'I', to_jsonb(NEW));
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_comment_after_insert
AFTER INSERT ON comments
FOR EACH ROW EXECUTE FUNCTION fn_comment_after_insert();


-- COMMENTS: after delete -> decrementar comment_count
CREATE OR REPLACE FUNCTION fn_comment_after_delete() RETURNS trigger AS $$
BEGIN
  IF OLD.post_id IS NOT NULL THEN
    UPDATE post SET comment_count = GREATEST(COALESCE(comment_count,0) - 1, 0) WHERE post_id = OLD.post_id;
  END IF;
  INSERT INTO comments_audit(comment_id, operation, payload) VALUES (OLD.id, 'D', to_jsonb(OLD));
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_comment_after_delete
AFTER DELETE ON comments
FOR EACH ROW EXECUTE FUNCTION fn_comment_after_delete();


-- PROCEDIMIENTO DE UTILIDAD: reasignar posts de una categoría a otra (por ejemplo, al eliminar una categoría)
CREATE OR REPLACE PROCEDURE sp_reassign_posts(old_category_id INTEGER, new_category_id INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE post SET category_id = new_category_id WHERE category_id = old_category_id;
  -- actualizar contadores (simple recálculo)
  UPDATE categories SET post_count = (
    SELECT COUNT(*) FROM post WHERE category_id = categories.category_id
  );
END;
$$;

-- Fin del script
