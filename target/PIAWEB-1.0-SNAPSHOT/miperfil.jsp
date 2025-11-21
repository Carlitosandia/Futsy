<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.piaweb.modelos.Post"%>
<%@page import="com.mycompany.piaweb.modelos.Users"%>
<%@page import="com.mycompany.piaweb.modelos.PostImage"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.Period"%>
<%@page import="java.sql.Date"%>
<%@page import="java.time.ZoneId"%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mi perfil</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/miperfil.css" />
        <link rel="stylesheet" href="css/footer.css">
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />
        <main class="page">
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
            <div class="<%= claseCSS%>" style="text-align: center; margin: 15px auto; max-width: 800px; padding: 10px; border-radius: 5px;">
                <%= mensaje%>
            </div>
            <% } %>
            <%
                List<Post> posts = (List<Post>) request.getAttribute("postsUsuario");
                Users usuario = (Users) request.getAttribute("usuario");

                String nombreCompleto = "";
                int edad = -1;
                nombreCompleto = usuario.getName() + " " + usuario.getLastname();
                Date sqlBirthday = usuario.getBirthday();
                if (sqlBirthday != null) {
                    LocalDate birthDate = sqlBirthday.toLocalDate();
                    LocalDate currentDate = LocalDate.now();
                    edad = Period.between(birthDate, currentDate).getYears();
                }
            %>
            <!-- Título -->
            <div class="pr-head">
                <h2 class="pr-title">PERFIL DE USUARIO</h2>
            </div>
            <!-- Layout 2 columnas -->
            <section class="mi-perfil">
                <div class="content-grid">
                    <!-- Columna izquierda: MIS POSTS -->
                    <div class="feed">
                        <div class="bar">
                            <h3 class="bar-title">MIS POSTS</h3>
                            <!-- Icono decorativo -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="bar-icon"><circle cx="12" cy="12" r="10"></circle><path d="M12 16v-4"></path><path d="M12 8h.01"></path></svg>
                        </div>

                        <div class="posts-list">
                            <% if (posts != null && !posts.isEmpty()) {
                                    for (Post post : posts) {
                                        // Generamos un ID unico para enlazar el botón de guardar con su formulario
                                        String formId = "edit-form-" + post.getId();
                            %>

                            <article class="card post-card">
                                <!-- Cabecera del Post (Foto y Nombre) -->
                                <div class="post-header">
                                    <img class="pfp" 
                                         src="<%= (usuario.getImage() != null && !usuario.getImage().isEmpty())
                                                 ? "public/imagenes/" + usuario.getUsername() + "/FotoPerfil/" + usuario.getUsername() + ".jpg"
                                                 : "public/uploads/default.png"%>"
                                         alt="pfp">
                                    <div class="user-info">
                                        <h4 class="user-name"><%= usuario.getName()%></h4>
                                        <span class="user-handle">@<%= usuario.getUsername()%></span>
                                    </div>
                                </div>

                                <!-- Formulario de Edición (Contiene la imagen y campos) -->
                                <!-- Nota: enctype multipart/form-data para subir imágenes -->
                                <form id="<%= formId%>" action="ManagePostServlet" method="post" enctype="multipart/form-data" class="post-content-form">
                                    <input type="hidden" name="postId" value="<%= post.getId()%>">
                                    <input type="hidden" name="action" value="edit">

                                    <!-- Título Editable -->
                                    <input type="text" name="title" class="editable-input post-title-input" value="<%= post.getTitle()%>" placeholder="Título del post">

                                    <!-- Descripción Editable -->
                                    <textarea name="description" class="editable-textarea post-desc-input" placeholder="Escribe algo..."><%= post.getDescription()%></textarea>

                                    <!-- Imagen del Post -->
                                    <div class="post-media-container">
                                        <% if (post.getImage() != null) {%>
                                        <img src="<%= post.getImage().getPath()%>" alt="Imagen del post" class="current-post-image">
                                        <% } else { %>
                                        <div class="no-image-placeholder">Sin imagen</div>
                                        <% }%>

                                        <!-- Input para cambiar imagen (overlay o botón abajo) -->
                                        <div class="file-upload-wrapper">
                                            <label for="file-<%= post.getId()%>" class="file-upload-label">
                                                <span>Cambiar imagen</span>
                                            </label>
                                            <input id="file-<%= post.getId()%>" type="file" name="postImage" accept="image/*" class="file-upload-input">
                                        </div>
                                    </div>
                                </form>

                                <!-- Barra de Acciones (Botones) -->
                                <div class="post-actions">
                                    <!-- Botón Guardar (Vinculado al form de arriba via form attribute) -->
                                    <button type="submit" form="<%= formId%>" class="btn btn-save">
                                        Guardar cambios
                                    </button>

                                    <!-- Formulario Eliminar Post -->
                                    <form id="deletePostForm" action="ManagePostServlet" method="post" class="delete-form">
                                        <input type="hidden" name="postId" value="<%= post.getId()%>">
                                        <input type="hidden" name="action" value="delete">
                                        <button type="submit" class="btn btn-delete" onclick="confirmDeletePost()">
                                            Eliminar
                                            </button>
                                    </form>

                                    <script>
                                        function confirmDeletePost() {
                                            if (confirm("¿Estás seguro de que quieres eliminar tu post? Esta acción no se puede deshacer.")) {
                                                document.getElementById("deletePostForm").submit();
                                            }
                                        }
                                    </script>
                                </div>
                            </article>

                            <%  }
                                            } else { %>
                            <div class="empty-state">
                                <p>Aún no has publicado nada.</p>
                            </div>
                            <% } %>
                        </div>

                        <%-- Paginación --%>
                        <div class="pagination">
                            <% int paginaActual = (request.getAttribute("paginaActual") != null) ? (int) request.getAttribute("paginaActual") : 1;
                                                    int totalPaginas = (request.getAttribute("totalPaginas") != null) ? (int) request.getAttribute("totalPaginas") : 1; %>

                            <% if (paginaActual > 1) {%>
                            <a href="GetMyProfileServlet?pagina=<%= paginaActual - 1%>" class="btn btn-secondary">Anterior</a>
                            <% } %>

                            <% for (int i = 1; i <= totalPaginas; i++) { %>
                            <% if (i == paginaActual) {%>
                            <span class="btn btn-primary"><%= i%></span>
                            <% } else {%>
                            <a href="GetMyProfileServlet?pagina=<%= i%>" class="btn btn-secondary"><%= i%></a>
                            <% } %>
                            <% } %>

                            <% if (paginaActual < totalPaginas) {%>
                            <a href="GetMyProfileServlet?pagina=<%= paginaActual + 1%>" class="btn btn-secondary">Siguiente</a>
                            <% }%>
                        </div>
                    </div>

                    <!-- Columna derecha: PERFIL -->
                    <aside class="profile card card-dark shadow-lg">
                        <h2 class="profile-title"><%= usuario.getUsername()%></h2>

                        <img class="avatar-lg"
                             src="<%= (usuario.getImage() != null && !usuario.getImage().isEmpty())
                                     ? "public/imagenes/" + usuario.getUsername() + "/FotoPerfil/" + usuario.getImage()
                                     : "public/uploads/default.png"%>"
                             alt="Foto de perfil">

                        <div class="profile-info-text"> 
                            <p>¡Hola <%= usuario.getName()%>! Ya tienes <%= edad%> años</p>
                        </div>

                        <!-- Formulario para editar perfil -->
                        <form class="profile-form" action="EditProfileServlet" method="post" enctype="multipart/form-data">
                            <div class="field midrow">
                                <div class="row-2">
                                    <div class="field">
                                        <label class="label">Nombre (s)</label>
                                        <input class="input" type="text" name="nombre" value="<%= usuario.getName()%>" required>
                                    </div>
                                    <div class="field">
                                        <label class="label">Apellidos</label>
                                        <input class="input" type="text" name="apellidos" value="<%= usuario.getLastname()%>" required>
                                    </div>
                                </div>

                                <div class="field">
                                    <label class="label">Correo electrónico</label>
                                    <input class="input" type="email" name="correo" value="<%= usuario.getEmail()%>" required>
                                </div>

                                <div class="row-2">
                                    <div class="field w-160">
                                        <label class="label">Fecha de nacimiento</label>
                                        <input class="input" type="date" name="birthday"
                                               value="<%= usuario.getBirthday() != null
                                                       ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(usuario.getBirthday())
                                                       : ""%>">
                                    </div>
                                </div>

                                <div class="field">
                                    <label for="idContrasenaR" class="label">Contraseña</label>
                                    <input type="password" id="idContrasenaR" class="input" name="passwordUpdated" placeholder="Ingresa tu contraseña" value="<%= usuario.getPassword()%>" required>
                                </div>

                                <div class="field">
                                    <label for="idConfirmContrasenaR" class="label">Confirmar contraseña</label>
                                    <input type="password" id="idConfirmContrasenaR" class="input" name="passwordConfirm" placeholder="Confirma tu contraseña" required>
                                </div>

                                <div class="field">
                                    <label class="label">Actualizar foto de perfil</label>
                                    <input type="file" name="fotoPerfil" accept="image/*" class="input-file-profile">
                                </div>

                                <button type="submit" class="btn btn-primary w-100">Guardar cambios</button>
                            </div>
                        </form>

                        <form id="deleteAccountForm" action="DeleteAccountServlet" method="post" style="margin-top: 15px; width: 100%;">
                            <button type="button" class="btn btn-danger w-100" onclick="confirmDelete()">Eliminar cuenta</button>
                        </form>

                        <script>
                            function confirmDelete() {
                                if (confirm("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")) {
                                    document.getElementById("deleteAccountForm").submit();
                                }
                            }
                        </script>
                    </aside>
                </div>
            </section>

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
        </main>
    </body>
    <script src="js/footer.js"></script>

    <!-- SCRIPT PARA PREVISUALIZAR IMÁGENES -->
    <script>
