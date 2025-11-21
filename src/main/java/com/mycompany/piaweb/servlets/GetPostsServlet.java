/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;
import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO;
import com.mycompany.piaweb.dao.TagsDAO;
import com.mycompany.piaweb.modelos.Tags;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Ravellion
 */
@WebServlet(name = "GetPostsServlet", urlPatterns = {"/GetPostsServlet"})
public class GetPostsServlet extends HttpServlet {

    private static final int POSTS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tagParam = request.getParameter("tag");
        String tagTitle = null;

        if (tagParam != null) {
            tagTitle = tagParam.trim();
            if (tagTitle.startsWith("#")) {
                tagTitle = tagTitle.substring(1); 
            }
        }

        if (tagTitle != null) {
            tagTitle = tagTitle.trim();
        }
        System.out.println("Buscando posts para el tag [" + tagTitle + " ]");
        List<Post> posts;
        Conexion conexion = new Conexion();

        try (Connection conn = conexion.Conectar()) {

            PostsDAO postsDAO = new PostsDAO(conn);

            if (tagTitle != null && !tagTitle.isEmpty()) {
                posts = postsDAO.getPostsByTagTitle(tagTitle);
                request.setAttribute("feedTitle", "Posts con el tag: " + tagTitle);
                request.setAttribute("currentTag", tagTitle);
            } else {
                posts = postsDAO.getAllPosts();
                request.setAttribute("feedTitle", "PARA TI");
            }

            // A cada post, agregar su imagen principal
            for (Post post : posts) {
                PostImage img = postsDAO.getPostImage(post.getId());
                post.setImage(img);
            }

            // Paginación
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            int totalPosts = posts.size();
            int start = (page - 1) * POSTS_PER_PAGE;
            int end = Math.min(start + POSTS_PER_PAGE, totalPosts);

            List<Post> postsPage = posts.subList(start, end);

            // Guardamos los posts en el request
            request.setAttribute("posts", postsPage);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE));

            TagsDAO tagsDAO = new TagsDAO(conn);
            List<Tags> tags = tagsDAO.getAll();
            request.setAttribute("tags", tags);

            System.out.println("Tags encontrados: " + tags.size()); // para revisar

            request.setAttribute("tags", tags);

            // Enviamos a la página JSP
            request.getRequestDispatcher("parati.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "No se pudieron cargar los posts");
            request.getRequestDispatcher("parati.jsp").forward(request, response);
        }
    }
}
