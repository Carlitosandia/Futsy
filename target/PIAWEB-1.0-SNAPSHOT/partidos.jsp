<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.piaweb.dao.TournamentDao"%>
<%@ page import="com.mycompany.piaweb.modelos.Match"%>
<%@ page import="com.mycompany.piaweb.modelos.Users"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.ArrayList"%>

<%!
    // --- FUNCIÓN AUXILIAR PARA AGRUPAR POR TORNEO ---
    public Map<Long, List<Match>> groupMatchesByTournament(List<Match> matches) {
        Map<Long, List<Match>> map = new LinkedHashMap<>();
        if (matches == null) return map;
        
        for (Match m : matches) {
            Long tId = m.getTournamentId();
            if (!map.containsKey(tId)) {
                map.put(tId, new ArrayList<>());
            }
            map.get(tId).add(m);
        }
        return map;
    }
%>

<%
    Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
    Long userId = null;

    if (usuarioSesion != null) {
        userId = Long.valueOf(usuarioSesion.getIdUsers());
    }

    List<Match> mundialMatches = null;
    List<Match> generalMatches = null;
    
    Map<Long, List<Match>> mundialGrouped = new LinkedHashMap<>();
    Map<Long, List<Match>> generalGrouped = new LinkedHashMap<>();

    if (userId != null) {
        TournamentDao dao = new TournamentDao();
        mundialMatches = dao.findUserWorldCupMatches(userId);
        generalMatches = dao.findUserGeneralMatches(userId);
        
        mundialGrouped = groupMatchesByTournament(mundialMatches);
        generalGrouped = groupMatchesByTournament(generalMatches);
    }
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Partidos</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/partidos.css" />
        <link rel="stylesheet" href="css/footer.css">
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />
        <main class="page">
            <div class="pr-head">
                <h2 class="pr-title">PARTIDOS Y RESULTADOS</h2>
                <div class="pr-actions">
                    <button class="btn btn-mundial is-active">TORNEO MUNDIAL</button>
                    <button class="btn btn-general">TORNEO GENERAL</button>
                </div>
            </div>

            <div class="pr-dates">
                <button class="nav-btn" id="prev">‹</button>
                <div class="dates-scroll" id="dates"></div>
                <button class="nav-btn" id="next">›</button>
            </div>

            <div class="match-meta">
                <div class="match-date"></div>
            </div>

            <!-- SECCION MUNDIAL -->
            <section id="partidos-mundial" class="match-section">
                <% 
                if (!mundialGrouped.isEmpty()) {
                    for (Map.Entry<Long, List<Match>> entry : mundialGrouped.entrySet()) {
                        Long torneoId = entry.getKey();
                        List<Match> partidosDelTorneo = entry.getValue();
                %>
                    <div class="tournament-container">
                        <!-- Título del torneo -->
                        <div class="tournament-header">Torneo Mundial #<%= torneoId %></div>
                        
                        <div class="match-grid">
                            <% 
                            for (Match m : partidosDelTorneo) {
                                String homeLogo = (m.getHome().getLogo() != null && !m.getHome().getLogo().isEmpty()) ? m.getHome().getLogo() : "public/assets/EquipoSinLogo.png";
                                String awayLogo = (m.getAway().getLogo() != null && !m.getAway().getLogo().isEmpty()) ? m.getAway().getLogo() : "public/assets/EquipoSinLogo.png";
                                String matchDate = (m.getPlayed_at() != null) ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(m.getPlayed_at()) : "";
                                
                                // --- LOGICA DE ESTILOS POR FASE ---
                                // CORRECCIÓN: Agregamos .toString() antes de .toUpperCase()
                                String phaseRaw = (m.getPhase() != null) ? m.getPhase().toString().toUpperCase() : "";
                                String phaseClass = ""; // Clase por defecto
                                
                                if (phaseRaw.contains("QUARTER") || phaseRaw.contains("CUARTOS")) {
                                    phaseClass = "card-quarter";
                                } else if (phaseRaw.contains("SEMI")) {
                                    phaseClass = "card-semi";
                                } else if (phaseRaw.contains("FINAL")) {
                                    phaseClass = "card-final";
                                }
                            %>
                                <!-- Agregamos la variable phaseClass aqui -->
                                <article class="match-card <%= phaseClass %>" data-date="<%= matchDate %>">
                                    <div class="team">
                                        <div class="team-name"><%= m.getHome().getName() %></div>
                                        <div class="team-logo">
                                            <img src="<%= homeLogo %>" alt="logo" style="width: 40px; height: 40px; object-fit: contain;" onerror="this.src='public/assets/EquipoSinLogo.png';">
                                        </div>
                                    </div>
                                    <div class="center">
                                        <div class="vs">vs</div>
                                        <div class="score"><%= m.getHomeScore() %> – <%= m.getAwayScore() %></div>
                                        <div style="font-size: 0.7em; color: #888; margin-top: 4px; text-transform: uppercase;"><%= m.getPhase() %></div>
                                    </div>
                                    <div class="team">
                                        <div class="team-name"><%= m.getAway().getName() %></div>
                                        <div class="team-logo">
                                            <img src="<%= awayLogo %>" alt="logo" style="width: 40px; height: 40px; object-fit: contain;" onerror="this.src='public/assets/EquipoSinLogo.png';">
                                        </div>
                                    </div>
                                </article>
                            <% } %>
                        </div>
                    </div>
                <% 
                    } 
                } else { 
                %>
                    <div class="empty-state">
                        <img src="public/assets/EquipoSinLogo.png" alt="Sin partidos" style="width: 60px; opacity: 0.5; margin-bottom: 15px;">
                        <h4>No hay partidos de Mundial</h4>
                    </div>
                <% } %>
            </section>

            <!-- SECCION GENERAL -->
            <section id="partidos-general" class="match-section" hidden>
                <% 
                if (!generalGrouped.isEmpty()) {
                    for (Map.Entry<Long, List<Match>> entry : generalGrouped.entrySet()) {
                        Long torneoId = entry.getKey();
                        List<Match> partidosDelTorneo = entry.getValue();
                %>
                    <div class="tournament-container">
                        <div class="tournament-header">Torneo General #<%= torneoId %></div>
                        
                        <div class="match-grid">
                            <% 
                            for (Match m : partidosDelTorneo) {
                                String homeLogo = (m.getHome().getLogo() != null && !m.getHome().getLogo().isEmpty()) ? m.getHome().getLogo() : "public/assets/EquipoSinLogo.png";
                                String awayLogo = (m.getAway().getLogo() != null && !m.getAway().getLogo().isEmpty()) ? m.getAway().getLogo() : "public/assets/EquipoSinLogo.png";
                                String matchDate = (m.getPlayed_at() != null) ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(m.getPlayed_at()) : "";
                                
                                // --- LOGICA DE ESTILOS POR FASE (Repetida para general) ---
                                // CORRECCIÓN: Agregamos .toString() antes de .toUpperCase()
                                String phaseRaw = (m.getPhase() != null) ? m.getPhase().toString().toUpperCase() : "";
                                String phaseClass = ""; 
                                
                                if (phaseRaw.contains("QUARTER") || phaseRaw.contains("CUARTOS")) {
                                    phaseClass = "card-quarter";
                                } else if (phaseRaw.contains("SEMI")) {
                                    phaseClass = "card-semi";
                                } else if (phaseRaw.contains("FINAL")) {
                                    phaseClass = "card-final";
                                }
                            %>
                                <article class="match-card <%= phaseClass %>" data-date="<%= matchDate %>">
                                    <div class="team">
                                        <div class="team-name"><%= m.getHome().getName() %></div>
                                        <div class="team-logo">
                                            <img src="<%= homeLogo %>" alt="logo" style="width: 40px; height: 40px; object-fit: contain;" onerror="this.src='public/assets/EquipoSinLogo.png';">
                                        </div>
                                    </div>
                                    <div class="center">
                                        <div class="vs">vs</div>
                                        <div class="score"><%= m.getHomeScore() %> – <%= m.getAwayScore() %></div>
                                        <div style="font-size: 0.7em; color: #888; margin-top: 4px; text-transform: uppercase;"><%= m.getPhase() %></div>
                                    </div>
                                    <div class="team">
                                        <div class="team-name"><%= m.getAway().getName() %></div>
                                        <div class="team-logo">
                                            <img src="<%= awayLogo %>" alt="logo" style="width: 40px; height: 40px; object-fit: contain;" onerror="this.src='public/assets/EquipoSinLogo.png';">
                                        </div>
                                    </div>
                                </article>
                            <% } %>
                        </div>
                    </div>
                <% 
                    } 
                } else { 
                %>
                    <div class="empty-state">
                        <img src="public/assets/EquipoSinLogo.png" alt="Sin partidos" style="width: 60px; opacity: 0.5; margin-bottom: 15px;">
                        <h4>No hay partidos generales</h4>
                    </div>
                <% } %>
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
                <p>Futsy League.Todos los derechos reservados</p>
            </div>
        </footer>
    </body>
    <script src="js/footer.js"></script>
    <script src="js/partidos.js"></script>
</html>