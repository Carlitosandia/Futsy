package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.TagsDAO;
import com.mycompany.piaweb.modelos.Tags;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LoadAdvancedSearchServlet", urlPatterns = {"/LoadAdvancedSearchServlet"})
public class LoadAdvancedSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {
            TagsDAO tagsDAO = new TagsDAO(conn);
            List<Tags> tags = tagsDAO.getAll();
            
            request.setAttribute("listaTags", tags);
            request.getRequestDispatcher("busquedaavanzada.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp");
        }
    }
}