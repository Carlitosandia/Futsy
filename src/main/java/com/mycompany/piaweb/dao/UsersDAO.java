/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.dao;

/**
 *
 * @author Sofia
 */
import com.mycompany.piaweb.modelos.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * El DAO(Data Object Access) contiene las consultas a la bd y prepara los datos obtenidos para regresar la informacion
 * en el Modelo definido anteriormente.
 */
public class UsersDAO {
    Connection conn;

    //Por medio del constructor puede acceder a la conexion establecida en la clase Conexion.
    public UsersDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
    *Cada metodo definido en esta clase esta diseñada para realizar una consulta a la bd.
    * Insert
    * Update
    * Delete
    * Select
    *
    */
    
    public List<Users> getAll(){
    
    List<Users> listaUsers = new ArrayList<>();
    
    PreparedStatement ps=null;
      
        try{
        ps = conn.prepareStatement("Select * From users ");
       
        
        ResultSet rs = ps.executeQuery();
        
       while(rs.next()){
       Users usuario = new Users();
       
       usuario.setName(rs.getString("usuario"));
       usuario.setPassword(rs.getString("contrasena"));
       
       
       listaUsers.add(usuario);
       }
        }catch(SQLException ex){
        
        }
       
    
    return listaUsers;
        
    }
    
    public Users getUsuario(int id) {
    Users usuario = null;

    String sql = "SELECT id, name, lastname, username, email, birthday, phone, `password`, image_url " +
                 "FROM users WHERE id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            usuario = new Users();

            usuario.setIdUsers(rs.getInt("id"));
            usuario.setName(rs.getString("name"));
            usuario.setLastname(rs.getString("lastname"));
            usuario.setUsername(rs.getString("username"));
            usuario.setEmail(rs.getString("email"));
            usuario.setPhone(rs.getString("phone"));
            usuario.setPassword(rs.getString("password"));
            usuario.setBirthday(rs.getDate("birthday"));
            usuario.setImage(rs.getString("image_url"));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return usuario;
}

    
    //Metodo para realizar un insert, tienen que agregar todos lo atributos que solicita la tabla
    //exceptuando los que se agregar directamente en la base como el id autoincrement
   
    public boolean insertUsuario(Users usuario){
    
        PreparedStatement ps=null;
        
        try{
             //cada "?" es un valor almacenado en el modelo.
        ps = conn.prepareStatement("Insert Into users(name,lastname,username,email,birthday,phone,password,image_url) Values (?,?,?,?,?,?,?,?)");
        
        //Esos valores se llenan por medio del siguiente metodo y el primer argumento que se pide es la posicion del "?"
        //contando de izquierda a derecha, se tiene que espesificar que tipo de dato se va almacenar, ejemplo: setString, int setInt,etc.
        ps.setString(1, usuario.getName());
        ps.setString(2, usuario.getLastname());
        ps.setString(3, usuario.getUsername());
        ps.setString(4, usuario.getEmail());
        ps.setDate(5, usuario.getBirthday());
        ps.setString(6, usuario.getPhone());
        ps.setString(7, usuario.getPassword());
        ps.setString(8, usuario.getImage());
        int insert = ps.executeUpdate();
        
        if(insert!=0){
            
            System.out.println("usuario insertado correctamente");
           
        
            return true;
        
        }else{
        System.out.println("usuario no se ha insertado correctamente");
            return false;
        
        }
        }catch(SQLException ex){
              ex.printStackTrace();
        return false;
        }
        
    }
    
    public void updateUsuario(Users usuario){
    String sql = "UPDATE users SET name = ?, lastname = ?, email = ?, birthday = ?, image_url = ?, status = ?, `password` = ? WHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, usuario.getName());
        ps.setString(2, usuario.getLastname());
        ps.setString(3, usuario.getEmail());
        ps.setDate(4, usuario.getBirthday()); // java.sql.Date
        ps.setString(5, usuario.getImage());
        ps.setString(6, usuario.isStatus() ? "active" : "banned");  // status activo/inactivo
        ps.setString(7, usuario.getPassword()); 
        ps.setInt(8, usuario.getIdUsers());

        int update = ps.executeUpdate();

        if(update != 0){
            System.out.println("Usuario actualizado correctamente");
        } else {
            System.out.println("Error al actualizar usuario");
        }

    } catch(SQLException ex){
        ex.printStackTrace();
    }
}

    
    public void deleteUsuario(int idUsuario){
      PreparedStatement ps=null;
        
        try{
        ps = conn.prepareStatement("UPDATE users SET status = 'banned', deleted_at = NOW() WHERE idUsers = ?");
        ps.setInt(1, idUsuario);

        
        int delete = ps.executeUpdate();
        
        if(delete!=0){
        //Mensaje Exitoso
        }else{
        
        //Mensaje Error
        }
        }catch(SQLException ex){
        
        }
    }
    
    public Users getUsuario(String nombreUsuario, String password) {
    Users usuario = null;

    final String u = nombreUsuario == null ? "" : nombreUsuario.trim();
    final String p = password == null ? "" : password.trim();

    System.out.printf("Login username=[%s](len=%d) password=[%s](len=%d)%n", u, u.length(), p, p.length());

    final String sql = "SELECT id, name, lastname, username, email, birthday, phone, `password`, image_url, status " +
                   "FROM users WHERE username = ? AND `password` = ? AND status = 'active'";


    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, u);
        ps.setString(2, p); // si usas hash, aquí no comparas, traes por username y luego verificas el hash en Java

        System.out.println("Ejecutando login query...");
        try (ResultSet rs = ps.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("️ No hay filas para ese usuario/password.");
            }

            if (rs.next()) {
                usuario = new Users();
                  
                usuario.setIdUsers(rs.getInt("id"));
                usuario.setName(rs.getString("name"));
                usuario.setLastname(rs.getString("lastname"));
                usuario.setUsername(rs.getString("username"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPhone(rs.getString("phone"));
                usuario.setPassword(rs.getString("password"));
                // si tienes birthday como DATE en el modelo:
                usuario.setBirthday(rs.getDate("birthday"));
                String statusStr = rs.getString("status"); // trae "active", "banned" o "pending"
                usuario.setStatus("active".equalsIgnoreCase(statusStr)); // true solo si está activo
                usuario.setImage(rs.getString("image_url"));

                System.out.println("Usuario encontrado: " + usuario.getUsername() +
                                   " | ID: " + usuario.getIdUsers());
            }
        }
    } catch (SQLException ex) {
        System.err.println("Error en getUsuario: " + ex.getMessage());
        ex.printStackTrace();
    }

    return usuario;
}

    
}

