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
        
        com.mycompany.piaweb.modelos.Users usuarioSesion = (com.mycompany.piaweb.modelos.Users) session.getAttribute("usuarioSesion");
        int userId = usuarioSesion.getIdUsers();

        
        Tournament tournament = new Tournament();
        tournament.setName("Liga General - Usuario " + userId);
        tournament.setMode(TournamentMode.GENERAL);
        tournament.setSimulationAllowed(true);
        tournament.setCreated_by_user_id(Long.valueOf(userId)); 

        
        Long tournamentId = tournamentDao.createTournament(tournament);
        tournament.setId(tournamentId);

        
        
        List<TournamentParticipant> participants =
                tournamentDao.insertParticipantsForGlobalGeneral(tournamentId, 8);

        
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

        
        knockoutEngine.generateBracket(tournament);

        
        knockoutEngine.simulateAll(tournament);

       
        List<Match> matches = tournament.getMatches();
        tournamentDao.insertMatches(matches);

        
        if (tournament.getChampion() != null) {
            tournamentDao.updateChampion(tournamentId, tournament.getChampion().getId());
        }

        
        response.sendRedirect("ligaGeneral.jsp?tournamentId=" + tournamentId);
    }
}
