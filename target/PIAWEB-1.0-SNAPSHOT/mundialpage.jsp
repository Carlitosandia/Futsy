<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.piaweb.dao.TournamentDao"%>
<%@ page import="com.mycompany.piaweb.modelos.Match"%>
<%@ page import="java.util.List"%>

<%
    String tidParam = request.getParameter("tournamentId");
    List<Match> quarterfinals = null;
    List<Match> semifinals = null;
    Match finalMatch = null;

    TournamentDao dao = new TournamentDao();
    Long tournamentId = null;

    if (tidParam != null) {
        try {
            tournamentId = Long.parseLong(tidParam);
        } catch (NumberFormatException e) {
            // ignore
        }
    } else {
        tournamentId = dao.findLatestSystemWorldCupTournamentId();
    }

    if (tournamentId != null) {
        List<Match> allMatches = dao.findMatchesByTournament(tournamentId);
        quarterfinals = new java.util.ArrayList<>();
        semifinals = new java.util.ArrayList<>();

        for (Match m : allMatches) {
            switch (m.getPhase()) {
                case QUARTERFINAL: quarterfinals.add(m); break;
                case SEMIFINAL:    semifinals.add(m);    break;
                case FINAL:        finalMatch = m;       break;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mundial - Bracket</title>
    <link rel="stylesheet" href="boostrap/css/bootstrap.css">
    <link rel="stylesheet" href="css/applayout.css">
    <!-- CSS Específico del Mundial Premium -->
    <link rel="stylesheet" href="css/mundialpage.css"> 
    <link rel="stylesheet" href="css/footer.css">
</head>

<body class="individual-page">
    
    <jsp:include page="header.jsp" />
    <jsp:include page="navbar.jsp" />
    
    <div class="individual-canvas">
        <!-- Encabezados estilo nuevo -->
        <header class="tournament-header">
            <div class="sub-headline" id="idSecondHeadline">FOOTBALL SOCCER</div>
            <h1 class="main-headline" id="idFirstHeadlineM">MUNDIAL</h1>
        </header>

        <!-- Grid Principal (bracket-grid) -->
        <section class="bracket-grid">
            
            <!-- === COLUMNA 1: CUARTOS IZQUIERDA === -->
            <div class="column-stack">
                <div class="round-label">Cuartos de Final</div>
                <% 
                for (int i = 0; i < 2; i++) { 
                    Match m = (quarterfinals != null && quarterfinals.size() > i) ? quarterfinals.get(i) : null;
                %>
                    <div class="match-wrapper">
                        <div class="match-card">
                            <!-- Local -->
                            <div class="team-row <%= (m != null && m.getHomeScore() > m.getAwayScore()) ? "winner-row" : "" %>">
                                <span class="team-name"><%= (m != null) ? m.getHome().getName() : "Pendiente" %></span>
                                <span class="team-score"><%= (m != null) ? m.getHomeScore() : "-" %></span>
                            </div>
                            <!-- Visitante -->
                            <div class="team-row <%= (m != null && m.getAwayScore() > m.getHomeScore()) ? "winner-row" : "" %>">
                                <span class="team-name"><%= (m != null) ? m.getAway().getName() : "Pendiente" %></span>
                                <span class="team-score"><%= (m != null) ? m.getAwayScore() : "-" %></span>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>

            <!-- === COLUMNA 2: SEMIFINAL IZQUIERDA (Centrada Verticalmente) === -->
            <div class="column-stack column-center-vertical">
                <div class="round-label">Semifinal</div>
                <% Match sm1 = (semifinals != null && semifinals.size() > 0) ? semifinals.get(0) : null; %>
                <div class="match-wrapper">
                    <div class="match-card">
                        <div class="team-row <%= (sm1 != null && sm1.getHomeScore() > sm1.getAwayScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (sm1 != null) ? sm1.getHome().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (sm1 != null) ? sm1.getHomeScore() : "-" %></span>
                        </div>
                        <div class="team-row <%= (sm1 != null && sm1.getAwayScore() > sm1.getHomeScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (sm1 != null) ? sm1.getAway().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (sm1 != null) ? sm1.getAwayScore() : "-" %></span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- === COLUMNA 3 (CENTRO): CAMPEON Y FINAL === -->
            <div class="column-center-main">
                
                <!-- Sección Campeón -->
                <div class="champion-section">
                    <div class="round-label-gold">Campeón del Mundo</div>
                    <div class="champion-card">
                        <div class="champion-name">
                            <% if (finalMatch != null && finalMatch.getWinner() != null) { %>
                                <%= finalMatch.getWinner().getName() %>
                            <% } else { out.print("?"); } %>
                        </div>
                    </div>
                </div>

                <!-- Sección Gran Final -->
                <div class="match-wrapper">
                    <div class="round-label">Gran Final</div>
                    <div class="match-card match-card-final">
                        <div class="team-row <%= (finalMatch != null && finalMatch.getHomeScore() > finalMatch.getAwayScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (finalMatch != null) ? finalMatch.getHome().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (finalMatch != null) ? finalMatch.getHomeScore() : "-" %></span>
                        </div>
                        <div class="team-row <%= (finalMatch != null && finalMatch.getAwayScore() > finalMatch.getHomeScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (finalMatch != null) ? finalMatch.getAway().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (finalMatch != null) ? finalMatch.getAwayScore() : "-" %></span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- === COLUMNA 4: SEMIFINAL DERECHA === -->
            <div class="column-stack column-center-vertical">
                <div class="round-label">Semifinal</div>
                <% Match sm2 = (semifinals != null && semifinals.size() > 1) ? semifinals.get(1) : null; %>
                <div class="match-wrapper">
                    <div class="match-card">
                        <div class="team-row <%= (sm2 != null && sm2.getHomeScore() > sm2.getAwayScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (sm2 != null) ? sm2.getHome().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (sm2 != null) ? sm2.getHomeScore() : "-" %></span>
                        </div>
                        <div class="team-row <%= (sm2 != null && sm2.getAwayScore() > sm2.getHomeScore()) ? "winner-row" : "" %>">
                            <span class="team-name"><%= (sm2 != null) ? sm2.getAway().getName() : "Pendiente" %></span>
                            <span class="team-score"><%= (sm2 != null) ? sm2.getAwayScore() : "-" %></span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- === COLUMNA 5: CUARTOS DERECHA === -->
            <div class="column-stack">
                <div class="round-label">Cuartos de Final</div>
                <% 
                for (int i = 2; i < 4; i++) { 
                    Match m = (quarterfinals != null && quarterfinals.size() > i) ? quarterfinals.get(i) : null;
                %>
                    <div class="match-wrapper">
                        <div class="match-card">
                            <div class="team-row <%= (m != null && m.getHomeScore() > m.getAwayScore()) ? "winner-row" : "" %>">
                                <span class="team-name"><%= (m != null) ? m.getHome().getName() : "Pendiente" %></span>
                                <span class="team-score"><%= (m != null) ? m.getHomeScore() : "-" %></span>
                            </div>
                            <div class="team-row <%= (m != null && m.getAwayScore() > m.getHomeScore()) ? "winner-row" : "" %>">
                                <span class="team-name"><%= (m != null) ? m.getAway().getName() : "Pendiente" %></span>
                                <span class="team-score"><%= (m != null) ? m.getAwayScore() : "-" %></span>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>

        </section>
    </div>

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

    <script src="js/footer.js"></script>
</body>
</html>