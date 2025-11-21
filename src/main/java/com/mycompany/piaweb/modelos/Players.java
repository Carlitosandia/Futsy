/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.sql.Date;

/**
 *
 
 */
public class Players {

    private int id;
    private String name;
    private String position;
    private int nationality_id;
    private int overall;
    private Date created_at;
    private Date updated_at;
    private String countryName;

    public Players(int id, String name, String position, int nationality_id, int overall, Date created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.nationality_id = nationality_id;
        this.overall = overall;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getNationality_id() {
        return nationality_id;
    }

    public int getOverall() {
        return overall;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

}
