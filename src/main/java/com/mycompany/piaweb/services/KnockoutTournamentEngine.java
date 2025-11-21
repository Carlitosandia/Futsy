/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.services;

import com.mycompany.piaweb.enums.MatchPhase;
import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.modelos.Tournament;
import com.mycompany.piaweb.modelos.TournamentParticipant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnockoutTournamentEngine {

    private final MatchSimulator simulator;

    public KnockoutTournamentEngine(MatchSimulator simulator) {
        this.simulator = simulator;
    }

    // Genera sólo cuartos (8 equipos) en base al listado de participantes
    public void generateBracket(Tournament tournament) {
        List<TournamentParticipant> participants =
                new ArrayList<>(tournament.getParticipants());

        if (participants.size() < 8) {
            throw new IllegalArgumentException("Se requieren al menos 8 participantes para KO");
        }

        // Mezclar aleatoriamente
        Collections.shuffle(participants);

        List<Match> matches = new ArrayList<>();

        // Cuartos de final
        for (int i = 0; i < 8; i += 2) {
            Match m = new Match();
            m.setTournamentId(tournament.getId());
            m.setHome(participants.get(i));
            m.setAway(participants.get(i + 1));
            m.setPhase(MatchPhase.QUARTERFINAL);
            m.setRoundNumber(1);
            matches.add(m);
        }

        tournament.setMatches(matches);
    }

    public void simulateAll(Tournament tournament) {
        List<Match> allMatches = new ArrayList<>(tournament.getMatches());

        // 1) cuartos
        List<TournamentParticipant> semifinalists = new ArrayList<>();
        for (Match m : allMatches) {
            if (m.getPhase() == MatchPhase.QUARTERFINAL) {
                simulator.simulate(m);
                semifinalists.add(m.getWinner());
            }
        }

        // 2) semis
        List<Match> semis = new ArrayList<>();
        for (int i = 0; i < semifinalists.size(); i += 2) {
            Match semi = new Match();
            semi.setTournamentId(tournament.getId());
            semi.setHome(semifinalists.get(i));
            semi.setAway(semifinalists.get(i + 1));
            semi.setPhase(MatchPhase.SEMIFINAL);
            semi.setRoundNumber(2);
            simulator.simulate(semi);
            semis.add(semi);
        }
        allMatches.addAll(semis);

        // 3) final
        TournamentParticipant f1 = semis.get(0).getWinner();
        TournamentParticipant f2 = semis.get(1).getWinner();

        Match finalMatch = new Match();
        finalMatch.setTournamentId(tournament.getId());
        finalMatch.setHome(f1);
        finalMatch.setAway(f2);
        finalMatch.setPhase(MatchPhase.FINAL);
        finalMatch.setRoundNumber(3);
        simulator.simulate(finalMatch);

        allMatches.add(finalMatch);

        tournament.setMatches(allMatches);
        tournament.setChampion(finalMatch.getWinner());
    }
}
