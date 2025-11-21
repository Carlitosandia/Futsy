/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

/**
 *
 * @author Ravellion
 */
public class PostImage {
    private int id;
    private String path; // Ruta de la imagen
    private int sortOrder; // Orden, opcional
    private int postId;


    public PostImage() {}

    public PostImage(String path, int sortOrder) {
        this.path = path;
        this.sortOrder = sortOrder;
    }
    
    public int getId() { 
        return id; 
    }         
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getPostId() {
    return postId;
}

public void setPostId(int postId) {
    this.postId = postId;
}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}