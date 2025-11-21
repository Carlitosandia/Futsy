/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.enums.MatchPhase;
import com.mycompany.piaweb.enums.TournamentMode;
import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.modelos.Tournament;
import com.mycompany.piaweb.modelos.TournamentParticipant;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlo
 */
public class TournamentDao {

    private final Conexion conexion;

    public TournamentDao() {
        this.conexion = new Conexion();
    }

    private Connection getConnection() throws SQLException {
        try {
            return conexion.Conectar();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver de MySQL", e);
        }
    }

    // ============================================================
    // 1. Crear torneo
    // ============================================================
    public Long createTournament(Tournament tournament) {
        String sql = "INSERT INTO tournaments "
                + "(name, mode, created_by_user_id, is_simulation_allowed) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tournament.getName());
            ps.setString(2, toDbMode(tournament.getMode()));
            if (tournament.getCreated_by_user_id() != null) {
                ps.setLong(3, tournament.getCreated_by_user_id());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setBoolean(4, tournament.isSimulationAllowed());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    tournament.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tournament", e);
        }

        return null;
    }

    private String toDbMode(TournamentMode mode) {
        switch (mode) {
            case GENERAL:
                return "general";
            case WORLD_CUP:
                return "worldcup";
            case HEAD_TO_HEAD:
                return "head_to_head";
            case SIMULATED:
                return "simulated";
            default:
                return "league";
        }
    }

