/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

/**
 *
 */
public class Countries {
    private int id;
    private String name;
    private String iso2;

    public Countries(int id, String name, String iso2) {
        this.id = id;
        this.name = name;
        this.iso2 = iso2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public Countries() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIso2() {
        return iso2;
    }
    
}
