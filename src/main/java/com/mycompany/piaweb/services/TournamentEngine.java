/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.services;

import com.mycompany.piaweb.modelos.Tournament;

/**
 *
 
 */
public interface TournamentEngine {
    void generateBracket(Tournament tournament);
    void simulateAllMatches(Tournament tournament);
}