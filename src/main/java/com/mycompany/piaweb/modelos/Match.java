/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import com.mycompany.piaweb.enums.MatchPhase;
import java.sql.Date;

/**
 *
 *
 */
public class Match {
    private Long id; 
    private Long tournamentId;
    private TournamentParticipant home;
    private TournamentParticipant away;
    private MatchPhase phase;
    private Integer roundNumber;

    private int homeScore;
    private int awayScore;
    private TournamentParticipant winner;
    private boolean simulated;
    
    private Date played_at;
    
    public Match(Long id, Long tournamentId, TournamentParticipant home, TournamentParticipant away, MatchPhase phase, Integer roundNumber, int homeScore, int awayScore, TournamentParticipant winner, boolean simulated) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.home = home;
        this.away = away;
        this.phase = phase;
        this.roundNumber = roundNumber;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.winner = winner;
        this.simulated = simulated;
    }

    public Match() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public TournamentParticipant getHome() {
        return home;
    }

    public void setHome(TournamentParticipant home) {
        this.home = home;
    }

    public TournamentParticipant getAway() {
        return away;
    }

    public void setAway(TournamentParticipant away) {
        this.away = away;
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public TournamentParticipant getWinner() {
        return winner;
    }

    public void setWinner(TournamentParticipant winner) {
        this.winner = winner;
    }

    public boolean isSimulated() {
        return simulated;
    }

    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }
    
    public Date getPlayed_at() {
        return played_at;
    }

    public void setPlayed_at(Date played_at) {
        this.played_at = played_at;
    }

}