    // ============================================================
    // 2. Insertar participantes (torneo individual general)
    //    Aquí: sólo el equipo del usuario + (opcional) otros equipos
    // ============================================================
    public List<TournamentParticipant> insertParticipantsForGeneral(Long tournamentId, Long ownerUserId) {
        List<TournamentParticipant> list = new ArrayList<>();

        String sqlSelectTeam
                = "SELECT id, name, logo "
                + "FROM teams "
                + "WHERE owner_user_id = ? AND type = 'general' "
                + "LIMIT 1";

        String sqlInsertParticipant
                = "INSERT INTO tournament_participants "
                + "(tournament_id, kind, team_id, national_team_id) "
                + "VALUES (?, 'club', ?, NULL)";

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            Long teamId = null;
            String teamName = null;
            String teamLogo = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlSelectTeam)) {
                ps.setLong(1, ownerUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        teamId = rs.getLong("id");
                        teamName = rs.getString("name");
                        teamLogo = rs.getString("logo");
                    } else {
                        throw new RuntimeException("User has no general team");
                    }
                }
            }

            Long participantId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertParticipant, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, tournamentId);
                ps.setLong(2, teamId);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        participantId = rs.getLong(1);
                    }
                }
            }

            TournamentParticipant p = new TournamentParticipant();
            p.setId(participantId);
            p.setTeamId(teamId);
            p.setNationalTeamId(null);
            p.setName(teamName);
            p.setLogo(teamLogo);
            p.setIsNational(false);
            list.add(p);

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting general participants", e);
        }

        return list;
    }

    // ============================================================
    // 3. Insertar participantes para torneo general global
    //    (elige N equipos al azar)
    // ============================================================
    public List<TournamentParticipant> insertParticipantsForGlobalGeneral(Long tournamentId, int limit) {
        List<TournamentParticipant> list = new ArrayList<>();

        String sqlSelectTeams
                = "SELECT id, name, logo "
                + "FROM teams "
                + "WHERE type = 'general' "
                + "ORDER BY RAND() "
                + "LIMIT ?";

        String sqlInsertParticipant
                = "INSERT INTO tournament_participants "
                + "(tournament_id, kind, team_id, national_team_id) "
                + "VALUES (?, 'club', ?, NULL)";

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            List<Long> teamIds = new ArrayList<>();
            List<String> teamNames = new ArrayList<>();
            List<String> teamLogos = new ArrayList<>();

            // 1) seleccionar equipos
            try (PreparedStatement ps = conn.prepareStatement(sqlSelectTeams)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        teamIds.add(rs.getLong("id"));
                        teamNames.add(rs.getString("name"));
                        teamLogos.add(rs.getString("logo"));
                    }
                }
            }

            // 2) insertarlos como participantes
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertParticipant, Statement.RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < teamIds.size(); i++) {
                    ps.setLong(1, tournamentId);
                    ps.setLong(2, teamIds.get(i));
                    ps.executeUpdate();

                    Long participantId = null;
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            participantId = rs.getLong(1);
                        }
                    }

                    TournamentParticipant p = new TournamentParticipant();
                    p.setId(participantId);
                    p.setTeamId(teamIds.get(i));
                    p.setNationalTeamId(null);
                    p.setName(teamNames.get(i));
                    p.setLogo(teamLogos.get(i));
                    p.setIsNational(false);
                    list.add(p);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting global general participants", e);
        }

        return list;
    }

    // ============================================================
    // 4. Insertar participantes para torneo tipo mundial
    //    (elige N selecciones al azar)
    // ============================================================
    public List<TournamentParticipant> insertParticipantsForWorldCup(Long tournamentId, int limit) {
        List<TournamentParticipant> list = new ArrayList<>();

        String sqlSelectNt
                = "SELECT id, display_name, logo "
                + "FROM national_teams "
                + "ORDER BY RAND() "
                + "LIMIT ?";

        String sqlInsertParticipant
                = "INSERT INTO tournament_participants "
                + "(tournament_id, kind, team_id, national_team_id) "
                + "VALUES (?, 'national', NULL, ?)";

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            List<Long> ntIds = new ArrayList<>();
            List<String> ntNames = new ArrayList<>();
            List<String> ntLogos = new ArrayList<>();

            // 1) seleccionar selecciones
            try (PreparedStatement ps = conn.prepareStatement(sqlSelectNt)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ntIds.add(rs.getLong("id"));
                        ntNames.add(rs.getString("display_name"));
                        ntLogos.add(rs.getString("logo"));
                    }
                }
            }

            // 2) insertarlas como participantes
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertParticipant, Statement.RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < ntIds.size(); i++) {
                    ps.setLong(1, tournamentId);
                    ps.setLong(2, ntIds.get(i));
                    ps.executeUpdate();

                    Long participantId = null;
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            participantId = rs.getLong(1);
                        }
                    }

                    TournamentParticipant p = new TournamentParticipant();
                    p.setId(participantId);
                    p.setTeamId(null);
                    p.setNationalTeamId(ntIds.get(i));
                    p.setName(ntNames.get(i));
                    p.setLogo(ntLogos.get(i));
                    p.setIsNational(true);
                    list.add(p);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting world cup participants", e);
        }

        return list;
    }

    // ============================================================
    // 5. Calcular overall de un equipo
    // ============================================================
    public int calculateOverallForTeam(Long teamId) {
        String sql
                = "SELECT AVG(p.overall) AS rating "
                + "FROM team_players tp "
                + "JOIN players p ON p.id = tp.player_id "
                + "WHERE tp.team_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating team overall", e);
        }

        return 0;
    }

    // ============================================================
    // 6. Calcular overall de una selección
    // ============================================================
    public int calculateOverallForNationalTeam(Long nationalTeamId) {
        String sql
                = "SELECT AVG(p.overall) AS rating "
                + "FROM national_team_players ntp "
                + "JOIN players p ON p.id = ntp.player_id "
                + "WHERE ntp.national_team_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, nationalTeamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating national team overall", e);
        }

        return 0;
    }

    // ============================================================
    // 7. Insertar partidos
    // ============================================================
    public void insertMatches(List<Match> matches) {
        String sql
                = "INSERT INTO matches "
                + "(tournament_id, home_participant_id, away_participant_id, "
                + " phase, round_number, home_score, away_score, winner_participant_id, played_at, is_simulated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Match m : matches) {
                ps.setLong(1, m.getTournamentId());
                ps.setLong(2, m.getHome().getId());
                ps.setLong(3, m.getAway().getId());
                ps.setString(4, toDbPhase(m.getPhase()));
                if (m.getRoundNumber() != null) {
                    ps.setInt(5, m.getRoundNumber());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setInt(6, m.getHomeScore());
                ps.setInt(7, m.getAwayScore());
                if (m.getWinner() != null) {
                    ps.setLong(8, m.getWinner().getId());
                } else {
                    ps.setNull(8, Types.BIGINT);
                }
                ps.setBoolean(9, m.isSimulated());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setId(rs.getLong(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting matches", e);
        }
    }

    private String toDbPhase(MatchPhase phase) {
        switch (phase) {
            case GROUP:
                return "group";
            case QUARTERFINAL:
                return "quarterfinal";
            case SEMIFINAL:
                return "semifinal";
            case FINAL:
                return "final";
            case LEAGUE_ROUND:
                return "league_round";
            default:
                return "group";
        }
    }

    // ============================================================
    // 8. Actualizar campeón
    // ============================================================
    public void updateChampion(Long tournamentId, Long winnerParticipantId) {
        String sql = "UPDATE tournaments "
                + "SET winner_participant_id = ? "
                + "WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, winnerParticipantId);
            ps.setLong(2, tournamentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating champion", e);
        }
    }

    // ============================================================
    // 9. Obtener partidos de un torneo
    //    (para pintar el bracket en JSP)
    // ============================================================
    public List<Match> findMatchesByTournament(Long tournamentId) {
        List<Match> list = new ArrayList<>();

        String sql
                = "SELECT m.*, "
                + " hp.kind AS h_kind, hp.team_id AS h_team_id, hp.national_team_id AS h_nt_id, "
                + " ap.kind AS a_kind, ap.team_id AS a_team_id, ap.national_team_id AS a_nt_id, "
                + " t_home.name AS h_team_name, t_home.logo AS h_team_logo, "
                + " nt_home.display_name AS h_nt_name, nt_home.logo AS h_nt_logo, "
                + " t_away.name AS a_team_name, t_away.logo AS a_team_logo, "
                + " nt_away.display_name AS a_nt_name, nt_away.logo AS a_nt_logo "
                + "FROM matches m "
                + "JOIN tournament_participants hp ON m.home_participant_id = hp.id "
                + "JOIN tournament_participants ap ON m.away_participant_id = ap.id "
                + "LEFT JOIN teams t_home ON hp.team_id = t_home.id "
                + "LEFT JOIN national_teams nt_home ON hp.national_team_id = nt_home.id "
                + "LEFT JOIN teams t_away ON ap.team_id = t_away.id "
                + "LEFT JOIN national_teams nt_away ON ap.national_team_id = nt_away.id "
                + "WHERE m.tournament_id = ? "
                + "ORDER BY FIELD(m.phase, 'quarterfinal','semifinal','final','group','league_round'), "
                + "         m.round_number, m.id";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, tournamentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // --- home participant ---
                    TournamentParticipant home = new TournamentParticipant();
                    home.setId(rs.getLong("home_participant_id"));
                    String hKind = rs.getString("h_kind");
                    boolean hNational = "national".equals(hKind);
                    home.setIsNational(hNational);
                    if (hNational) {
                        home.setNationalTeamId(rs.getLong("h_nt_id"));
                        home.setTeamId(null);
                        home.setName(rs.getString("h_nt_name"));
                        home.setLogo(rs.getString("h_nt_logo"));
                    } else {
                        home.setTeamId(rs.getLong("h_team_id"));
                        home.setNationalTeamId(null);
                        home.setName(rs.getString("h_team_name"));
                        home.setLogo(rs.getString("h_team_logo"));
                    }

                    // --- away participant ---
                    TournamentParticipant away = new TournamentParticipant();
                    away.setId(rs.getLong("away_participant_id"));
                    String aKind = rs.getString("a_kind");
                    boolean aNational = "national".equals(aKind);
                    away.setIsNational(aNational);
                    if (aNational) {
                        away.setNationalTeamId(rs.getLong("a_nt_id"));
                        away.setTeamId(null);
                        away.setName(rs.getString("a_nt_name"));
                        away.setLogo(rs.getString("a_nt_logo"));
                    } else {
                        away.setTeamId(rs.getLong("a_team_id"));
                        away.setNationalTeamId(null);
                        away.setName(rs.getString("a_team_name"));
                        away.setLogo(rs.getString("a_team_logo"));
                    }

                    // --- match ---
                    Match m = new Match();
                    m.setId(rs.getLong("id"));
                    m.setTournamentId(rs.getLong("tournament_id"));
                    m.setHome(home);
                    m.setAway(away);
                    m.setPhase(fromDbPhase(rs.getString("phase")));
                    int round = rs.getInt("round_number");
                    if (!rs.wasNull()) {
                        m.setRoundNumber(round);
                    }
                    m.setHomeScore(rs.getInt("home_score"));
                    m.setAwayScore(rs.getInt("away_score"));
                    m.setSimulated(rs.getBoolean("is_simulated"));

                    long winnerId = rs.getLong("winner_participant_id");
                    if (!rs.wasNull()) {
                        if (winnerId == home.getId()) {
                            m.setWinner(home);
                        } else if (winnerId == away.getId()) {
                            m.setWinner(away);
                        }
                    }

                    list.add(m);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding matches by tournament", e);
        }

        return list;
    }

    public Long findLatestSystemSimulatedTournamentId() {
        String sql = "SELECT id "
                + "FROM tournaments "
                + "WHERE mode = 'simulated' "
                + "  AND created_by_user_id IS NULL "
                + "ORDER BY created_at DESC "
                + "LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding latest system simulated tournament", e);
        }

        return null;
    }

    // Último torneo mundial creado por el sistema (created_by_user_id IS NULL)
    public Long findLatestSystemWorldCupTournamentId() {
        String sql = "SELECT id "
                + "FROM tournaments "
                + "WHERE mode = 'worldcup' "
                + "  AND created_by_user_id IS NULL "
                + "ORDER BY created_at DESC "
                + "LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding latest system worldcup tournament", e);
        }

        return null;
    }

