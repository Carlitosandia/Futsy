/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

/**
 *
 * @author Sofia
 */
public class Tags {
    
    int id;
    String slug;
    String title;
    boolean league_featured;
    boolean worldcup_featured;

    public Tags() {
    }

    public Tags(int id, String slug, String title, boolean league_featured, boolean worldcup_featured) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.league_featured = league_featured;
        this.worldcup_featured = worldcup_featured;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLeague_featured() {
        return league_featured;
    }

    public void setLeague_featured(boolean league_featured) {
        this.league_featured = league_featured;
    }

    public boolean isWorldcup_featured() {
        return worldcup_featured;
    }

    public void setWorldcup_featured(boolean worldcup_featured) {
        this.worldcup_featured = worldcup_featured;
    }
    
    
}
