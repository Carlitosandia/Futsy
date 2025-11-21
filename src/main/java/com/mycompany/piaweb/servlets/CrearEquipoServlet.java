package com.mycompany.piaweb.servlets;

import com.mycompany.piaweb.clases.Conexion;
import com.mycompany.piaweb.dao.CountriesDAO;
import com.mycompany.piaweb.dao.PlayersDAO;
import com.mycompany.piaweb.dao.TeamsDAO;
import com.mycompany.piaweb.dao.TeamsMundialDAO;
import com.mycompany.piaweb.modelos.Countries;
import com.mycompany.piaweb.modelos.Players;
import com.mycompany.piaweb.modelos.Users;
import com.mycompany.piaweb.modelos.Teams;
import com.mycompany.piaweb.modelos.TeamsMundial;
import java.util.List;
import jakarta.servlet.http.Part;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Carlo
 */
@WebServlet(name = "CrearEquipoServlet", urlPatterns = {"/CrearEquipoServlet"})
@MultipartConfig
public class CrearEquipoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session.getAttribute("usuarioSesion") == null) {
            session.setAttribute("mensajeFlash", "Para registrar un equipo tienes que iniciar sesión.");
            session.setAttribute("tipoMensaje", "danger");
            response.sendRedirect("frontpage.jsp");
            return;
        }
        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.Conectar()) {
            PlayersDAO playersDAO = new PlayersDAO(conn);
            request.setAttribute("playersList", playersDAO.getAllPlayers());

            CountriesDAO countriesDAO = new CountriesDAO(conn);
            request.setAttribute("countriesList", countriesDAO.getAll());

            TeamsDAO teamsDAO = new TeamsDAO(conn);
            Teams equipoGeneral = teamsDAO.getTeamByUserId(usuarioSesion.getIdUsers());
            request.setAttribute("equipoGeneral", equipoGeneral);

            TeamsMundialDAO tmDAO = new TeamsMundialDAO(conn);
            TeamsMundial equipoMundial = tmDAO.getTeamMundialByUserId(usuarioSesion.getIdUsers());
            request.setAttribute("equipoMundial", equipoMundial);

            if (session.getAttribute("mensajeFlash") != null) {
                request.setAttribute("Mensaje", session.getAttribute("mensajeFlash"));
                request.setAttribute("TipoAlerta", session.getAttribute("tipoMensaje"));
                session.removeAttribute("mensajeFlash");
                session.removeAttribute("tipoMensaje");
            }

            request.getRequestDispatcher("crearequipo.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");

        // Recibir parámetros básicos
        String teamName = request.getParameter("nombreEquipo");
        String teamType = request.getParameter("tipoEquipo");
        String playerListRaw = request.getParameter("jugadoresSeleccionados");

        // 1. DETECTAR SI ES EDICIÓN (Recibimos un ID oculto desde el JSP)
        String teamIdParam = request.getParameter("teamId");
        boolean isEdit = (teamIdParam != null && !teamIdParam.isEmpty());

        // Procesar IDs de jugadores... (Igual que tu código actual)
        List<Integer> playerIds = new ArrayList<>();
        if (playerListRaw != null && !playerListRaw.isEmpty()) {
            String[] parts = playerListRaw.split(",");
            for (String part : parts) {
                playerIds.add(Integer.parseInt(part.trim()));
            }
        }

        String logoPathForDB = "public/assets/EquipoSinLogo.png";

        if (isEdit) {
            // Si es edición, recuperamos el logo anterior por si no sube uno nuevo
            logoPathForDB = request.getParameter("logoActual");
        }
        Part filePart = request.getPart("logoEquipo");
        if (filePart != null && filePart.getSize() > 0) {
            String submittedFileName = filePart.getSubmittedFileName();
            String fileExtension = "";
            int dotIndex = submittedFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = submittedFileName.substring(dotIndex);
            }
            String fileName = "team_" + System.currentTimeMillis() + fileExtension;
            String relativeFolder = "public/imagenes/" + usuarioSesion.getUsername() + "/LogoEquipo/";
            String basePath = request.getServletContext().getRealPath("/") + relativeFolder;
            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(basePath + fileName);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            logoPathForDB = relativeFolder + fileName;

        }

        Teams team = new Teams();
        team.setName(teamName);
        team.setOwner_user_id(usuarioSesion.getIdUsers());
        team.setType(teamType);
        team.setLogo(logoPathForDB);
        if (isEdit) {
            team.setId(Integer.parseInt(teamIdParam));
        }

        Conexion conn = new Conexion();
        try (Connection connection = conn.Conectar()) {
            TeamsDAO teamsDAO = new TeamsDAO(connection);
            boolean exito;

            if (isEdit) {
                // ACTUALIZAR
                exito = teamsDAO.updateTeamGeneral(team, playerIds);
            } else {
                // CREAR
                exito = teamsDAO.insertTeamGeneral(team, playerIds);
            }

            if (exito) {
                session.setAttribute("mensajeFlash", isEdit ? "Equipo actualizado correctamente." : "Equipo creado correctamente.");
                session.setAttribute("tipoMensaje", "success");
            } else {
                session.setAttribute("mensajeFlash", "Error al guardar el equipo.");
                session.setAttribute("tipoMensaje", "danger");
            }
            response.sendRedirect("CrearEquipoServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("CrearEquipoServlet");
        }
    }
}
