/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

/**
 *
 */
public class TournamentParticipant {
    private Long id;           
    private Long teamId;           
    private Long nationalTeamId;    
    private String name;
    private String logo;
    private int overall;          
    private boolean isNational;

    public TournamentParticipant(Long id, Long teamId, Long nationalTeamId, String name, String logo, int overall, boolean isNational) {
        this.id = id;
        this.teamId = teamId;
        this.nationalTeamId = nationalTeamId;
        this.name = name;
        this.logo = logo;
        this.overall = overall;
        this.isNational = isNational;
    }

    public TournamentParticipant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getNationalTeamId() {
        return nationalTeamId;
    }

    public void setNationalTeamId(Long nationalTeamId) {
        this.nationalTeamId = nationalTeamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public boolean getIsNational() {
        return isNational;
    }

    public void setIsNational(boolean isNational) {
        this.isNational = isNational;
    }
    
}
