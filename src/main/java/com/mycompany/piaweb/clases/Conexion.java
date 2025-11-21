/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sofia
 */
public class Conexion {

    String bd = "futsy";
    String url = "jdbc:mysql://localhost:3306/";
    String user = "root";
    String password = "root";
    String driver = "com.mysql.cj.jdbc.Driver";

    
    public Conexion() {
    }

    // ✔ Siempre devuelve una conexión nueva
    public Connection Conectar() throws SQLException, ClassNotFoundException {
        System.out.println("Intentando conectar a la base de datos...");
        Class.forName(driver);
        Connection nuevaConexion = DriverManager.getConnection(url + bd, user, password);
        System.out.println("Conexión exitosa");
        return nuevaConexion;
    }

    // ✔ Cierra SOLO la conexión que le pases
    public void Desconectar(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión:");
            e.printStackTrace();
        }
    }
}
