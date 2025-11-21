package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.TeamsMundialDAO;
import com.mycompany.piaweb.modelos.TeamsMundial;
import com.mycompany.piaweb.modelos.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 
 */
@WebServlet("/CrearEquipoMundialServlet")
public class CrearEquipoMundialServlet extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("CrearEquipoServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");

        
        String teamName = request.getParameter("nombreEquipo");
       
        String teamIdParam = request.getParameter("teamId"); 
        boolean isEdit = (teamIdParam != null && !teamIdParam.isEmpty());

        String playerListRaw = request.getParameter("jugadoresSeleccionados");
        String countryIdRaw = request.getParameter("paisSeleccionado");
        int countryId = 0;

        
        if (countryIdRaw != null && !countryIdRaw.equals("TODOS") && !countryIdRaw.isEmpty()) {
            try {
                countryId = Integer.parseInt(countryIdRaw);
            } catch (NumberFormatException e) {
                countryId = 0;
            }
        }

        
        List<Integer> playerIds = new ArrayList<>();
        if (playerListRaw != null && !playerListRaw.isEmpty()) {
            String[] parts = playerListRaw.split(",");
            for (String part : parts) {
                try {
                    playerIds.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("ID inválido ignorado: " + part);
                }
            }
        }

        if (playerIds.isEmpty()) {
            response.sendRedirect("CrearEquipoServlet?error=SinJugadores");
            return;
        }

        
        String logoPathForDB;
        if (countryId == 0) {
            logoPathForDB = "public/assets/EquipoSinLogo.png";
        } else {
            
            logoPathForDB = "public/assets/banderas/" + countryIdRaw + ".png";
        }

        
        TeamsMundial newTeam = new TeamsMundial();
        if (isEdit) {
            newTeam.setId(Integer.parseInt(teamIdParam));
        }
        newTeam.setDisplay_name(teamName);
        newTeam.setOwner_user_id(usuarioSesion.getIdUsers());
        newTeam.setLogo(logoPathForDB);
        newTeam.setCountry_id(countryId);

        Conexion conn = new Conexion();
        try (Connection connection = conn.Conectar()) {
            TeamsMundialDAO teamMundialDAO = new TeamsMundialDAO(connection);
            boolean exito;

            
            if (isEdit) {
                exito = teamMundialDAO.updateTeamMundial(newTeam, playerIds);
            } else {
                exito = teamMundialDAO.insertTeamMundial(newTeam, playerIds);
            }

            if (exito) {
                session = request.getSession();
                session.setAttribute("mensajeFlash", isEdit ? "Selección actualizada correctamente." : "¡Tu selección fue creada con éxito!");
                session.setAttribute("tipoMensaje", "success");
            } else {
                session = request.getSession();
                session.setAttribute("mensajeFlash", "Hubo un problema al guardar tu selección.");
                session.setAttribute("tipoMensaje", "danger");
            }
            response.sendRedirect("CrearEquipoServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("CrearEquipoServlet?error=ErrorServidor");
        }
    }
}