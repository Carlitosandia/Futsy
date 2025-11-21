/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import com.mycompany.piaweb.modelos.Tags;
import com.mycompany.piaweb.dao.TagsDAO;

/**
 *
 * @author Ravellion
 */
public class PostsDAO {

    private Connection conn;

    public PostsDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Post> getPostsByTagSlug(String tagSlug) throws SQLException {
        List<Post> listaPosts = new ArrayList<>();
        String sql
                = "SELECT p.id, p.title, p.description, p.author_user_id, "
                + "u.name, u.username, u.image_url "
                + "FROM posts p "
                + "INNER JOIN users u ON p.author_user_id = u.id "
                + "INNER JOIN post_tags pt ON p.id = pt.post_id "
                + "INNER JOIN tags t ON pt.tag_id = t.id "
                + "WHERE t.slug = ? AND p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tagSlug);
            ResultSet rs = ps.executeQuery();

            TagsDAO tagsDAO = new TagsDAO(conn);

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setAuthorUserId(rs.getInt("author_user_id"));
                post.setAuthorName(rs.getString("name"));
                post.setAuthorUsername(rs.getString("username"));
                post.setAuthorImage(rs.getString("image_url"));

                // Asume que también necesitas cargar la imagen y los tags para la vista
                post.setImage(getPostImage(post.getId()));
                post.setTags(tagsDAO.getByPostId(post.getId()));

                listaPosts.add(post);
            }
        }
        return listaPosts;
    }

    public List<Post> getPostsByTagTitle(String tagTitle) throws SQLException {
        List<Post> listaPosts = new ArrayList<>();

        // Consulta SQL que usa INNER JOIN para filtrar posts que tienen un tag específico
        String sql
                = "SELECT p.id, p.title, p.description, p.author_user_id, "
                + "u.name, u.username, u.image_url "
                + "FROM posts p "
                + "INNER JOIN users u ON p.author_user_id = u.id "
                + "INNER JOIN post_tags pt ON p.id = pt.post_id "
                + "INNER JOIN tags t ON pt.tag_id = t.id "
                + "WHERE t.title = ? AND p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tagTitle);
            ResultSet rs = ps.executeQuery();

            TagsDAO tagsDAO = new TagsDAO(conn);

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setAuthorUserId(rs.getInt("author_user_id"));
                post.setAuthorName(rs.getString("name"));
                post.setAuthorUsername(rs.getString("username"));
                post.setAuthorImage(rs.getString("image_url"));
                post.setImage(getPostImage(post.getId()));
                post.setTags(tagsDAO.getByPostId(post.getId()));

                listaPosts.add(post);
            }
        }
        return listaPosts;
    }

    public int insertPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts(title, description, author_user_id, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setInt(3, post.getAuthorUserId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void insertPostImage(PostImage img) throws SQLException {
        String sql = "INSERT INTO post_images(post_id, path, sort_order) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, img.getPostId());
            ps.setString(2, img.getPath());
            ps.setInt(3, img.getSortOrder());
            ps.executeUpdate();
        }
    }

    public void insertPostTags(int postId, List<Integer> tagIds) throws SQLException {
        String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer tagId : tagIds) {
                ps.setInt(1, postId);   // si tus ids en Java son int
                ps.setInt(2, tagId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void updatePost(Post post) throws SQLException {
        String sql = "UPDATE posts SET title = ?, description = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setInt(3, post.getId());
            ps.executeUpdate();
        }

        // Actualizar imagen si existe
        if (post.getImage() != null) {
            // Primero verificar si ya existe imagen para el post
            String checkSql = "SELECT COUNT(*) FROM post_images WHERE post_id = ? AND sort_order = 0";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, post.getId());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Actualizar
                    String updateImgSql = "UPDATE post_images SET path = ? WHERE post_id = ? AND sort_order = 0";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateImgSql)) {
                        psUpdate.setString(1, post.getImage().getPath());
                        psUpdate.setInt(2, post.getId());
                        psUpdate.executeUpdate();
                    }
                } else {
                    // Insertar nueva imagen
                    insertPostImage(post.getImage());
                }
            }
        }
    }

    public void deletePost(int postId) throws SQLException {
        String sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }

    public List<Post> getPostsByUser(int userId, int offset, int limit) {
        List<Post> listaPosts = new ArrayList<>();
        String sql
                = "SELECT p.id, p.title, p.description, p.author_user_id, "
                + "u.name, u.username, u.image_url "
                + "FROM posts p "
                + "INNER JOIN users u ON p.author_user_id = u.id "
                + "WHERE p.author_user_id = ? AND p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC "
                + "LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();

            TagsDAO tagsDAO = new TagsDAO(conn);

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setAuthorUserId(rs.getInt("author_user_id"));
                post.setAuthorName(rs.getString("name"));
                post.setAuthorUsername(rs.getString("username"));
                post.setAuthorImage(rs.getString("image_url"));

                post.setTags(tagsDAO.getByPostId(post.getId()));

                listaPosts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaPosts;
    }

    public int countPostsByUser(int userId) {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM posts WHERE author_user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<Post> getAllPosts() {
        List<Post> lista = new ArrayList<>();

        String sql
                = "SELECT p.id, p.title, p.description, p.author_user_id, "
                + "u.name, u.username, u.image_url "
                + "FROM posts p "
                + "INNER JOIN users u ON p.author_user_id = u.id "
                + "WHERE p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            TagsDAO tagsDAO = new TagsDAO(conn);

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setAuthorUserId(rs.getInt("author_user_id"));

                // datos del autor
                post.setAuthorName(rs.getString("name"));
                post.setAuthorUsername(rs.getString("username"));
                post.setAuthorImage(rs.getString("image_url"));

                post.setTags(tagsDAO.getByPostId(post.getId()));

                lista.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public PostImage getPostImage(int postId) {
        String sql = "SELECT id, post_id, path, sort_order FROM post_images WHERE post_id = ? ORDER BY sort_order ASC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PostImage img = new PostImage();
                    img.setId(rs.getInt("id"));
                    img.setPostId(rs.getInt("post_id"));
                    img.setPath(rs.getString("path"));
                    img.setSortOrder(rs.getInt("sort_order"));
                    return img;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
