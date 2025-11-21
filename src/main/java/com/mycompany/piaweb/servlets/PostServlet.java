/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO;
import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;
import com.mycompany.piaweb.modelos.Users;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ravellion
 */
@WebServlet(name = "PostServlet", urlPatterns = {"/PostServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class PostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session.getAttribute("usuarioSesion") == null) {
            System.out.print("Intento de crear una publicacion sin haber hecho login");
            session.setAttribute("mensajeFlash", "Para poder publicar posts tienes que hacer iniciado sesion.");
            session.setAttribute("tipoMensaje", "danger");
            response.sendRedirect("frontpage.jsp");
            return;
        }
        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
        int usuarioID = usuarioSesion.getIdUsers();
        System.out.println("ID del usuario logueado: " + usuarioID);
        if (usuarioSesion == null) {
            System.out.println("Usuario en sesión es null");
        } else {
            System.out.println("Usuario en sesión: " + usuarioSesion.getUsername()
                    + ", ID: " + usuarioID);
        }
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        
        if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            System.out.println("Intento de publicación con campos vacíos");
            session.setAttribute("mensajeFlash", "El título y la descripción son obligatorios.");
            session.setAttribute("tipoMensaje", "danger");
            response.sendRedirect("GetPostsServlet");
            return;
        }
        
        System.out.println("Título del post: " + title);
        System.out.println("Descripción del post: " + description);

        String[] tagIdsParam = request.getParameterValues("tagIds");
        List<Integer> tagIds = new ArrayList<>();
        if (tagIdsParam != null) {
            for (String idStr : tagIdsParam) {
                try {
                    int tagId = Integer.parseInt(idStr);
                    tagIds.add(tagId);
                } catch (NumberFormatException e) {
                    System.out.println("ID de tag inválido: " + idStr);
                }
            }
        }
        System.out.println("Tags seleccionados: " + tagIds);

        Part filePart = request.getPart("image");
        if (filePart != null) {
            System.out.println("Archivo subido: " + filePart.getSubmittedFileName()
                    + ", tamaño: " + filePart.getSize());
        } else {
            System.out.println("No se subió ninguna imagen");
        }

        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            fileName = new File(filePart.getSubmittedFileName()).getName();
        }

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {

            PostsDAO postsDAO = new PostsDAO(conn);

            // Crear post
            Post post = new Post();
            post.setAuthorUserId(usuarioSesion.getIdUsers());
            post.setTitle(title != null ? title : "");
            post.setDescription(description != null ? description : "");

            System.out.println("Insertando post con datos:");
            System.out.println(" - AuthorUserID: " + post.getAuthorUserId());
            System.out.println(" - Title: " + post.getTitle());
            System.out.println(" - Description: " + post.getDescription());

            int postId = postsDAO.insertPost(post);
            System.out.println("Post insertado con ID generado: " + postId);

            if (!tagIds.isEmpty()) {
                postsDAO.insertPostTags(postId, tagIds);
                System.out.println("Tags asociados al post: " + tagIds);
            } else {
                System.out.println("El post no tiene tags seleccionados.");
            }

            // Guardar imagen si existe
            if (fileName != null) {
                System.out.println("Guardando imagen en uploads/" + fileName);

                String uploadPath = getServletContext().getRealPath("/public/uploads");
                System.out.println("uploadPath final: " + uploadPath);

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                // Guardar físicamente el archivo
                filePart.write(uploadPath + File.separator + fileName);

                PostImage img = new PostImage();
                img.setPostId(postId);
                img.setPath("public/uploads/" + fileName);
                img.setSortOrder(1);

                postsDAO.insertPostImage(img);
                System.out.println("Imagen insertada correctamente en la DB");
            }

            // Respuesta simple estilo RegistroServlet
            request.setAttribute("MensajePost", "Publicación creada correctamente");
            response.sendRedirect("GetPostsServlet");

        } catch (Exception e) {
            System.out.println("️ Error al crear la publicación");
            e.printStackTrace(); // imprime la traza completa
            request.getRequestDispatcher("parati.jsp").forward(request, response);
        }
    }
}
