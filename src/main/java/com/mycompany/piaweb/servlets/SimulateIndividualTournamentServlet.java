package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.dao.TournamentDao;
import com.mycompany.piaweb.enums.TournamentMode;
import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.modelos.Tournament;
import com.mycompany.piaweb.modelos.TournamentParticipant;
import com.mycompany.piaweb.modelos.Users;
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

@WebServlet("/SimulateIndividualTournamentServlet")
public class SimulateIndividualTournamentServlet extends HttpServlet {

    private TournamentDao tournamentDao;
    private KnockoutTournamentEngine knockoutEngine;

    @Override
    public void init() throws ServletException {
        this.tournamentDao = new TournamentDao(); // usa tu Conexion interna
        MatchSimulator simulator = new MatchSimulator();
        this.knockoutEngine = new KnockoutTournamentEngine(simulator);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

         HttpSession session = request.getSession(false);
        if (session.getAttribute("usuarioSesion") == null) {
            session.setAttribute("mensajeFlash", "Para simular un torneo individual tienes que inicar sesion primero.");
            session.setAttribute("tipoMensaje", "danger");
            response.sendRedirect("frontpage.jsp");
            return;
        }

        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
        int userId = usuarioSesion.getIdUsers();
        
        Tournament tournament = new Tournament();
        tournament.setName("Liga Individual - User " + userId);
        tournament.setMode(TournamentMode.SIMULATED);
        tournament.setSimulationAllowed(true);
        tournament.setCreated_by_user_id(Long.valueOf(userId));

        Long tournamentId = tournamentDao.createTournament(tournament);
        tournament.setId(tournamentId);

        // Insertar participantes
        List<TournamentParticipant> participants =
                tournamentDao.insertParticipantsForGlobalGeneral(tournamentId, 8);

        //  Calcular overall para cada participante
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

        // Generar el bracket de KO
        knockoutEngine.generateBracket(tournament);

        // Simular todos los partidos
        knockoutEngine.simulateAll(tournament);

        // Guardar partidos en BD
        List<Match> matches = tournament.getMatches();
        tournamentDao.insertMatches(matches);

        //  Guardar campeón
        if (tournament.getChampion() != null) {
            tournamentDao.updateChampion(tournamentId, tournament.getChampion().getId());
        }

        // Redirigir a la liga individual mostrando ese torneo
        response.sendRedirect("individualpage.jsp?tournamentId=" + tournamentId);
    }
}
