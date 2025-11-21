/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.util.List;

/**
 *
 * @author Ravellion
 */
public class Post {
    private int id;
    private int authorUserId;
    private String authorName;
    private String authorUsername;
    private String authorImage; 
    private String description;
    private String title;
    private String featuredLeague;
    private String featuredWorldcup;
    private PostImage image; 
    private List<Tags> tags;

    
    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorUserId() {
        return authorUserId;
    }
    public void setAuthorUserId(int authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorImage() {
        return authorImage;
    }
    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTitle() { 
        return title; 
    }   
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getFeaturedLeague() { 
        return featuredLeague; 
    }
    
    public void setFeaturedLeague(String featuredLeague) { 
        this.featuredLeague = featuredLeague; 
    }
    
    public String getFeaturedWorldcup() { 
        return featuredWorldcup; 
    }
    public void setFeaturedWorldcup(String featuredWorldcup) { 
        this.featuredWorldcup = featuredWorldcup; 
    }
    
    public PostImage getImage() {
        return image;
    }
    public void setImage(PostImage image) {
        this.image = image;
    }
}