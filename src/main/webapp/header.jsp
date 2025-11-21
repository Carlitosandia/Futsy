<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mycompany.piaweb.modelos.Users" %>

<%
    Users usuarioSesion = (Users) session.getAttribute("usuarioSesion");
%>

<header class="site-header">
  <div class="logo">
    <img src="public/logo/image0.png" alt="Logo">
  </div>

<form action="SearchBarServlet" method="GET" class="search-form">
    <div class="search-bar">
        <input type="text" name="q" placeholder="Buscas algun post? Escribelo!" required>
    </div>
</form>

<a href="LoadAdvancedSearchServlet" style="    
    all: unset;
    display: inline-block;
    cursor: pointer;
    border-radius: 8px;
    padding: 10px 16px;
    font-weight: 700;
    text-align: center;
    transition: all 0.2s;
">
    Búsqueda avanzada
</a>

<div class="user-actions">
    <% if (usuarioSesion != null) { %>
        <div class="usuario">
          <img src="public/imagenes/<%= usuarioSesion.getUsername() %>/FotoPerfil/<%= usuarioSesion.getImage() %>" 
               alt="Foto de <%= usuarioSesion.getName() %>" 
               style="width:40px; height:40px; border-radius:50%; object-fit:cover;">
        </div>
        <span style="margin-left:10px; font-weight:600;">
          <%= usuarioSesion.getName() %>
        </span>
    <% } else { %>
        <div class="usuario">
          <img src="public/assets/usuario sesion.png" alt="usuario">
        </div>
        <a href="index.jsp"
           style=
           "
                display: inline-block;
                padding: 8px 16px;
                background-color: #007bff; 
                color: white;
                text-decoration: none;
                border-radius: 5px;    
                font-weight: bold;
                transition: background-color 0.3s ease;
                margin-left: 10px;
                background-color: #0056b3; 
                color: white;
            "
            >Iniciar sesión</a>
    <% } %>
  </div>
</header>
