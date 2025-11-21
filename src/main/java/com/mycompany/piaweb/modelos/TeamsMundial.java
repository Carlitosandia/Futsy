/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlo
 */
public class TeamsMundial {

    private int id;
    private int owner_user_id;
    private int country_id;
    private String display_name;
    private String logo;
    private Date created_at;
    private Date updated_at;
    private List<Integer> playerIds = new ArrayList<>();

    public TeamsMundial(int id, int owner_user_id, int country_id, String display_name, String logo, Date created_at, Date updated_at) {
        this.id = id;
        this.owner_user_id = owner_user_id;
        this.country_id = country_id;
        this.display_name = display_name;
        this.logo = logo;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public TeamsMundial() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_user_id() {
        return owner_user_id;
    }

    public void setOwner_user_id(int owner_user_id) {
        this.owner_user_id = owner_user_id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getLogo() {
        if (this.country_id > 0) {
            // Construimos la ruta dinámicamente
            return "public/assets/banderas/" + this.country_id + ".png";
        }
        // Si no hay país seleccionado, devolvemos el logo por defecto
        return "public/assets/EquipoSinLogo.png";
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public List<Integer> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Integer> playerIds) {
        this.playerIds = playerIds;
    }

}
