/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList; 

/**
 *
 * @author Carlo
 */
public class Teams {
        private int id;
    private String name;
    private int owner_user_id;
    private String type;
    private Date created_at;
    private Date updated_at;
    private String logo;
    private List<Integer> playerIds = new ArrayList<>();
    
    public Teams() {
    }

    public Teams(int id, String name, int owner_user_id, String type, Date created_at, Date updated_at, String logo) {
        this.id = id;
        this.name = name;
        this.owner_user_id = owner_user_id;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner_user_id() {
        return owner_user_id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setOwner_user_id(int owner_user_id) {
        this.owner_user_id = owner_user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
