/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import com.mycompany.piaweb.enums.TournamentMode;
import java.util.List;

/**
 *
 */
public class Tournament {
    private Long id;
    private String name;
    private TournamentMode mode;
    private boolean simulationAllowed;
    private Long created_by_user_id;
    private List<TournamentParticipant> participants;
    private List<Match> matches;
    private TournamentParticipant champion;

    public Tournament(Long id, String name, TournamentMode mode, boolean simulationAllowed, Long created_by_user_id, List<TournamentParticipant> participants, List<Match> matches, TournamentParticipant champion) {
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.simulationAllowed = simulationAllowed;
        this.created_by_user_id = created_by_user_id;
        this.participants = participants;
        this.matches = matches;
        this.champion = champion;
    }

    

    public Tournament() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentMode getMode() {
        return mode;
    }

    public void setMode(TournamentMode mode) {
        this.mode = mode;
    }

    public boolean isSimulationAllowed() {
        return simulationAllowed;
    }

    public void setSimulationAllowed(boolean simulationAllowed) {
        this.simulationAllowed = simulationAllowed;
    }

    public List<TournamentParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<TournamentParticipant> participants) {
        this.participants = participants;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public TournamentParticipant getChampion() {
        return champion;
    }

    public void setChampion(TournamentParticipant champion) {
        this.champion = champion;
    }

    public Long getCreated_by_user_id() {
        return created_by_user_id;
    }

    public void setCreated_by_user_id(Long created_by_user_id) {
        this.created_by_user_id = created_by_user_id;
    }
    
}
