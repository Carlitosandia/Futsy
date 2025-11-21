<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Registro</title>
        <link rel="stylesheet" href="boostrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/editprofile.css">
        <link rel="stylesheet" href="css/footer.css">
    </head>

    <body class="editprofile-bg">
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />

        <main class="page">
            <!-- contenido nuevo -->
            <div id="idTopBg">
                <h2 id="idEditProfileTitle">EDITAR PERFIL</h2>
            </div>

            <div id="idMidBg">
                <h3 id="idUsername"> Nombre de usuario</h3>

                <img src="https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png" alt="Profile Pic"
                     id="idProfilePicEP">

                <form action="editprofile.html" method="post" id="idEditProfileForm">
                    <label for="idNombre" class="tagsEP">Nombre (s)</label>
                    <input type="text" id="idNombre" class="midrow" name="nombre" placeholder="Ingresa tu nombre(s)"
                           required>
                    <label for="idApellidos" class="tagsEP"> Apellidos</label>
                    <input type="text" id="idApellidos" class="midrow" name="apellidos" placeholder="Ingresa tus apellidos"
                           required>
                    <label for="idCorreoR" class="tagsEP">Correo electronico</label>
                    <input type="text" id="idCorreoR" class="fullrow" name="correoR" placeholder="ejemplo@correo.com"
                           required>
                    <label for="idFechaNacR" class="tagsEP"> Fecha de nacimiento</label>
                    <input type="date" id="idFechaNacR" class="midrow" name="fechaNacimiento" required>
                    <label for="idTelefonoR" class="tagsEP"> Tel.</label>
                    <input type="text" id="idTelefonoR" class="midrow" name="telefonoR" placeholder="(Opcional)" required>
                    <label for="idContrasenaR" class="tagsEP">Contraseña</label>
                    <input type="password" id="idContrasenaR" class="fullrow" name="contrasenaR"
                           placeholder="Ingresa tu contraseÃ±a" required>
                    <label for="idConfirmContrasenaR" class="tagsEP">Confirmar contraseÃ±a</label>
                    <input type="text" id="idConfirmContrasenaR" class="fullrow" name="confirmarContrasenaR"
                           placeholder="Confirma tu contraseÃ±a" required>
                    <input type="submit" value="Confirmar" id="idButtonConfirmar">


                </form>
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


    </body>

    <script src="js/footer.js"></script>

</html>