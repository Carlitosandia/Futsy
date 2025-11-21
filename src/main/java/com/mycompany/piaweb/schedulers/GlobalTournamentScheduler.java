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
        // Se ejecuta cuando se levanta la app
        // Pool de 2 hilos para correr 2 tareas periódicas
        scheduler = Executors.newScheduledThreadPool(2);

        scheduler.scheduleAtFixedRate(
                new GlobalLeagueTournamentTask(),
                0,
                10,
                TimeUnit.MINUTES
        );

// Mundial cada 5 min, empezando 1 min después
        scheduler.scheduleAtFixedRate(
                new GlobalWorldCupTournamentTask(),
                1,
                10,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    // ==========================================================
    //  Tarea 1: Liga General Global (clubes)
    // ==========================================================
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

                // 1. Crear torneo SIMULATED (global general)
                Tournament tournament = new Tournament();
                tournament.setName("Liga General Global (auto)");
                tournament.setMode(TournamentMode.GENERAL);
                tournament.setSimulationAllowed(true);
                // created_by_user_id = null -> sistema
                tournament.setCreated_by_user_id(null);

                Long tournamentId = tournamentDao.createTournament(tournament);
                tournament.setId(tournamentId);

                // 2. Insertar participantes (8 equipos general random)
                List<TournamentParticipant> participants
                        = tournamentDao.insertParticipantsForGlobalGeneral(tournamentId, 8);

                if (participants.size() < 8) {
                    System.out.println("[Scheduler] [Liga] No hay suficientes equipos ("
                            + participants.size() + "/8). Borrando torneo.");
                    tournamentDao.deleteTournamentById(tournamentId);
                    return;
                }

                // 3. Calcular overall por participante
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

                // 4. Generar bracket (cuartos, semis, final)
                knockoutEngine.generateBracket(tournament);

                // 5. Simular todos los partidos
                knockoutEngine.simulateAll(tournament);

                // 6. Guardar partidos
                List<Match> matches = tournament.getMatches();
                tournamentDao.insertMatches(matches);

                // 7. Guardar campeón
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

    // ==========================================================
    //  Tarea 2: Mundial Global (selecciones)
    // ==========================================================
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

                // 1. Crear torneo WORLD_CUP (global mundial)
                Tournament tournament = new Tournament();
                tournament.setName("Mundial Global (auto)");
                tournament.setMode(TournamentMode.WORLD_CUP);
                tournament.setSimulationAllowed(true);
                tournament.setCreated_by_user_id(null); // sistema

                Long tournamentId = tournamentDao.createTournament(tournament);
                tournament.setId(tournamentId);

                // 2. Insertar participantes (ej: 16 selecciones random)
                List<TournamentParticipant> participants
                        = tournamentDao.insertParticipantsForWorldCup(tournamentId, 8);

                if (participants.size() < 8) {
                    System.out.println("[Scheduler] [Mundial] No hay suficientes selecciones ("
                            + participants.size() + "/8). Borrando torneo.");
                    tournamentDao.deleteTournamentById(tournamentId);
                    return;
                }

                // 3. Calcular overall
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

                // 4. Generar bracket
                knockoutEngine.generateBracket(tournament);

                // 5. Simular partidos
                knockoutEngine.simulateAll(tournament);

                // 6. Guardar partidos
                List<Match> matches = tournament.getMatches();
                tournamentDao.insertMatches(matches);

                // 7. Guardar campeón
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
