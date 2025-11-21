package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.modelos.Post;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchBarDAO {

    Connection conn;

    public SearchBarDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Post> singleSearch(String titleSearch) {
        List<Post> posts = new ArrayList<>();

        // CAMBIO CLAVE EN EL SQL:
        // Usamos LOWER(p.title) y LOWER(?) para igualar todo a minúsculas antes de comparar.
        String sql = "SELECT p.*, u.name, u.lastname, u.username, u.image_url " +
                     "FROM posts p " +
                     "JOIN users u ON p.author_user_id = u.id " +
                     "WHERE LOWER(p.title) LIKE LOWER(?) " + 
                     "AND p.deleted_at IS NULL " +
                     "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // CAMBIO CLAVE EN JAVA:
            // Agregamos los % a izquierda y derecha. 
            // El SQL se encarga de las mayúsculas/minúsculas con LOWER, 
            // pero los % son necesarios para que busque coincidencias parciales.
            ps.setString(1, "%" + titleSearch + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setAuthorUserId(rs.getInt("author_user_id"));
                    post.setTitle(rs.getString("title"));
                    post.setDescription(rs.getString("description"));
                    
                    // Datos del autor
                    post.setAuthorName(rs.getString("name") + " " + rs.getString("lastname"));
                    post.setAuthorUsername(rs.getString("username"));
                    post.setAuthorImage(rs.getString("image_url"));
                    
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    public List<Post> advancedSearch(String text, String tagIdStr, String dateFrom, String dateTo) {
        List<Post> posts = new ArrayList<>();
        
        // 1. Construcción dinámica de la Query
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT p.*, u.name, u.lastname, u.username ");
        sql.append("FROM posts p ");
        sql.append("JOIN users u ON p.author_user_id = u.id ");
        
        // Si seleccionó un tag, necesitamos hacer JOIN con post_tags
        if (tagIdStr != null && !tagIdStr.isEmpty()) {
            sql.append("JOIN post_tags pt ON p.id = pt.post_id ");
        }
        
        sql.append("WHERE p.deleted_at IS NULL ");
        
        // Lista para guardar los valores de los ? en orden
        List<Object> params = new ArrayList<>();

        // Filtro Texto (Título O Descripción)
        if (text != null && !text.trim().isEmpty()) {
            sql.append("AND (LOWER(p.title) LIKE LOWER(?) OR LOWER(p.description) LIKE LOWER(?)) ");
            params.add("%" + text.trim() + "%");
            params.add("%" + text.trim() + "%");
        }

        // Filtro Tag
        if (tagIdStr != null && !tagIdStr.isEmpty()) {
            sql.append("AND pt.tag_id = ? ");
            params.add(Integer.parseInt(tagIdStr));
        }

        // Filtro Fecha Desde
        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append("AND p.created_at >= ? ");
            params.add(dateFrom + " 00:00:00"); // Formato YYYY-MM-DD HH:mm:ss
        }

        // Filtro Fecha Hasta
        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append("AND p.created_at <= ? ");
            params.add(dateTo + " 23:59:59");
        }

        sql.append("ORDER BY p.created_at DESC");

        // 2. Ejecución
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Asignar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setAuthorUserId(rs.getInt("author_user_id"));
                    post.setTitle(rs.getString("title"));
                    post.setDescription(rs.getString("description"));
                    post.setAuthorName(rs.getString("name") + " " + rs.getString("lastname"));
                    post.setAuthorUsername(rs.getString("username"));
                    
                    // Nota: La imagen se carga en el Servlet, igual que en singleSearch
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return posts;
    }
}