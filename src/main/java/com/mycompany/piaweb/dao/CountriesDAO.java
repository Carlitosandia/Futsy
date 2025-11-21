/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

import com.mycompany.piaweb.modelos.Countries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountriesDAO {
    Connection conn;
    
    public CountriesDAO(Connection conn){
        this.conn = conn;
    }

    public List<Countries> getAll() {
        List<Countries> listaCountries = new ArrayList<>();

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM COUNTRIES ");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Countries country = new Countries();

                country.setId(rs.getInt("id"));
                country.setName(rs.getString("name"));
                country.setIso2(rs.getString("iso2"));

                listaCountries.add(country);
            }
        } catch (SQLException ex) {

        }

        return listaCountries;
    }
}
