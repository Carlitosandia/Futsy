/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.UsersDAO;
import com.mycompany.piaweb.modelos.Users;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sofia
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String nombreUsuario = request.getParameter("usuario");
        String pass = request.getParameter("contrasena");

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("MensajeLogin", "El formulario de login debe usar el metodo POST.");
        request.getRequestDispatcher("/index.jsp").forward(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("usuario y contraseña");
        String nombreUsuario = request.getParameter("usuario");
        String pass = request.getParameter("contrasena");

        Conexion conexion = null;
        try {
            System.out.println("Conexion");
            conexion = new Conexion();
            System.out.println("Intentando conectar a la base de datos...");
            Connection con = conexion.Conectar();
            System.out.println("conexion instanciada, iniando instancia de usersDAO");

            UsersDAO uDao = new UsersDAO(con);
            System.out.println("usersDAO instanciado, invocando metodo .getusuario");
            Users usuario = uDao.getUsuario(nombreUsuario, pass);

            if (usuario != null && usuario.isStatus()) {
                System.out.println("if");
                request.setAttribute("Users", usuario);
                System.out.println("Usuario obtenido del DAO: " + usuario.getIdUsers());
                request.getSession().setAttribute("usuarioSesion", usuario);
                System.out.println("Metodo para redireccionar a otra vista/Servlet");
                request.setAttribute("MensajeHome", "bienvenido " + usuario.getName());
                List<Users> listaUsuarios = uDao.getAll();
                request.setAttribute("listaUsuarios", listaUsuarios);
                request.getRequestDispatcher("frontpage.jsp").forward(request, response);
            } else {
                System.out.println("else");
                request.setAttribute("MensajeLoginError", "Usuario/Contraseña incorrectos o cuenta no activa");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("MensajeLogin", "Error de servidor al intentar iniciar sesión.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
