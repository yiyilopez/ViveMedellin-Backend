-- Script de creación de objetos para PostgreSQL
-- ViveMedellin Backend - Tablas principales
-- Basado en entidades JPA en com.vivemedellin.models

-- TABLA: users
-- Campos mapeados desde User.java
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  about TEXT
);

-- TABLA: categories
-- Campos mapeados desde Category.java
CREATE TABLE IF NOT EXISTS categories (
  category_id SERIAL PRIMARY KEY,
  category_title VARCHAR(100) NOT NULL,
  category_description TEXT
);

-- TABLA: post
-- Campos mapeados desde Post.java
CREATE TABLE IF NOT EXISTS post (
  post_id SERIAL PRIMARY KEY,
  post_title VARCHAR(100) NOT NULL,
  content TEXT,
  image_name VARCHAR(255),
  creation_date DATE,
  category_id INTEGER REFERENCES categories(category_id) ON DELETE SET NULL,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL
);

-- TABLA: comments
-- Campos mapeados desde Comment.java
CREATE TABLE IF NOT EXISTS comments (
  id SERIAL PRIMARY KEY,
  content TEXT,
  post_id INTEGER NOT NULL REFERENCES post(post_id) ON DELETE CASCADE,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL
);

-- TABLA: user_roles
-- Generada automáticamente por @ElementCollection en User.java
-- Mapea roles ENUM (ROLE_USER, ROLE_ADMIN) a usuarios
CREATE TABLE IF NOT EXISTS user_roles (
  user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role)
);

-- ÍNDICES
CREATE INDEX IF NOT EXISTS idx_post_category ON post(category_id);
CREATE INDEX IF NOT EXISTS idx_post_user ON post(user_id);
CREATE INDEX IF NOT EXISTS idx_comments_post ON comments(post_id);
CREATE INDEX IF NOT EXISTS idx_comments_user ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles ON user_roles(user_id);

