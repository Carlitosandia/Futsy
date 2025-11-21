package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO;
import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "ManagePostServlet", urlPatterns = {"/ManagePostServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, 
        maxFileSize = 1024 * 1024 * 10, 
        maxRequestSize = 1024 * 1024 * 15 
)
public class ManagePostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

      
        String postIdStr = request.getParameter("postId");
        String action = request.getParameter("action");

        if (postIdStr == null || action == null) {
            response.sendRedirect("GetMyProfileServlet");
            return;
        }

        int postId = Integer.parseInt(postIdStr);

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {
            PostsDAO postsDAO = new PostsDAO(conn);

            if ("delete".equals(action)) {
                
                postsDAO.deletePost(postId);
                System.out.println("Post eliminado: " + postId);

            } else if ("edit".equals(action)) {

                String title = request.getParameter("title");
                String description = request.getParameter("description");
                if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
                    session.setAttribute("mensajeFlash", "El título y la descripción no pueden estar vacíos.");
                    session.setAttribute("tipoMensaje", "danger");
                    response.sendRedirect("GetMyProfileServlet");
                    return;
                }
                Post post = new Post();
                post.setId(postId);
                post.setTitle(title);
                post.setDescription(description);

                Part filePart = request.getPart("postImage");

                if (filePart != null && filePart.getSize() > 0) {

                    String fileName = new File(filePart.getSubmittedFileName()).getName();

                    String uploadPath = getServletContext().getRealPath("/public/uploads");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    
                    filePart.write(uploadPath + File.separator + fileName);
                    System.out.println("Imagen actualizada guardada en: " + uploadPath + File.separator + fileName);

                   
                    PostImage img = new PostImage();
                    img.setPostId(postId);
                    img.setPath("public/uploads/" + fileName); 
                    img.setSortOrder(0); 

                   
                    post.setImage(img);
                }

                
                postsDAO.updatePost(post);
                System.out.println("Post actualizado: " + postId);
            }

            response.sendRedirect("GetMyProfileServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetMyProfileServlet");
        }
    }
}
