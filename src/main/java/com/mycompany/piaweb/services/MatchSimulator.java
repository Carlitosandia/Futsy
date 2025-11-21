/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.services;

import com.mycompany.piaweb.modelos.Match;
import com.mycompany.piaweb.modelos.TournamentParticipant;
import java.util.Random;

/**
 *
 * @author Carlo
 */

public class MatchSimulator {

    private final Random random = new Random();

    public void simulate(Match match) {
        TournamentParticipant home = match.getHome();
        TournamentParticipant away = match.getAway();

        int ratingHome = home.getOverall();
        int ratingAway = away.getOverall();

        if (ratingHome <= 0) ratingHome = 1;
        if (ratingAway <= 0) ratingAway = 1;

        double pHomeWin = (double) ratingHome / (ratingHome + ratingAway);
        double r = random.nextDouble();

        int homeGoals;
        int awayGoals;

        if (r < pHomeWin) {
            // Gana local
            homeGoals = 1 + random.nextInt(4); // 1..4
            awayGoals = random.nextInt(homeGoals + 1); // 0..homeGoals
            match.setWinner(home);
        } else {
            // Gana visita
            awayGoals = 1 + random.nextInt(4);
            homeGoals = random.nextInt(awayGoals + 1);
            match.setWinner(away);
        }

        match.setHomeScore(homeGoals);
        match.setAwayScore(awayGoals);
        match.setSimulated(true);
    }
}
