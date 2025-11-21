/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.UsersDAO;
import com.mycompany.piaweb.modelos.Users;
import java.time.LocalDate;
import java.time.ZoneId;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.annotation.MultipartConfig;
import java.io.File;

/**
 *
 * @author Sofia
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB - tamaño en memoria
        maxFileSize = 1024 * 1024 * 10, // 10 MB - tamaño máximo de archivo
        maxRequestSize = 1024 * 1024 * 15 // 15 MB - tamaño máximo total de la solicitud
)
public class RegistroServlet extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegistroServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegistroServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);

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

        //Se reciben la informacion del formulario que envia la vista
        Part filePart = request.getPart("fotoR");
        final String fileName = request.getParameter("usuarioR") + ".jpg";
        File dir = new File("C:\\Users\\Carlo\\Documents\\PIAWEB-Av\\PIAWEB\\src\\main\\webapp\\public\\imagenes\\" + request.getParameter("usuarioR") + "\\FotoPerfil\\");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        filePart.write(dir.getAbsolutePath() + File.separator + fileName);
        String nombreUsuario = request.getParameter("nombre");
        String ApellidosUsuario = request.getParameter("apellidos");
        String Username = request.getParameter("usuarioR");
        String TelefonoUsuario = request.getParameter("telefonoR");
        String email = request.getParameter("correoR");
        String Pass = request.getParameter("contrasenaR");
        String passwordConfirm = request.getParameter("confirmarContrasenaR");
        LocalDate fechaCreacion = LocalDate.now();
        Date fechaC = Date.from(fechaCreacion.atStartOfDay(ZoneId.systemDefault()).toInstant());

        //Convertir string a fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String text_fechaNacimiento = request.getParameter("fechaNacimiento");
        java.util.Date fecha_Nacimiento_util = null;
        java.sql.Date sqlFechaNacimiento = null;

        try {
            fecha_Nacimiento_util = dateFormat.parse(text_fechaNacimiento);

            java.util.Calendar fechaNac = java.util.Calendar.getInstance();
            fechaNac.setTime(fecha_Nacimiento_util);

            java.util.Calendar hoy = java.util.Calendar.getInstance();

            // Calcular la diferencia de años
            int edad = hoy.get(java.util.Calendar.YEAR) - fechaNac.get(java.util.Calendar.YEAR);

            // Ajustar la fecha de cumpleaños para saber si este año ya fue su cumple
            if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < fechaNac.get(java.util.Calendar.DAY_OF_YEAR)) {
                edad--;
            }

            // Verificar si es menor de 13
            if (edad < 13) {
                request.setAttribute("MensajeRegistro", "Debes tener al menos 13 años para registrarte.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            sqlFechaNacimiento = new java.sql.Date(fecha_Nacimiento_util.getTime());
        } catch (Exception e) {
            System.out.println("Error al procesar la fecha: " + e.getMessage());
            request.setAttribute("MensajeRegistro", "Formato de fecha no válido.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
        String regexNombre = "^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$";

        if (nombreUsuario != null && ApellidosUsuario != null) {
            nombreUsuario = nombreUsuario.trim();
            ApellidosUsuario = ApellidosUsuario.trim();

            boolean nombreValido = nombreUsuario.matches(regexNombre);
            boolean apellidoValido = ApellidosUsuario.matches(regexNombre);

            if (nombreValido && apellidoValido) {
                System.out.println("Datos válidos: " + nombreUsuario + " " + ApellidosUsuario);
            } else {
                System.out.println("Error: El nombre o apellido contiene caracteres inválidos.");
                request.setAttribute("MensajeRegistro", "Los nombres y apellidos no pueden contener caracteres especiales.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
        }

        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$";
        if (Pass != null) {
            if (!Pass.matches(regexPassword)) {
                System.out.println("Error: La contraseña no cumple los requisitos de seguridad.");
                request.setAttribute("MensajeRegistro", "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("MensajeRegistro", "La contraseña es obligatoria.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (!Pass.equals(passwordConfirm)) {
                System.out.println("Error: La contraseña no cumple los requisitos de seguridad.");
                request.setAttribute("MensajeRegistro", "La contraseñas contraseñas no coinciden.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
        }

        System.out.println(nombreUsuario);
        System.out.println(ApellidosUsuario);
        System.out.println(Username);
        System.out.println(TelefonoUsuario);
        System.out.println(Pass);
        System.out.println(fechaC);
        System.out.println(fechaCreacion);
        System.out.println(sqlFechaNacimiento);

        Conexion conn = null;
        try {

            //Se crea un objeto del modelo para despues llenarlo con los datos capturados
            Users usuario = new Users();
            usuario.setName(nombreUsuario);
            usuario.setLastname(ApellidosUsuario);
            usuario.setUsername(Username);
            usuario.setPhone(TelefonoUsuario);
            usuario.setEmail(email);
            usuario.setPassword(Pass);
            usuario.setImage(fileName);
            java.sql.Date sqlFechaCreacion = new java.sql.Date(fechaC.getTime());
            usuario.setCreated_at(sqlFechaCreacion);
            usuario.setBirthday(sqlFechaNacimiento);

            System.out.println("Se termino de insertar");
            //Se declara el objeto de la conexion
            conn = new Conexion();
            System.out.println("Se instancio la conexion");
            //Conectamos el DAO utilizando el objeto de Conexion y el metodo conectar()
            UsersDAO uDao = new UsersDAO(conn.Conectar());
            System.out.println("UsersDAO");
            //Recibimos la respuesta del metodo del DAO
            boolean insert = uDao.insertUsuario(usuario);
            System.out.println("Boolean insert");
            if (insert) {
                //Metodo para redireccionar a otra vista/Servlet
                request.setAttribute("MensajeRegistro", "Usuario registrado correctamente");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                System.out.println("Metodo para redireccionar a otra vista/Servlet");
            } else {
                request.setAttribute("MensajeRegistro", "Usuario  no registrado correctamente");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                System.out.println("Metodo para redireccionar a otra vista/Servlet ELSE");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(RegistroServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Ya no se llama a conn.Desconectar()
            System.out.println("RegistroServlet finalizado");
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
