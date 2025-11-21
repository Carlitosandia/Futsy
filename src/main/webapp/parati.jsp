<%-- 
    Document   : parati
    Created on : 21 oct 2025, 20:24:42
    Author     : Sofia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.piaweb.modelos.Post"%>
<%@page import="com.mycompany.piaweb.modelos.PostImage"%>
<%@page import="com.mycompany.piaweb.modelos.Tags"%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Para ti</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css"><!-- ojo: carpeta -->
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/footer.css">
        <link rel="stylesheet" href="css/parati.css">
    </head>

    <body class="parati-bg">
        <!-- Header / Navbar -->
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />

        <!-- Contenido de la página -->
        <main class="page">
            <%-- MANEJO DE ALERTAS (FLASH MESSAGES) --%>
            <%
                String mensaje = (String) request.getAttribute("Mensaje");
                String tipo = (String) request.getAttribute("TipoAlerta");
                if (mensaje == null) {
                    if (session.getAttribute("mensajeFlash") != null) {
                        mensaje = (String) session.getAttribute("mensajeFlash");
                        tipo = (String) session.getAttribute("tipoMensaje");
                        session.removeAttribute("mensajeFlash");
                        session.removeAttribute("tipoMensaje");
                    }
                }

                String claseCSS = "alert-messsage-bar";
                if ("danger".equals(tipo)) {
                    claseCSS = "alert-error-bar";
                } else if ("success".equals(tipo)) {
                    claseCSS = "alert-success-bar";
                }
                if (mensaje != null) {
            %>
            <div class="<%= claseCSS%>" style="text-align: center; margin: 10px 0; padding: 10px; border-radius: 5px;">
                <%= mensaje%>
            </div>
            <% } %>
            <div class="content-grid">

                <section class="feed">
                    <%
                        List<Tags> tags = (List<Tags>) request.getAttribute("tags");
                        String feedTitle = (String) request.getAttribute("feedTitle");
                    %> 
                    <div id="idParaTiContainer" class="bar">
                        <h3 id="idParaTiText">
                            <%= (feedTitle != null) ? feedTitle : "PARA TI"%>
                        </h3>
                        <img id="idBallImage" src="https://cdn-icons-png.flaticon.com/512/4498/4498011.png" alt="">
                    </div>

                    <!-- Formulario de publicación -->
                    <article class="card" id="postFormContainer">
                        <form id="postForm" action="PostServlet" method="POST" enctype="multipart/form-data">
                            <input type="text" name="title" id="postTitle" 
                                   placeholder="Añade un título a tu publicación" required>
                            <textarea name="description" id="postDescription" placeholder="Comparte tus triunfos" required></textarea>
                            <input type="file" name="image" id="postImage" accept="image/*">

                            <% if (tags != null && !tags.isEmpty()) { %>
                            <div class="tags-select-container">
                                <p class="tags-label">Selecciona tags para tu publicación:</p>
                                <div class="tags-list">
                                    <% for (Tags tag : tags) {%>
                                    <label class="tag-pill">
                                        <input type="checkbox"
                                               name="tagIds"
                                               value="<%= tag.getId()%>">
                                        #<%= (tag.getSlug() != null && !tag.getSlug().isEmpty())
                                                ? tag.getTitle()
                                                : "No se encontro este tag"%>
                                    </label>
                                    <% } %>
                                </div>
                            </div>
                            <% } else { %>
                            <p>No hay tags disponibles.</p>
                            <% } %>

                            <button type="submit" class="btn btn-primary">Publicar</button>
                        </form>
                    </article>

                    <!-- Contenedor para posts dinámicos -->
                    <div id="postsContainer">
                        <%
                            // Recuperamos la lista de posts del request
                            List<Post> posts = (List<Post>) request.getAttribute("posts");
                            if (posts != null) {
                                for (Post post : posts) {
                        %>
                        <article class="card post">
                            <!-- Imagen de perfil del autor -->
                            <div class="post-header">
                                <div class="pfp-container">
                                    <img class="pfp" style="width:44px; height:44px; border-radius:50%; object-fit:cover;"
                                         src="<%= (post.getAuthorImage() != null)
                                                 ? "public/imagenes/" + post.getAuthorUsername() + "/FotoPerfil/" + post.getAuthorImage()
                                                 : "https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png"%>" 
                                         alt="Foto perfil" />
                                </div>
                                <h4 class="user-name"><%= post.getAuthorName()%> (@<%= post.getAuthorUsername()%>)</h4>
                            </div>

                            <h3 class="post-title"><%= post.getTitle()%></h3>

                            <p class="post-text"><%= post.getDescription()%></p>

                            <% if (post.getTags() != null && !post.getTags().isEmpty()) { %>
                            <div class="post-tags">
                                <% for (com.mycompany.piaweb.modelos.Tags tag : post.getTags()) {%>
                                <a href="GetPostsServlet?tag=<%=tag.getTitle()%>" class="tag-pill">
                                    #<%=(tag.getSlug() != null && !tag.getSlug().isEmpty() && !tag.getTitle().isEmpty())
                                            ? tag.getTitle() : "No se encontro este tag"%>
                                </a>
                                <% } %>
                            </div>
                            <% } %>

                            <%-- Imagen del post, si existe --%>
                            <%
                                PostImage img = post.getImage();
                                if (img != null && img.getPath() != null && !img.getPath().isEmpty()) {
                            %>
                            <div class="post-media">
                                <img src="<%= img.getPath()%>" alt="Post Image">
                            </div>
                            <% } %>
                        </article>
                        <%
                            } // fin for
                        } else {
                        %>
                        <p>No hay publicaciones para mostrar.</p>
                        <% } %>
                    </div>

                    <!-- PAGINACION NUMERADA (Igual a Mi Perfil) -->
                    <div class="pagination-container">
                        <%
                            // Obtener datos de paginación
                            int currentPage = (request.getAttribute("currentPage") != null) ? (Integer) request.getAttribute("currentPage") : 1;
                            int totalPages = (request.getAttribute("totalPages") != null) ? (Integer) request.getAttribute("totalPages") : 1;

                            // 1. Recuperamos si hay un TAG activo (del request attribute)
                            String currentTag = (String) request.getAttribute("currentTag");

                            // 2. Recuperamos si hay una BÚSQUEDA activa (del parámetro de URL 'q')
                            String searchQuery = request.getParameter("q");

                            String urlBase;

                            // Lógica de prioridad para construir la URL
                            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                                // CASO 1: Estamos en una BÚSQUEDA
                                // La URL debe apuntar a SearchBarServlet manteniendo el parámetro 'q'
                                urlBase = "SearchBarServlet?q=" + searchQuery + "&page=";

                            } else if (currentTag != null && !currentTag.isEmpty()) {
                                // CASO 2: Estamos filtrando por TAG
                                urlBase = "GetPostsServlet?tag=" + currentTag + "&page=";

                            } else {
                                // CASO 3: Feed NORMAL (Para Ti)
                                urlBase = "GetPostsServlet?page=";
                            }
                        %>

                        <%-- Botón Anterior --%>
                        <% if (currentPage > 1) {%>
                        <a href="<%= urlBase + (currentPage - 1)%>" class="btn btn-secondary">Anterior</a>
                        <% } %>

                        <%-- Números de página --%>
                        <% for (int i = 1; i <= totalPages; i++) { %>
                        <% if (i == currentPage) {%>
                        <span class="btn btn-primary"><%= i%></span>
                        <% } else {%>
                        <a href="<%= urlBase + i%>" class="btn btn-secondary"><%= i%></a>
                        <% } %>
                        <% } %>

                        <%-- Botón Siguiente --%>
                        <% if (currentPage < totalPages) {%>
                        <a href="<%= urlBase + (currentPage + 1)%>" class="btn btn-secondary">Siguiente</a>
                        <% } %>
                    </div>
                </section>

                <aside class="sidebar">
                    <div id="idTagsContainer" class="card">
                        <h4 id="idHeaderTags">Usa estos tags!</h4>

                        <% if (tags != null && !tags.isEmpty()) { %>
                        <% for (Tags tag : tags) {%>
                        <p class="tag-pill">
                            #<%= (tag.getSlug() != null && !tag.getSlug().isEmpty())
                                    ? tag.getTitle()
                                    : "No se encontro este tag"%>
                        </p>
                        <% } %>
                        <% } else { %>
                        <p>No hay tags configurados aún.</p>
                        <% }%>
                    </div>
                </aside>
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
                    <p>Futsy League.Todos los derechos reservados</p>
                </div>
            </footer>

        </main>

        <!--<script src="js/parati.js"></script>-->
    </body>

    <script src="js/footer.js"></script>

</html>