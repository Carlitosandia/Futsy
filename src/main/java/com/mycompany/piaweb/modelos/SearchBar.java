/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.sql.Date;

/**
 *
 * @author Carlo
 */
public class SearchBar {
    private String title;
    private String author_user_id;
    private String description;
    private boolean featured_league;
    private boolean featured_worldcup;
    private Date created_at;
    private Date updated_at;
    private Date deleted_at;

    public SearchBar(String title, String author_user_id, String description, boolean featured_league, boolean featured_worldcup, Date created_at, Date updated_at, Date deleted_at) {
        this.title = title;
        this.author_user_id = author_user_id;
        this.description = description;
        this.featured_league = featured_league;
        this.featured_worldcup = featured_worldcup;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }

    public SearchBar() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor_user_id() {
        return author_user_id;
    }

    public void setAuthor_user_id(String author_user_id) {
        this.author_user_id = author_user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFeatured_league() {
        return featured_league;
    }

    public void setFeatured_league(boolean featured_league) {
        this.featured_league = featured_league;
    }

    public boolean isFeatured_worldcup() {
        return featured_worldcup;
    }

    public void setFeatured_worldcup(boolean featured_worldcup) {
        this.featured_worldcup = featured_worldcup;
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

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }
    
}
