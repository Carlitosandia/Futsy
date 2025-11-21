/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.modelos.Teams;
import com.mycompany.piaweb.modelos.TeamsMundial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class TeamsMundialDAO {
    Connection conn;
    public TeamsMundialDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertTeamMundial(TeamsMundial teamMundial, List<Integer> playerIds) {
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        
        try {
            conn.setAutoCommit(false); 

            String sqlTeam = "INSERT INTO national_teams (owner_user_id, country_id, display_name, logo) VALUES (?,?,?,?)";
            System.out.println("Query to be executed:" + sqlTeam);
            ps = conn.prepareStatement(sqlTeam, java.sql.Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, teamMundial.getOwner_user_id());
            ps.setInt(2, teamMundial.getCountry_id());
            ps.setString(3, teamMundial.getDisplay_name());
            ps.setString(4, teamMundial.getLogo());

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

            String sqlPlayers = "INSERT INTO national_team_players (national_team_id, player_id) VALUES (?,?)";
            System.out.println("SQL to be executed:" + sqlPlayers);
            ps2 = conn.prepareStatement(sqlPlayers);


            for (Integer playerId : playerIds) {
                ps2.setLong(1, teamIdGenerado);
                ps2.setInt(2, playerId);
                ps2.addBatch();
            }

            ps2.executeBatch();

            conn.commit(); 
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); 
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
    
    public boolean updateTeamMundial(TeamsMundial team, List<Integer> playerIds) {
    try {
        conn.setAutoCommit(false);
        String sqlUpdate = "UPDATE national_teams SET display_name = ?, logo = ?, country_id = ? WHERE id = ? AND owner_user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setString(1, team.getDisplay_name());
            ps.setString(2, team.getLogo());
            ps.setInt(3, team.getCountry_id());
            ps.setInt(4, team.getId());
            ps.setInt(5, team.getOwner_user_id());
            ps.executeUpdate();
        }

        String sqlDelete = "DELETE FROM national_team_players WHERE national_team_id = ?";
        try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
            psDel.setInt(1, team.getId());
            psDel.executeUpdate();
        }

        String sqlInsert = "INSERT INTO national_team_players (national_team_id, player_id) VALUES (?,?)";
        try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
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
    
    public TeamsMundial getTeamMundialByUserId(int userId) {
        TeamsMundial team = null;
        String sql = "SELECT * FROM national_teams WHERE owner_user_id = ? LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    team = new TeamsMundial();
                    team.setId(rs.getInt("id"));
                    team.setDisplay_name(rs.getString("display_name"));
                    team.setOwner_user_id(rs.getInt("owner_user_id"));
                    team.setCountry_id(rs.getInt("country_id"));
                    team.setLogo(rs.getString("logo"));
                    
                    
                    team.setPlayerIds(getMundialTeamPlayerIds(team.getId()));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return team;
    }

    
    private List<Integer> getMundialTeamPlayerIds(int teamId) {
        List<Integer> ids = new java.util.ArrayList<>();
        String sql = "SELECT player_id FROM national_team_players WHERE national_team_id = ?";
        
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

}
