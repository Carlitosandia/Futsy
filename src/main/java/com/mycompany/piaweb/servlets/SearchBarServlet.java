package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO; // Necesario para buscar las imágenes
import com.mycompany.piaweb.dao.SearchBarDAO;
import com.mycompany.piaweb.dao.TagsDAO;
import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;
import com.mycompany.piaweb.modelos.Tags;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SearchBarServlet", urlPatterns = {"/SearchBarServlet"})
public class SearchBarServlet extends HttpServlet {

    private static final int POSTS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Recibir parámetros
        String type = request.getParameter("type"); // "advanced" o null
        String simpleQuery = request.getParameter("q");
        
        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {
            
            List<Post> allPosts = new ArrayList<>();
            SearchBarDAO searchDAO = new SearchBarDAO(conn);
            String tituloFeed = "";

            // --- LÓGICA DE SELECCIÓN DE BÚSQUEDA ---
            
            if ("advanced".equals(type)) {
                // >> BÚSQUEDA AVANZADA
                String searchText = request.getParameter("searchText");
                String searchTagId = request.getParameter("searchTagId");
                String dateFrom = request.getParameter("dateFrom");
                String dateTo = request.getParameter("dateTo");
                
                allPosts = searchDAO.advancedSearch(searchText, searchTagId, dateFrom, dateTo);
                tituloFeed = "Resultados filtrados";
                
                // Pasamos los parámetros de vuelta para mantener la paginación (si quisieras implementarlo al 100%)
                // Por ahora, simple.
                
            } else if (simpleQuery != null && !simpleQuery.trim().isEmpty()) {
                // >> BÚSQUEDA SIMPLE
                allPosts = searchDAO.singleSearch(simpleQuery.trim());
                tituloFeed = "Resultados para: \"" + simpleQuery + "\"";
            } else {
                // Nada seleccionado
                response.sendRedirect("GetPostsServlet");
                return;
            }
            
            request.setAttribute("feedTitle", tituloFeed);

            // --- DE AQUÍ HACIA ABAJO ES EXACTAMENTE IGUAL (Paginación) ---
            
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) { page = 1; }
            }

            int totalPosts = allPosts.size();
            int start = (page - 1) * POSTS_PER_PAGE;
            
            if (start > totalPosts) { start = 0; page = 1; }

            int end = Math.min(start + POSTS_PER_PAGE, totalPosts);
            List<Post> postsPage = allPosts.subList(start, end);

            // Cargar imágenes
            PostsDAO postsDAO = new PostsDAO(conn); 
            for (Post post : postsPage) {
                PostImage img = postsDAO.getPostImage(post.getId());
                post.setImage(img);
            }

            request.setAttribute("posts", postsPage);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE));

            TagsDAO tagsDAO = new TagsDAO(conn);
            List<Tags> tags = tagsDAO.getAll();
            request.setAttribute("tags", tags);

            request.getRequestDispatcher("parati.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetPostsServlet");
        }
    }
}