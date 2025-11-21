package com.mycompany.piaweb.dao;


import com.mycompany.piaweb.modelos.Players;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 */
public class PlayersDAO {

    Connection conn;

    public PlayersDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Players> getAllPlayers() throws SQLException {
        List<Players> players = new ArrayList<>();
        String sql = "SELECT p.*, c.name as nombre_pais FROM players p JOIN countries c ON p.nationality_id = c.id ORDER BY p.overall DESC";
        System.out.println("SQL to be executed: " + sql);
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Instanciamos con los datos base
                Players p = new Players(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("position"),
                        rs.getInt("nationality_id"),
                        rs.getInt("overall"),
                        rs.getDate("created_at"),
                        rs.getDate("updated_at") 
                );
                p.setCountryName(rs.getString("nombre_pais"));

                players.add(p);
            }
        }
        return players;
    }
}
