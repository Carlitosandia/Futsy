<%-- 
    Document   : register
    Created on : 21 oct 2025, 20:11:02
    Author     : Sofia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro</title>

    <link rel="stylesheet" href="boostrap/css/bootstrap.css">
    <link rel="stylesheet" href="css/register.css">
</head>

<body class="registrar-bg">

    <% String mensaje = (String) request.getAttribute("MensajeRegistro"); %>

    <% if (mensaje != null) { %>
        <div class="alerta-registro"><%= mensaje %></div>
    <% } %>

    <!-- CONTENEDOR PRINCIPAL (DOS COLUMNAS) -->
    <div class="register-container">

        <!-- COLUMNA IZQUIERDA -->
        <div class="col-left-register">
            <div class="left-content">
                <h4 class="col-titulo-register">¡Únete a Futsy hoy!</h4>
                <img class="logo-register" src="public/logo/image0.png" alt="Logo Futsy">
            </div>
        </div>

        <!-- COLUMNA DERECHA -->
        <div class="col-right-register">
            <h3 id="idCreateAccount">Crear cuenta</h3>

            <form action="RegistroServlet" method="post" id="idRegistrar" enctype="multipart/form-data">

                <label class="label-names">Nombre (s)</label>
                <input type="text" name="nombre" class="input" placeholder="Ingresa tu nombre(s)" required>

                <label class="label-names">Apellidos</label>
                <input type="text" name="apellidos" class="input" placeholder="Ingresa tus apellidos" required>

                <label class="label-names">Usuario</label>
                <input type="text" name="usuarioR" class="input" placeholder="Ingresa tu usuario" required>

                <label class="label-names">Correo electrónico</label>
                <input type="email" name="correoR" class="input" placeholder="Ingresa tu correo electrónico" required>

                <label class="label-names">Fecha de nacimiento</label>
                <input type="date" name="fechaNacimiento" class="input" required>

                <label class="label-names">Teléfono</label>
                <input type="text" name="telefonoR" class="input" placeholder="Ingresa tu teléfono" required>

                <label class="label-names">Contraseña</label>
                <input type="password" name="contrasenaR" class="input" placeholder="Ingresa tu contraseña" required>

                <label class="label-names">Confirmar contraseña</label>
                <input type="password" name="confirmarContrasenaR" class="input" placeholder="Confirma tu contraseña"
                    required>

                <label class="label-names">Subir foto</label>
                <input type="file" name="fotoR" class="input" required>

                <button type="submit" id="idButtonRegistrar">Registrar</button>
            </form>
        </div>

    </div>
    
    <script>
    const hoy = new Date();
    
    const hace13Anios = new Date(hoy.getFullYear() - 13, hoy.getMonth(), hoy.getDate());
    
    const año = hace13Anios.getFullYear();
    const mes = String(hace13Anios.getMonth() + 1).padStart(2, '0');
    const dia = String(hace13Anios.getDate()).padStart(2, '0');
    
    const fechaMaxima = `${año}-${mes}-${dia}`;
    
    document.getElementById("fechaNacimiento").setAttribute("max", fechaMaxima);
</script>

</body>

</html>