// Último torneo mundial (sin importar si es sistema o usuario)
    public Long findLatestWorldCupTournamentId() {
        String sql = "SELECT id "
                + "FROM tournaments "
                + "WHERE mode = 'worldcup' "
                + "ORDER BY created_at DESC "
                + "LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding latest worldcup tournament", e);
        }

        return null;
    }

    public void deleteTournamentById(Long tournamentId) {
        String sql = "DELETE FROM tournaments WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, tournamentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting tournament", e);
        }
    }

    public List<Match> findUserMatchesByMode(Long userId, TournamentMode mode) {
        List<Match> list = new ArrayList<>();

        String sql = "SELECT m.*, "
                + "       t.mode, "
                + "       hp.kind AS h_kind, hp.team_id AS h_team_id, hp.national_team_id AS h_nt_id, "
                + "       ap.kind AS a_kind, ap.team_id AS a_team_id, ap.national_team_id AS a_nt_id, "
                + "       t_home.name AS h_team_name, t_home.logo AS h_team_logo, "
                + "       nt_home.display_name AS h_nt_name, nt_home.logo AS h_nt_logo, "
                + "       t_away.name AS a_team_name, t_away.logo AS a_team_logo, "
                + "       nt_away.display_name AS a_nt_name, nt_away.logo AS a_nt_logo "
                + "FROM matches m "
                + "JOIN tournaments t ON t.id = m.tournament_id "
                + "JOIN tournament_participants hp ON m.home_participant_id = hp.id "
                + "JOIN tournament_participants ap ON m.away_participant_id = ap.id "
                + "LEFT JOIN teams t_home ON hp.team_id = t_home.id "
                + "LEFT JOIN national_teams nt_home ON hp.national_team_id = nt_home.id "
                + "LEFT JOIN teams t_away ON ap.team_id = t_away.id "
                + "LEFT JOIN national_teams nt_away ON ap.national_team_id = nt_away.id "
                + "WHERE "
                + "      t.mode = ? "
                + "  AND ( "
                + "       t_home.owner_user_id = ? OR "
                + "       t_away.owner_user_id = ? OR "
                + "       nt_home.owner_user_id = ? OR "
                + "       nt_away.owner_user_id = ? "
                + "      ) "
                + "ORDER BY m.played_at ASC, m.id ASC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, toDbMode(mode));
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, userId);
            ps.setLong(5, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // ----- home -----
                    TournamentParticipant home = new TournamentParticipant();
                    home.setId(rs.getLong("home_participant_id"));
                    String hKind = rs.getString("h_kind");
                    boolean hNational = "national".equals(hKind);
                    home.setIsNational(hNational);
                    if (hNational) {
                        home.setNationalTeamId(rs.getLong("h_nt_id"));
                        home.setTeamId(null);
                        home.setName(rs.getString("h_nt_name"));
                        home.setLogo(rs.getString("h_nt_logo"));
                    } else {
                        home.setTeamId(rs.getLong("h_team_id"));
                        home.setNationalTeamId(null);
                        home.setName(rs.getString("h_team_name"));
                        home.setLogo(rs.getString("h_team_logo"));
                    }

                    // ----- away -----
                    TournamentParticipant away = new TournamentParticipant();
                    away.setId(rs.getLong("away_participant_id"));
                    String aKind = rs.getString("a_kind");
                    boolean aNational = "national".equals(aKind);
                    away.setIsNational(aNational);
                    if (aNational) {
                        away.setNationalTeamId(rs.getLong("a_nt_id"));
                        away.setTeamId(null);
                        away.setName(rs.getString("a_nt_name"));
                        away.setLogo(rs.getString("a_nt_logo"));
                    } else {
                        away.setTeamId(rs.getLong("a_team_id"));
                        away.setNationalTeamId(null);
                        away.setName(rs.getString("a_team_name"));
                        away.setLogo(rs.getString("a_team_logo"));
                    }

                    // ----- match -----
                    Match m = new Match();
                    m.setId(rs.getLong("id"));
                    m.setTournamentId(rs.getLong("tournament_id"));
                    m.setHome(home);
                    m.setAway(away);
                    m.setPhase(fromDbPhase(rs.getString("phase")));
                    int round = rs.getInt("round_number");
                    if (!rs.wasNull()) {
                        m.setRoundNumber(round);
                    }
                    m.setHomeScore(rs.getInt("home_score"));
                    m.setAwayScore(rs.getInt("away_score"));
                    m.setSimulated(rs.getBoolean("is_simulated"));

                    // si tu modelo Match tiene campo playedAt:
                    java.sql.Timestamp ts = rs.getTimestamp("played_at");
                    if (ts != null) {
                        m.setPlayed_at(new java.sql.Date(ts.getTime()));
                    } 

                    long winnerId = rs.getLong("winner_participant_id");
                    if (!rs.wasNull()) {
                        if (winnerId == home.getId()) {
                            m.setWinner(home);
                        } else if (winnerId == away.getId()) {
                            m.setWinner(away);
                        }
                    }

                    list.add(m);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user matches by mode", e);
        }

        return list;
    }
    
    public List<Match> findChampionshipsWonByUser(Long userId) {
        List<Match> list = new ArrayList<>();

        String sql = "SELECT m.*, "
                   + "       t.mode, "
                   // Datos del equipo Local del partido
                   + "       hp.kind AS h_kind, hp.team_id AS h_team_id, hp.national_team_id AS h_nt_id, "
                   + "       t_home.name AS h_team_name, t_home.logo AS h_team_logo, "
                   + "       nt_home.display_name AS h_nt_name, nt_home.logo AS h_nt_logo, "
                   // Datos del equipo Visitante del partido
                   + "       ap.kind AS a_kind, ap.team_id AS a_team_id, ap.national_team_id AS a_nt_id, "
                   + "       t_away.name AS a_team_name, t_away.logo AS a_team_logo, "
                   + "       nt_away.display_name AS a_nt_name, nt_away.logo AS a_nt_logo "
                   + "FROM matches m "
                   + "JOIN tournaments t ON t.id = m.tournament_id "
                   // JOINS para saber quién es el CAMPEÓN DEL TORNEO
                   + "JOIN tournament_participants wp ON t.winner_participant_id = wp.id "
                   + "LEFT JOIN teams w_team ON wp.team_id = w_team.id "
                   + "LEFT JOIN national_teams w_nt ON wp.national_team_id = w_nt.id "
                   // JOINS estándar para armar la info del partido (Match)
                   + "JOIN tournament_participants hp ON m.home_participant_id = hp.id "
                   + "JOIN tournament_participants ap ON m.away_participant_id = ap.id "
                   + "LEFT JOIN teams t_home ON hp.team_id = t_home.id "
                   + "LEFT JOIN national_teams nt_home ON hp.national_team_id = nt_home.id "
                   + "LEFT JOIN teams t_away ON ap.team_id = t_away.id "
                   + "LEFT JOIN national_teams nt_away ON ap.national_team_id = nt_away.id "
                   + "WHERE "
                   + "      m.phase = 'final' " // Solo queremos ver la final
                   + "  AND ( " // Verificamos que el dueño del CAMPEÓN sea el usuario
                   + "        w_team.owner_user_id = ? OR "
                   + "        w_nt.owner_user_id = ? "
                   + "      ) "
                   + "ORDER BY m.played_at DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // --- Mapeo idéntico al anterior ---
                    
                    // 1. Home Participant
                    TournamentParticipant home = new TournamentParticipant();
                    home.setId(rs.getLong("home_participant_id"));
                    String hKind = rs.getString("h_kind");
                    boolean hNational = "national".equals(hKind);
                    home.setIsNational(hNational);
                    if (hNational) {
                        home.setNationalTeamId(rs.getLong("h_nt_id"));
                        home.setName(rs.getString("h_nt_name"));
                        home.setLogo(rs.getString("h_nt_logo"));
                    } else {
                        home.setTeamId(rs.getLong("h_team_id"));
                        home.setName(rs.getString("h_team_name"));
                        home.setLogo(rs.getString("h_team_logo"));
                    }

                    // 2. Away Participant
                    TournamentParticipant away = new TournamentParticipant();
                    away.setId(rs.getLong("away_participant_id"));
                    String aKind = rs.getString("a_kind");
                    boolean aNational = "national".equals(aKind);
                    away.setIsNational(aNational);
                    if (aNational) {
                        away.setNationalTeamId(rs.getLong("a_nt_id"));
                        away.setName(rs.getString("a_nt_name"));
                        away.setLogo(rs.getString("a_nt_logo"));
                    } else {
                        away.setTeamId(rs.getLong("a_team_id"));
                        away.setName(rs.getString("a_team_name"));
                        away.setLogo(rs.getString("a_team_logo"));
                    }

                    // 3. Match Object
                    Match m = new Match();
                    m.setId(rs.getLong("id"));
                    m.setTournamentId(rs.getLong("tournament_id"));
                    m.setHome(home);
                    m.setAway(away);
                    m.setPhase(fromDbPhase(rs.getString("phase"))); // Asegúrate de tener este método helper accesible o copiado
                    m.setRoundNumber(rs.getInt("round_number"));
                    m.setHomeScore(rs.getInt("home_score"));
                    m.setAwayScore(rs.getInt("away_score"));
                    m.setSimulated(rs.getBoolean("is_simulated"));
                    java.sql.Timestamp ts = rs.getTimestamp("played_at");
                    if (ts != null) {
                        m.setPlayed_at(new java.sql.Date(ts.getTime()));
                    } 

                    // Set Winner en el objeto Match
                    long matchWinnerId = rs.getLong("winner_participant_id");
                    if (!rs.wasNull()) {
                        if (matchWinnerId == home.getId()) m.setWinner(home);
                        else if (matchWinnerId == away.getId()) m.setWinner(away);
                    }

                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding championships won by user", e);
        }
        return list;
    }

    public List<Match> findUserGeneralMatches(Long userId) {
        // aquí decides qué consideras “general”: SIMULATED, LEAGUE, etc.
        return findUserMatchesByMode(userId, TournamentMode.GENERAL);
    }

    public List<Match> findUserWorldCupMatches(Long userId) {
        return findUserMatchesByMode(userId, TournamentMode.WORLD_CUP);
    }

    public List<Match> findGeneralUserMatchesByDate(Long userId, String date) {
        List<Match> matches = new ArrayList<>();

        List<Match> allMatches = findUserGeneralMatches(userId);

        for (Match m : allMatches) {
            // Compara las fechas de los partidos con la fecha seleccionada
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String matchDate = sdf.format(m.getPlayed_at());
            if (matchDate.equals(date)) {
                matches.add(m);
            }
        }

        return matches;
    }

    public List<Match> findWorldCuplUserMatchesByDate(Long userId, String date) {
        List<Match> matches = new ArrayList<>();

        List<Match> allMatches = findUserWorldCupMatches(userId);

        for (Match m : allMatches) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String matchDate = sdf.format(m.getPlayed_at());
            if (matchDate.equals(date)) {
                matches.add(m);
            }
        }

        return matches;
    }

    private MatchPhase fromDbPhase(String phase) {
        if (phase == null) {
            return MatchPhase.GROUP;
        }
        switch (phase) {
            case "group":
                return MatchPhase.GROUP;
            case "quarterfinal":
                return MatchPhase.QUARTERFINAL;
            case "semifinal":
                return MatchPhase.SEMIFINAL;
            case "final":
                return MatchPhase.FINAL;
            case "league_round":
                return MatchPhase.LEAGUE_ROUND;
            default:
                return MatchPhase.GROUP;
        }
    }
}
