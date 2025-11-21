/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.modelos.Teams;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Carlo
 */
public class TeamsDAO {

    Connection conn;

    public TeamsDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertTeamGeneral(Teams team, List<Integer> playerIds) {
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        
        try {
            conn.setAutoCommit(false); 

            String sqlTeam = "INSERT INTO teams (name, owner_user_id, type) VALUES (?,?,?)";
            ps = conn.prepareStatement(sqlTeam, java.sql.Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, team.getName());
            ps.setInt(2, team.getOwner_user_id());
            ps.setString(3, team.getType());

            int insert = ps.executeUpdate();

            if (insert == 0) {
                conn.rollback();
                return false;
            }

            long teamIdGenerado = 0;
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                teamIdGenerado = rs.getLong(1); 
                System.out.println("ID del equipo creado: " + teamIdGenerado);
            } else {
                throw new SQLException("Error al crear el equipo, no se obtuvo el ID.");
            }

            String sqlPlayers = "INSERT INTO team_players (team_id, player_id, joined_at) VALUES (?,?,?)";
            ps2 = conn.prepareStatement(sqlPlayers);

            java.sql.Timestamp fechaActual = new java.sql.Timestamp(System.currentTimeMillis());

            for (Integer playerId : playerIds) {
                ps2.setLong(1, teamIdGenerado);
                ps2.setInt(2, playerId);
                ps2.setTimestamp(3, fechaActual);
                ps2.addBatch();
            }

            ps2.executeBatch();

            conn.commit(); 
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Si hay error, deshacemos cambios
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); 
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    public Teams getTeamByUserId(int userId) {
        Teams team = null;
        String sql = "SELECT * FROM teams WHERE owner_user_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    team = new Teams();
                    team.setId(rs.getInt("id"));
                    team.setName(rs.getString("name"));
                    team.setLogo(rs.getString("logo"));
                    team.setType(rs.getString("type"));
                    // Importante: Necesitamos los IDs de los jugadores que ya están en el equipo
                    team.setPlayerIds(getTeamPlayerIds(team.getId()));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return team;
    }
    
    private List<Integer> getTeamPlayerIds(int teamId) {
    List<Integer> ids = new java.util.ArrayList<>();
    // Consultamos tu tabla intermedia
    String sql = "SELECT player_id FROM team_players WHERE team_id = ?";
    
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, teamId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("player_id"));
            }
        }
    } catch (Exception e) { e.printStackTrace(); }
    return ids;
}


    // Método para ACTUALIZAR
    public boolean updateTeamGeneral(Teams team, List<Integer> playerIds) {
        try {
            conn.setAutoCommit(false);

            // 1. Actualizar datos básicos del equipo
            String sqlUpdate = "UPDATE teams SET name = ?, logo = ? WHERE id = ? AND owner_user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, team.getName());
                ps.setString(2, team.getLogo()); // Nota: lógica en servlet para no borrar logo si no suben uno nuevo
                ps.setInt(3, team.getId());
                ps.setInt(4, team.getOwner_user_id());
                ps.executeUpdate();
            }

            // 2. Actualizar Jugadores: Borrar viejos -> Insertar nuevos
            String sqlDeletePlayers = "DELETE FROM team_players WHERE team_id = ?";
            try (PreparedStatement psDel = conn.prepareStatement(sqlDeletePlayers)) {
                psDel.setInt(1, team.getId());
                psDel.executeUpdate();
            }

            String sqlInsertPlayers = "INSERT INTO team_players (team_id, player_id, joined_at) VALUES (?,?, NOW())";
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertPlayers)) {
                for (Integer pid : playerIds) {
                    psIns.setInt(1, team.getId());
                    psIns.setInt(2, pid);
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ex) {}
        }
    }
}
