package com.mycompany.piaweb.schedulers;

import com.mycompany.piaweb.dao.TournamentDao;
import com.mycompany.piaweb.enums.TournamentMode;
import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.modelos.Tournament;
import com.mycompany.piaweb.modelos.TournamentParticipant;
import com.mycompany.piaweb.services.MatchSimulator;
import com.mycompany.piaweb.services.KnockoutTournamentEngine;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class GlobalTournamentScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        
        scheduler = Executors.newScheduledThreadPool(2);

        scheduler.scheduleAtFixedRate(
                new GlobalLeagueTournamentTask(),
                0,
                1,
                TimeUnit.MINUTES
        );


        scheduler.scheduleAtFixedRate(
                new GlobalWorldCupTournamentTask(),
                1,
                1,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    
    static class GlobalLeagueTournamentTask implements Runnable {

        private final TournamentDao tournamentDao;
        private final KnockoutTournamentEngine knockoutEngine;

        GlobalLeagueTournamentTask() {
            this.tournamentDao = new TournamentDao();
            MatchSimulator simulator = new MatchSimulator();
            this.knockoutEngine = new KnockoutTournamentEngine(simulator);
        }

        @Override
        public void run() {
            try {
                System.out.println("[Scheduler] Generando torneo global de Liga General...");

                
                Tournament tournament = new Tournament();
                tournament.setName("Liga General Global (auto)");
                tournament.setMode(TournamentMode.GENERAL);
                tournament.setSimulationAllowed(true);
               
                tournament.setCreated_by_user_id(null);

                Long tournamentId = tournamentDao.createTournament(tournament);
                tournament.setId(tournamentId);

                
                List<TournamentParticipant> participants
                        = tournamentDao.insertParticipantsForGlobalGeneral(tournamentId, 8);

                if (participants.size() < 8) {
                    System.out.println("[Scheduler] [Liga] No hay suficientes equipos ("
                            + participants.size() + "/8). Borrando torneo.");
                    tournamentDao.deleteTournamentById(tournamentId);
                    return;
                }

                
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
                    tournamentDao.updateChampion(
                            tournamentId,
                            tournament.getChampion().getId()
                    );
                }

                System.out.println("[Scheduler] [Liga] Torneo global generado con ID = " + tournamentId);

            } catch (Exception e) {
                System.err.println("[Scheduler] [Liga] Error generando torneo global:");
                e.printStackTrace();
            }
        }
    }

    
    static class GlobalWorldCupTournamentTask implements Runnable {

        private final TournamentDao tournamentDao;
        private final KnockoutTournamentEngine knockoutEngine;

        GlobalWorldCupTournamentTask() {
            this.tournamentDao = new TournamentDao();
            MatchSimulator simulator = new MatchSimulator();
            this.knockoutEngine = new KnockoutTournamentEngine(simulator);
        }

        @Override
        public void run() {
            try {
                System.out.println("[Scheduler] Generando torneo global de Mundial...");

               
                Tournament tournament = new Tournament();
                tournament.setName("Mundial Global (auto)");
                tournament.setMode(TournamentMode.WORLD_CUP);
                tournament.setSimulationAllowed(true);
                tournament.setCreated_by_user_id(null); 

                Long tournamentId = tournamentDao.createTournament(tournament);
                tournament.setId(tournamentId);

               
                List<TournamentParticipant> participants
                        = tournamentDao.insertParticipantsForWorldCup(tournamentId, 8);

                if (participants.size() < 8) {
                    System.out.println("[Scheduler] [Mundial] No hay suficientes selecciones ("
                            + participants.size() + "/8). Borrando torneo.");
                    tournamentDao.deleteTournamentById(tournamentId);
                    return;
                }

               
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
                    tournamentDao.updateChampion(
                            tournamentId,
                            tournament.getChampion().getId()
                    );
                }

                System.out.println("[Scheduler] [Mundial] Torneo global generado con ID = " + tournamentId);

            } catch (Exception e) {
                System.err.println("[Scheduler] [Mundial] Error generando torneo global:");
                e.printStackTrace();
            }
        }
    }
}
