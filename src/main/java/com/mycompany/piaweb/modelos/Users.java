/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.modelos;

import java.sql.Date;

/**
 *
 * @author Sofia
 */
public class Users {

    int id;
    String name;
    String lastname;
    String username;
    java.sql.Date birthday;
    String email;
    String phone;
    String password;
    boolean status;
    String image_url;
    Date created_at;
    Date updated_at;
    Date deleted_at;

    public Users() {

    }

    public Users(int idUsers, String name, String lastname, String username, Date birthday, String email, String phone, String password, boolean status, String image_url, Date created_at, Date updated_at, Date deleted_at) {
        this.id = idUsers;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.image_url = image_url;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }

    public int getIdUsers() {
        return id;
    }

    public void setIdUsers(int idUsers) {
        this.id = idUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image_url;
    }

    public void setImage(String image) {
        this.image_url = image;
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
