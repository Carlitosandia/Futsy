/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;
import com.mycompany.piaweb.modelos.Tags;
import com.mycompany.piaweb.modelos.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sofia
 */
public class TagsDAO {
     Connection conn;

    //Por medio del constructor puede acceder a la conexion establecida en la clase Conexion.
    public TagsDAO(Connection conn) {
        this.conn = conn;
    }
    
   public List<Tags> getByPostId(int postId) {
    List<Tags> lista = new ArrayList<>();

    String sql = "SELECT t.id, t.slug, t.title, t.league_featured, t.worldcup_featured " +
                 "FROM tags t " +
                 "INNER JOIN post_tags pt ON t.id = pt.tag_id " +
                 "WHERE pt.post_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, postId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tags tag = new Tags();
                tag.setId(rs.getInt("id"));
                tag.setSlug(rs.getString("slug"));
                tag.setTitle(rs.getString("title"));
                tag.setLeague_featured(rs.getBoolean("league_featured"));
                tag.setWorldcup_featured(rs.getBoolean("worldcup_featured"));
                lista.add(tag);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}    

    public List<Tags> getAll() {
    List<Tags> listaTags = new ArrayList<>();

    String sql = "SELECT id, slug, title, league_featured, worldcup_featured FROM tags";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Tags tag = new Tags();

            tag.setId(rs.getInt("id"));
            tag.setSlug(rs.getString("slug"));
            tag.setTitle(rs.getString("title"));
            tag.setLeague_featured(rs.getBoolean("league_featured"));
            tag.setWorldcup_featured(rs.getBoolean("worldcup_featured"));

            listaTags.add(tag);
        }

    } catch (SQLException e) {
        e.printStackTrace(); // para ver el error en consola si algo falla
    }

    return listaTags;
}
    
    
    
    
}
