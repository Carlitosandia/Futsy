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
            // Manejo de error silencioso o log
        }
    }

    if (tournamentId != null) {
        List<Match> allMatches = dao.findMatchesByTournament(tournamentId);
        quarterfinals = new java.util.ArrayList<>();
        semifinals = new java.util.ArrayList<>();

        for (Match m : allMatches) {
            switch (m.getPhase()) {
                case QUARTERFINAL: quarterfinals.add(m); break;
                case SEMIFINAL: semifinals.add(m); break;
                case FINAL: finalMatch = m; break;
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Liga Individual - Bracket Premium</title>
  
  <!-- Bootstrap y estilos base -->
  <link rel="stylesheet" href="boostrap/css/bootstrap.css">
  <link rel="stylesheet" href="css/applayout.css">
  <link rel="stylesheet" href="css/footer.css">
  
  <!-- Estilos Específicos de la Página Individual -->
  <link rel="stylesheet" href="css/individualpage.css">
</head>

<body class="frontpage-bg individual-page">

  <jsp:include page="header.jsp" />
  <jsp:include page="navbar.jsp" />

  <main class="page">
    <div class="individual-canvas">
      
      <!-- Encabezados -->
      <header class="tournament-header">
          <h2 class="sub-headline">FOOTBALL SOCCER</h2>
          <h1 class="main-headline">LIGA INDIVIDUAL</h1>
      </header>

      <!-- Botón de Acción Principal -->
      <div class="action-container">
          <form action="SimulateIndividualTournamentServlet" method="post">
            <button type="submit" class="btn-premium-simulate">
                Simular nuevo torneo
            </button>
          </form>
      </div>

      <!-- Grid del Torneo -->
      <section class="bracket-grid">
        
        <!-- COLUMNA 1: CUARTOS IZQUIERDA -->
        <div class="column-stack">
          <% for (int i = 0; i < 2; i++) { 
               Match m = (quarterfinals != null && quarterfinals.size() > i) ? quarterfinals.get(i) : null;
          %>
            <div class="match-wrapper">
                <span class="round-label">Cuartos</span>
                <div class="match-card">
                    <!-- Equipo Local -->
                    <div class="team-row <%= (m != null && m.getHomeScore() > m.getAwayScore()) ? "winner-row" : "" %>">
                        <span class="team-name"><%= (m != null) ? m.getHome().getName() : "Pendiente" %></span>
                        <span class="team-score"><%= (m != null) ? m.getHomeScore() : "-" %></span>
                    </div>
                    <!-- Equipo Visitante -->
                    <div class="team-row <%= (m != null && m.getAwayScore() > m.getHomeScore()) ? "winner-row" : "" %>">
                        <span class="team-name"><%= (m != null) ? m.getAway().getName() : "Pendiente" %></span>
                        <span class="team-score"><%= (m != null) ? m.getAwayScore() : "-" %></span>
                    </div>
                </div>
            </div>
          <% } %>
        </div>

        <!-- COLUMNA 2: SEMIFINAL IZQUIERDA -->
        <div class="column-stack column-center-vertical">
          <% Match sm1 = (semifinals != null && semifinals.size() > 0) ? semifinals.get(0) : null; %>
          <div class="match-wrapper">
              <span class="round-label">Semifinal</span>
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

        <!-- COLUMNA 3: FINAL Y CAMPEÓN (CENTRO) -->
        <div class="column-center-main">
          
          <!-- Tarjeta del Campeón -->
          <div class="champion-section">
            <span class="round-label-gold">CAMPEÓN</span>
            <div class="champion-card">
                <div class="champion-name">
                    <% if (finalMatch != null && finalMatch.getWinner() != null) { %>
                        <%= finalMatch.getWinner().getName() %>
                    <% } else { %>
                        ?
                    <% } %>
                </div>
            </div>
          </div>

          <!-- Gran Final -->
          <div class="match-wrapper final-match-wrapper">
              <span class="round-label">Gran Final</span>
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

        <!-- COLUMNA 4: SEMIFINAL DERECHA -->
        <div class="column-stack column-center-vertical">
          <% Match sm2 = (semifinals != null && semifinals.size() > 1) ? semifinals.get(1) : null; %>
          <div class="match-wrapper">
              <span class="round-label">Semifinal</span>
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

        <!-- COLUMNA 5: CUARTOS DERECHA -->
        <div class="column-stack">
          <% for (int i = 2; i < 4; i++) { 
               Match m = (quarterfinals != null && quarterfinals.size() > i) ? quarterfinals.get(i) : null;
          %>
            <div class="match-wrapper">
                <span class="round-label">Cuartos</span>
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
  </main>
  
  <jsp:include page="footer.jsp" />
</body>
</html>