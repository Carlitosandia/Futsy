<%-- 
    Document   : index
    Created on : 21 oct 2025, 20:09:48
    Author     : Sofia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Iniciar Sesion</title>
        <link rel="stylesheet" href="boostrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/index.css">
    </head>

    <body class="iniciar-sesion-bg">    
        <div id="idImage">
            <h4 id="idWelcome" class="col-titulo">Bienvenido de vuelta !!!</h4>
            <img src="public/logo/image0.png" alt="">
        </div>

        <div class="col-right">
            <%

                String mensajeError = (String) request.getAttribute("MensajeLoginError");

            %>

            <%if (mensajeError != null) {%>
            <div class = "alert-error-bar" role = "alert" style="padding: 10px; background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; border-radius: 5px; margin-bottom: 15px;"> 
                <strong>Error: </strong><%=mensajeError%>
            </div>
            <%}%>

            <h5 id="idIniciar">Iniciar sesión</h5>

            <form action="LoginServlet" method="post" id="idIniciarSesion">
                <label for="idUsuario" class="first-form"> Usuario</label>
                <input type="text" id="idUsuario" class="first-forminput" name="usuario" placeholder="Ingresa tu usuario"
                       required>
                <label for="idContrasena" class="first-form"> Contraseña</label>
                <input type="password" id="idContrasena" class="first-forminput" name="contrasena"
                       placeholder="Ingresa tu contraseña" required>
                <button id="idForgotPassword">¿Olvidaste tu contraseña?</button>
                <input type="submit" value="Iniciar sesión" id="idButtonIniciar" class="first-forminput">

            </form>

            <p id="idNoCuenta">¿No tienes una cuenta? <a href="register.jsp">Registrate</a></p>

    </body>

</html>