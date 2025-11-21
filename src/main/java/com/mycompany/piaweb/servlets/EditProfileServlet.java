package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO;
import com.mycompany.piaweb.dao.UsersDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import java.util.List;
import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.Users;
import com.mycompany.piaweb.modelos.PostImage;
import java.util.Date;

@WebServlet(name = "EditProfileServlet", urlPatterns = {"/EditProfileServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");

        
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String correo = request.getParameter("correo");
        String birthdayStr = request.getParameter("birthday");
        String passwordUpdated = request.getParameter("passwordUpdated");
        String passwordConfirm = request.getParameter("passwordConfirm");

        String regexNombre = "^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$";

        if (nombre != null && apellidos != null) {
            nombre = nombre.trim();
            apellidos = apellidos.trim();

            boolean nombreValido = nombre.matches(regexNombre);
            boolean apellidoValido = apellidos.matches(regexNombre);

            if (nombreValido && apellidoValido) {
                System.out.println("Datos válidos: " + nombre + " " + apellidos);
            } else {
                System.out.println("Error: El nombre o apellido contiene caracteres inválidos.");
                session.setAttribute("mensajeFlash", "El nombre y apellido no pueden tener caracteres especiales o numeros.");
                session.setAttribute("tipoMensaje", "danger"); 
                response.sendRedirect("GetMyProfileServlet");
                return;
            }
        }
        
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$";
        if (passwordUpdated != null) {
            if (!passwordUpdated.matches(regexPassword)) {
                System.out.println("Error: La contraseña no cumple los requisitos de seguridad.");
                session.setAttribute("mensajeFlash", "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
                session.setAttribute("tipoMensaje", "danger"); 
                response.sendRedirect("GetMyProfileServlet");
                return;
            }
        } else {
            request.setAttribute("MensajeRegistro", "La contraseña es obligatoria.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        System.out.println("Contrasenia vieja: " + passwordUpdated);
        System.out.println("Contrasenia confirmacion: " + passwordConfirm);
        if (!passwordUpdated.equals(passwordConfirm)) {
            session.setAttribute("mensajeFlash", "Las contraseñas no coinciden. Inténtalo de nuevo.");
            session.setAttribute("tipoMensaje", "danger"); 
            response.sendRedirect("GetMyProfileServlet");
            return;
        }
        java.sql.Date birthday = null;

        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            try {
                Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayStr);
                java.util.Calendar fechaNac = java.util.Calendar.getInstance();
                fechaNac.setTime(parsedDate);

                java.util.Calendar hoy = java.util.Calendar.getInstance();

                
                int edad = hoy.get(java.util.Calendar.YEAR) - fechaNac.get(java.util.Calendar.YEAR);

                
                if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < fechaNac.get(java.util.Calendar.DAY_OF_YEAR)) {
                    edad--;
                }

               
                if (edad < 13) {
                    session.setAttribute("mensajeFlash", "Fecha inválida: Debes tener al menos 13 años.");
                    session.setAttribute("tipoMensaje", "danger");
                    response.sendRedirect("GetMyProfileServlet");
                    return; 
                }

                birthday = new java.sql.Date(parsedDate.getTime());

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("mensajeFlash", "Formato de fecha incorrecto.");
                session.setAttribute("tipoMensaje", "danger");
                response.sendRedirect("GetMyProfileServlet");
                return;
            }
        }

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {
            UsersDAO usersDAO = new UsersDAO(conn);

            
            usuarioSesion.setName(nombre);
            usuarioSesion.setLastname(apellidos);
            usuarioSesion.setEmail(correo);
            usuarioSesion.setPassword(passwordConfirm);
            if (birthday != null) {
                usuarioSesion.setBirthday(birthday);
            }

           
            Part filePart = request.getPart("fotoPerfil");
            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = filePart.getSubmittedFileName();
                String fileExtension = "";
                int dotIndex = submittedFileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileExtension = submittedFileName.substring(dotIndex); // Obtiene .png, .jpg, etc.
                }
                String fileName = usuarioSesion.getUsername() + fileExtension;
                String basePath = request.getServletContext().getRealPath("/") + "public/imagenes/" + usuarioSesion.getUsername() + "/FotoPerfil/";
                File dir = new File(basePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(basePath + fileName);

                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                usuarioSesion.setImage(fileName);
            }

            
            usersDAO.updateUsuario(usuarioSesion); 

            
            Users usuarioActualizado = usersDAO.getUsuario(usuarioSesion.getIdUsers());
            session.setAttribute("usuarioSesion", usuarioActualizado);

            
            response.sendRedirect("GetMyProfileServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
