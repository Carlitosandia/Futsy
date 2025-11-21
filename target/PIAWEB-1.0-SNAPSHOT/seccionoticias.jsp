<%-- 
    Document   : seccionoticias
    Created on : 21 oct 2025, 20:22:00
    Author     : Sofia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sección Noticiera</title>
    <link rel="stylesheet" href="boostrap/css/bootstrap.css">
    <link rel="stylesheet" href="css/applayout.css">
    <link rel="stylesheet" href="css/posts.css">
    <link rel="stylesheet" href="css/footer.css">
</head>

<body class="noticiero-bg">
<jsp:include page="header.jsp" />
<jsp:include page="navbar.jsp" />

    <main class="page">
        <div id="idBackgroundN">
            <h1 id="idTextN"> "Donde Rueda la Palabra"</h1>
        </div>

        <div id="idBackgroundN1">
            <h2 id="idTextN1"> Insert Text</h2>
        </div>

        <div id="idBackgroundN2">
            <h2 id="idTextN2"> Insert Text</h2>
        </div>
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

</html>