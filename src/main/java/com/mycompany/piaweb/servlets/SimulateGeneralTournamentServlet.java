package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.dao.TournamentDao;
import com.mycompany.piaweb.enums.TournamentMode;
import com.mycompany.piaweb.modelos.Tournament;
import com.mycompany.piaweb.modelos.TournamentParticipant;
import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.services.MatchSimulator;
import com.mycompany.piaweb.services.KnockoutTournamentEngine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;
import java.util.List;

@WebServlet(name = "/SimulateGeneralTournamentServlet")
public class SimulateGeneralTournamentServlet extends HttpServlet {

    private TournamentDao tournamentDao;
    private KnockoutTournamentEngine knockoutEngine;

    @Override
    public void init() throws ServletException {
        this.tournamentDao = new TournamentDao(); 
        MatchSimulator simulator = new MatchSimulator();
        this.knockoutEngine = new KnockoutTournamentEngine(simulator);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        // Obtener el objeto Users de la sesión
        com.mycompany.piaweb.modelos.Users usuarioSesion = (com.mycompany.piaweb.modelos.Users) session.getAttribute("usuarioSesion");
        int userId = usuarioSesion.getIdUsers();

        // 1. Crear objeto Tournament (en memoria)
        Tournament tournament = new Tournament();
        tournament.setName("Liga General - Usuario " + userId);
        tournament.setMode(TournamentMode.GENERAL);
        tournament.setSimulationAllowed(true);
        tournament.setCreated_by_user_id(Long.valueOf(userId)); // o null si quieres que sea “del sistema”

        // 2. Persistir torneo en BD
        Long tournamentId = tournamentDao.createTournament(tournament);
        tournament.setId(tournamentId);

        // 3. Insertar participantes
        //   Opción A: solo el equipo del usuario
        //   List<TournamentParticipant> participants =
        //           tournamentDao.insertParticipantsForGeneral(tournamentId, userId);

        //   Opción B: torneo de 8 equipos random general (puedes mezclar luego)
        List<TournamentParticipant> participants =
                tournamentDao.insertParticipantsForGlobalGeneral(tournamentId, 8);

        // 4. Calcular el overall de cada participante
        for (TournamentParticipant p : participants) {
            int overall;
            if (p.getIsNational()) {
                overall = tournamentDao.calculateOverallForNationalTeam(p.getNationalTeamId());
            } else {
                overall = tournamentDao.calculateOverallForTeam(p.getTeamId());
            }
            p.setOverall(overall);
        }
        tournament.setParticipants(participants);

        // 5. Generar el cuadro (cuartos)
        knockoutEngine.generateBracket(tournament);

        // 6. Simular todos los partidos (cuartos, semis, final)
        knockoutEngine.simulateAll(tournament);

        // 7. Guardar los partidos en la BD
        List<Match> matches = tournament.getMatches();
        tournamentDao.insertMatches(matches);

        // 8. Actualizar campeón en la tabla tournaments
        if (tournament.getChampion() != null) {
            tournamentDao.updateChampion(tournamentId, tournament.getChampion().getId());
        }

        // 9. Redirigir a la página donde se pinta el bracket
        response.sendRedirect("ligaGeneral.jsp?tournamentId=" + tournamentId);
    }
}