document.addEventListener('DOMContentLoaded', function () {
// Seleccionamos todos los inputs de tipo file que usamos para los posts
const fileInputs = document.querySelectorAll('.file-upload-input');
fileInputs.forEach(input => {
input.addEventListener('change', function (e) {
// Verificamos que haya un archivo seleccionado
if (this.files && this.files[0]) {
const reader = new FileReader();
// Buscamos el contenedor padre (.post-media-container)
const container = this.closest('.post-media-container');
// Buscamos si ya existe una imagen o el placeholder
let img = container.querySelector('.current-post-image');
const placeholder = container.querySelector('.no-image-placeholder');
// Cuando el archivo se termine de leer...
reader.onload = function (e) {
if (img) {
// Si ya existe la imagen, simplemente actualizamos su fuente (src)
img.src = e.target.result;
} else {
// Si no existe imagen (estaba el placeholder "Sin imagen")
if (placeholder) {
placeholder.style.display = 'none'; // Ocultamos el texto "Sin imagen"
}

// Creamos una nueva etiqueta img
img = document.createElement('img');
img.classList.add('current-post-image');
img.src = e.target.result;
img.alt = "Previsualización";
// Insertamos la imagen antes del botón de subir archivo
const wrapper = container.querySelector('.file-upload-wrapper');
container.insertBefore(img, wrapper);
}
}

// Leemos el archivo como URL de datos (base64)
reader.readAsDataURL(this.files[0]);
}
});
});
});
    </script>
</html>