/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.modelos.Post;
import com.mycompany.piaweb.modelos.PostImage;
import com.mycompany.piaweb.modelos.Users;
import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.PostsDAO;
import com.mycompany.piaweb.dao.UsersDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;



/**
 *
 * @author Ravellion
 */
@WebServlet(name = "GetMyProfileServlet", urlPatterns = {"/GetMyProfileServlet"})
public class GetMyProfileServlet extends HttpServlet {
    
    private static final int POSTS_POR_PAGINA = 10; // Número de posts por página

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
        
        // Obtener parámetro de página, si no existe, la primera página (1)
        int pagina = 1;
        String paginaParam = request.getParameter("pagina");
        if (paginaParam != null) {
            try {
                pagina = Integer.parseInt(paginaParam);
            } catch (NumberFormatException e) {
                pagina = 1;
            }
        }

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {

            UsersDAO usersDAO = new UsersDAO(conn);
            PostsDAO postsDAO = new PostsDAO(conn);

            // Obtener información actualizada del usuario
            Users usuario = usersDAO.getUsuario(usuarioSesion.getIdUsers());

            // Obtener posts creados por este usuario
            //List<Post> postsUsuario = postsDAO.getPostsByUser(usuario.getIdUsers());
            
            // Formatear fecha de nacimiento a dd-MM-yyyy para mostrar en JSP
            if (usuario.getBirthday() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String fechaFormateada = sdf.format(usuario.getBirthday());
                request.setAttribute("birthdayFormatted", fechaFormateada);
            } else {
                request.setAttribute("birthdayFormatted", "");
            }
            
            // Obtener total de posts del usuario
            int totalPosts = postsDAO.countPostsByUser(usuario.getIdUsers());

            // Calcular offset para la consulta
            int offset = (pagina - 1) * POSTS_POR_PAGINA;

            // Traer posts de la página actual
            List<Post> postsUsuario = postsDAO.getPostsByUser(usuario.getIdUsers(), offset, POSTS_POR_PAGINA);

            // Traer imagen principal de cada post
            for (Post post : postsUsuario) {
                PostImage img = postsDAO.getPostImage(post.getId());
                post.setImage(img);
            }
            
            // Calcular total de páginas
            int totalPaginas = (int) Math.ceil((double) totalPosts / POSTS_POR_PAGINA);

            request.setAttribute("usuario", usuario);
            request.setAttribute("postsUsuario", postsUsuario);
            request.setAttribute("paginaActual", pagina);
            request.setAttribute("totalPaginas", totalPaginas);

            request.getRequestDispatcher("miperfil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
