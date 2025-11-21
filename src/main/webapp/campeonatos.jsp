<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.piaweb.dao.TournamentDao"%>
<%@ page import="com.mycompany.piaweb.modelos.Match"%>
<%@ page import="com.mycompany.piaweb.modelos.Users"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>

<%
    Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
    List<Match> campeonatos = new ArrayList<>();
    boolean userIsLogged = (usuarioSesion != null);

    String mensaje = (String) request.getAttribute("Mensaje");
    String tipo = (String) request.getAttribute("TipoAlerta");

    // Si no viene en el request, revisamos la sesión
    if (mensaje == null && session.getAttribute("mensajeFlash") != null) {
        mensaje = (String) session.getAttribute("mensajeFlash");
        tipo = (String) session.getAttribute("tipoMensaje");
        // Borrar de la sesión para que se vea una sola vez
        session.removeAttribute("mensajeFlash");
        session.removeAttribute("tipoMensaje");
    }

    String claseCSS = "alert-messsage-bar";
    if ("danger".equals(tipo)) {
        claseCSS = "alert-error-bar"; // Rojo
    }

    // Cargar los datos
    if (userIsLogged) {
        Long userId = Long.valueOf(usuarioSesion.getIdUsers());
        TournamentDao dao = new TournamentDao();
        campeonatos = dao.findChampionshipsWonByUser(userId);
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Campeonatos</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/campeonatos.css" />
        <link rel="stylesheet" href="css/footer.css">
        <style>
            .team-logo img {
                width: 100%;
                height: 100%;
                object-fit: contain;
            }
            .empty-state {
                grid-column: 1 / -1;
                text-align: center;
                padding: 40px;
                color: #666;
            }
        </style>
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />

        <main class="page">

            <% if (mensaje != null) {%>
            <div class="<%= claseCSS%>">
                <%= mensaje%>
            </div>
            <% } %>

            <div class="pr-head">
                <h2 class="pr-title">HISTORIAL DE CAMPEONATOS</h2>
            </div>

            <section id="campeonatos-usuario" class="match-section">

                <% if (!userIsLogged) { %>
                <div class="alert-error-bar">
                    <h4>Inicia sesión para ver tu historial de campeonatos.</h4>
                </div>
                <% } else { %>

                <div class="match-grid">

                    <%
                        if (campeonatos != null && !campeonatos.isEmpty()) {
                            for (Match m : campeonatos) {
                                // Lógica para logos y fecha
                                String homeLogo = (m.getHome().getLogo() != null && !m.getHome().getLogo().isEmpty()) ? m.getHome().getLogo() : "public/assets/EquipoSinLogo.png";
                                String awayLogo = (m.getAway().getLogo() != null && !m.getAway().getLogo().isEmpty()) ? m.getAway().getLogo() : "public/assets/EquipoSinLogo.png";
                                String fecha = (m.getPlayed_at() != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(m.getPlayed_at()) : "";
                    %>

                    <article class="match-card champion-card">
                        <div class="team">
                            <div class="team-name"><%= m.getHome().getName()%></div>
                            <div class="team-logo">
                                <img src="<%= homeLogo%>" alt="logo" onerror="this.src='public/assets/EquipoSinLogo.png';">
                            </div>
                        </div>

                        <div class="center">
                            <div class="champion-label">CAMPEÓN</div>

                            <div class="vs">FINAL</div>
                            <div class="score"><%= m.getHomeScore()%>-<%= m.getAwayScore()%></div>
                            <div style="font-size: 0.9rem; margin-top: 5px; color: #333; font-weight: 800;"><%= fecha%></div>
                        </div>

                        <div class="team">
                            <div class="team-name"><%= m.getAway().getName()%></div>
                            <div class="team-logo">
                                <img src="<%= awayLogo%>" alt="logo" onerror="this.src='public/assets/EquipoSinLogo.png';">
                            </div>
                        </div>
                    </article>

                    <%
                        }
                    } else {
                    %>
                    <div class="empty-state">
                        <h4>Aún no has ganado ningún campeonato.</h4>
                        <p>¡Sigue intentándolo!</p>
                    </div>
                    <% } %>

                </div>
                <% }%>
            </section>
        </main>

        <footer class="footer">
            <div class="container-logo">
                <img src="public/logo/image0.png" alt="">
            </div>
            <div class="container-texto">
                <nav>
                    <ul>
                        <li><a href="frontpage.jsp">HOME</a></li>
                        <li><a href="partidos.jsp">PARTIDOS</a></li>
                        <li><a href="campeonatos.jsp">CAMPEONATOS</a></li>
                        <li><a href="miperfil.jsp">PERFIL</a></li>
                        <li><a href="parati.jsp">PARA TI</a></li>
                    </ul>
                </nav>
            </div>
            <div class="container-texto-date">
                <p>©</p>
                <p id="date-footer"></p>
                <p>Futsy League. Todos los derechos reservados</p>
            </div>
        </footer>
    </body>
    <script src="js/footer.js"></script>
</html>