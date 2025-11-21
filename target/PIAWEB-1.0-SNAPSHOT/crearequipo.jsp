<%@page import="com.mycompany.piaweb.modelos.TeamsMundial"%> <%@page import="com.mycompany.piaweb.modelos.Teams"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.mycompany.piaweb.modelos.Players"%> 
<%@page import="com.mycompany.piaweb.modelos.Countries"%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Crear / Editar equipo</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/crearequipo.css" />
        <link rel="stylesheet" href="css/footer.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />

        <main class="page">

            <%
                //Recuperamos la lista de jugadores
                List<Players> listaJugadores = (List<Players>) request.getAttribute("playersList");
                if (listaJugadores == null) listaJugadores = new ArrayList<>();
                
                List<Countries> listaCountries = (List<Countries>) request.getAttribute("countriesList");
                if (listaCountries == null) listaCountries = new ArrayList<>();
                
                // Recuperamos el equipo general
                Teams equipoG = (Teams) request.getAttribute("equipoGeneral");
                boolean existeGeneral = (equipoG != null);
                
                // Recuperamos el equipo mundial
                TeamsMundial equipoM = (TeamsMundial) request.getAttribute("equipoMundial");
                boolean existeMundial = (equipoM != null);
            %>

            <div class="tabs">
                <button class="tab tab-general is-active" onclick="switchTab('general')">Equipo general</button>
                <button class="tab tab-mundial" onclick="switchTab('mundial')">Equipo mundial</button>
            </div>
            
            <%-- MANEJO DE ALERTAS --%>
            <%
                String mensaje = (String) request.getAttribute("Mensaje");
                String tipo = (String) request.getAttribute("TipoAlerta");
                String claseCSS = "alert-messsage-bar"; 
                if ("danger".equals(tipo)) {
                    claseCSS = "alert-error-bar";
                }
                if (mensaje != null) {
            %>
            <div class="<%= claseCSS%>">
                <%= mensaje%>
            </div>
            <% } %>

            <form action="CrearEquipoServlet" method="POST" enctype="multipart/form-data" id="form-general">
                <input type="hidden" name="tipoEquipo" value="general">
                
                <% if (existeGeneral) { %>
                    <input type="hidden" name="teamId" value="<%= equipoG.getId() %>">
                    <input type="hidden" name="logoActual" value="<%= equipoG.getLogo() %>">
                <% } %>
                
                <input type="hidden" name="jugadoresSeleccionados" id="input-ids-general">

                <section id="equipo-general" class="workarea">
                    <div class="col col-sticky">
                        <h2 class="h-title"><%= existeGeneral ? "Editar Equipo" : "Crear Equipo" %></h2>
                        
                        <div class="input-group-custom">
                            <label class="label">Nombre del club</label>
                            <input class="input" type="text" name="nombreEquipo" 
                                   value="<%= existeGeneral ? equipoG.getName() : "" %>"
                                   placeholder="Ej. Pollitos FC" required />
                        </div>

                        <div class="panel" id="panel-jugadores-general">
                            <div class="panel-header">
                                <div class="panel-title">SELECCIONADOS</div>
                                <div class="counter-badge" id="contador-general">0/11</div>
                            </div>
                            <div class="selected-list-container"></div>
                        </div>

                        <button class="panel-crear" type="submit">
                            <%= existeGeneral ? "Guardar Cambios" : "Confirmar Equipo" %> <i class="fas fa-check-circle"></i>
                        </button>
                    </div>

                    <div class="col big-board">
                        <div class="filters-row">
                            <div class="filter-group">
                                <label class="label" for="posicion-general">Posición</label>
                                <select id="posicion-general" class="select-modern" onchange="filtrar('general')">
                                    <option value="TODAS" selected>Todas las posiciones</option>
                                    <option value="GK">Porteros (GK)</option>
                                    <option value="DF">Defensas (DF)</option>
                                    <option value="MF">Medios (MF)</option>
                                    <option value="FW">Delanteros (FW)</option>
                                </select>
                            </div>

                            <div class="logo-wrapper">
                                <img id="logo-preview-img-general" class="logo-preview-circle" 
                                     src="<%= existeGeneral ? equipoG.getLogo() : "#" %>" 
                                     alt="Preview" 
                                     <%= existeGeneral ? "" : "hidden" %> />

                                <label class="logo-upload-btn">
                                    <i class="fas fa-camera"></i> <%= existeGeneral ? "Cambiar Logo" : "Subir Logo" %>
                                    <input id="logoInputGeneral" type="file" name="logoEquipo" accept="image/*" onchange="previewLogo(event, 'general')">
                                </label>
                            </div>
                        </div>

                        <div id="grid-general" class="players-grid">
                            <% for (Players p : listaJugadores) {%>
                            <div class="player-card-item" 
                                 id="card-general-<%= p.getId()%>"
                                 data-id="<%= p.getId()%>" 
                                 data-pos="<%= p.getPosition()%>" 
                                 data-country="<%= p.getCountryName()%>"
                                 data-name="<%= p.getName()%>">

                                <div class="player-avatar">
                                    <img src="https://cdn-icons-png.flaticon.com/512/166/166344.png" alt="Player">
                                </div>

                                <div class="card-content">
                                    <div>
                                        <strong class="player-name"><%= p.getName()%></strong>
                                        <span class="player-rating"><%= p.getOverall()%></span>
                                    </div>
                                    <div class="details-row">
                                        <span class="pos-badge"><%= p.getPosition()%></span>
                                        <span>•</span>
                                        <span><%= p.getCountryName()%></span>
                                    </div>
                                </div>

                                <div class="player-action">
                                    <button type="button" class="btn-add-large" onclick="toggleSeleccion('<%= p.getId()%>', 'general')">
                                        AGREGAR
                                    </button>
                                </div>
                            </div>
                            <% } %>
                            <% if (listaJugadores.isEmpty()) { %>
                            <div class="empty-state"><p>No se encontraron jugadores.</p></div>
                            <% } %>
                        </div>
                    </div>
                </section>
            </form>

            <form action="CrearEquipoMundialServlet" method="POST" id="form-mundial">
                <input type="hidden" name="tipoEquipo" value="mundial">
                
                <%-- INPUTS OCULTOS PARA EDICIÓN MUNDIAL --%>
                <% if (existeMundial) { %>
                    <input type="hidden" name="teamId" value="<%= equipoM.getId() %>">
                <% } %>
                
                <input type="hidden" name="jugadoresSeleccionados" id="input-ids-mundial">

                <section id="equipo-mundial" class="workarea" hidden>
                    <div class="col col-sticky">
                        <h2 class="h-title"><%= existeMundial ? "Editar Selección" : "Selección Mundial" %></h2>
                        <div class="input-group-custom">
                            <label class="label">Nombre de Selección</label>
                            <input class="input" type="text" name="nombreEquipo" 
                                   value="<%= existeMundial ? equipoM.getDisplay_name() : "" %>"
                                   placeholder="Ej. Selección Global" required />
                        </div>

                        <div class="panel" id="panel-jugadores-mundial">
                            <div class="panel-header">
                                <div class="panel-title">SELECCIONADOS</div>
                                <div class="counter-badge" id="contador-mundial">0/11</div>
                            </div>
                            <div class="selected-list-container"></div>
                        </div>

                        <button class="panel-crear" type="submit">
                            <%= existeMundial ? "Guardar Cambios" : "Confirmar Selección" %> <i class="fas fa-check-circle"></i>
                        </button>
                    </div>

                    <div class="col big-board">
                        <div class="filters-row">
                            <div class="filter-group">
                                <label class="label">Posición</label>
                                <select id="posicion-mundial" class="select-modern" onchange="filtrar('mundial')">
                                    <option value="TODAS">Todas</option>
                                    <option value="GK">Porteros</option>
                                    <option value="DF">Defensas</option>
                                    <option value="MF">Medios</option>
                                    <option value="FW">Delanteros</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label class="label">País</label>
                                <select id="pais-mundial" class="select-modern" name="paisSeleccionado" onchange="filtrar('mundial')">
                                    <option value="TODOS">Todos</option>
                                    <% for (Countries c : listaCountries) { 
                                        // Pre-seleccionar país si estamos editando
                                        boolean selected = existeMundial && (equipoM.getCountry_id() == c.getId());
                                    %>
                                        <option value="<%= c.getId() %>" <%= selected ? "selected" : "" %>>
                                            <%= c.getName() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>
                        </div>

                        <div id="grid-mundial" class="players-grid">
                            <% for (Players p : listaJugadores) {%>
                            <div class="player-card-item" 
                                 id="card-mundial-<%= p.getId()%>"
                                 data-id="<%= p.getId()%>" 
                                 data-pos="<%= p.getPosition()%>" 
                                 data-country="<%= p.getNationality_id() %>"
                                 data-name="<%= p.getName()%>">

                                <div class="player-avatar">
                                    <img src="https://cdn-icons-png.flaticon.com/512/166/166344.png" alt="Player">
                                </div>

                                <div class="card-content">
                                    <div>
                                        <strong class="player-name"><%= p.getName()%></strong>
                                        <span class="player-rating"><%= p.getOverall()%></span>
                                    </div>
                                    <div class="details-row">
                                        <span class="pos-badge"><%= p.getPosition()%></span>
                                        <span>•</span>
                                        <span><%= p.getCountryName()%></span>
                                    </div>
                                </div>

                                <div class="player-action">
                                    <button type="button" class="btn-add-large" onclick="toggleSeleccion('<%= p.getId()%>', 'mundial')">
                                        AGREGAR
                                    </button>
                                </div>
                            </div>
                            <% }%>
                        </div>
                    </div>
                </section>
            </form>

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

        </main>

        <script>
            function switchTab(tabName) {
                document.getElementById('equipo-general').hidden = true;
                document.getElementById('equipo-mundial').hidden = true;
                document.querySelector('.tab-general').classList.remove('is-active');
                document.querySelector('.tab-mundial').classList.remove('is-active');
                document.getElementById('equipo-' + tabName).hidden = false;
                document.querySelector('.tab-' + tabName).classList.add('is-active');
            }
            
            // Función corregida para manejar la preview del logo
            function previewLogo(event, tipo) {
                const input = event.target;
                // Si 'tipo' es 'general', el ID es 'logo-preview-img-general' (ajusté el ID arriba para evitar conflictos)
                // Si usaste el ID original 'logo-preview-img', ajusta esto.
                // En este código he cambiado el ID en el HTML a 'logo-preview-img-general'
                const previewImg = document.getElementById('logo-preview-img-' + tipo) || document.getElementById('logo-preview-img');

                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        previewImg.src = e.target.result;
                        previewImg.hidden = false;
                    }
                    reader.readAsDataURL(input.files[0]);
                }
            }
        </script>

        <script src="js/footer.js"></script>
        <script src="js/crearequipo.js"></script>

        <script>
            window.addEventListener('DOMContentLoaded', () => {
                // 1. PRE-CARGAR EQUIPO GENERAL
                <% 
                    if (existeGeneral && equipoG.getPlayerIds() != null && !equipoG.getPlayerIds().isEmpty()) {
                        for (Integer pid : equipoG.getPlayerIds()) { 
                %>
                            toggleSeleccion('<%= pid %>', 'general');
                <%      }
                    } 
                %>
                
                // 2. PRE-CARGAR EQUIPO MUNDIAL
                <% 
                    if (existeMundial && equipoM.getPlayerIds() != null && !equipoM.getPlayerIds().isEmpty()) {
                        for (Integer pid : equipoM.getPlayerIds()) { 
                %>
                            toggleSeleccion('<%= pid %>', 'mundial');
                <%      }
                    } 
                %>
            });
        </script>
    </body>
</html>