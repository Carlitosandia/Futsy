<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.piaweb.modelos.Tags"%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Búsqueda Avanzada</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="css/applayout.css">
        <link rel="stylesheet" href="css/footer.css">
        <style>
            /* Estilos extra específicos para este form */
            .advanced-search-container {
                max-width: 800px;
                margin: 40px auto;
                background: #1e1e1e; /* Asumiendo tema oscuro por tus clases anteriores */
                padding: 30px;
                border-radius: 15px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.3);
                color: white;
            }
            .form-label { font-weight: bold; margin-top: 10px; }
            .btn-search-adv { margin-top: 20px; width: 95%; font-weight: bold; text-transform: uppercase;}
        </style>
    </head>

    <body class="parati-bg">
        <jsp:include page="header.jsp" />
        <jsp:include page="navbar.jsp" />

        <main class="page">
                
                <div class="advanced-search-container">
                    <h2 class="text-center mb-4">Búsqueda Avanzada</h2>
                    
                    <form action="SearchBarServlet" method="GET">
                        <input type="hidden" name="type" value="advanced">

                        <div class="mb-3">
                            <label class="form-label">Palabras clave</label>
                            <input type="text" name="searchText" class="form-control" placeholder="Ej: Final del mundial, golazo...">
                            <small class="text-muted" style="color: #aaa !important;">Busca en título y descripción</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Categoría / Tag</label>
                            <select name="searchTagId" class="form-control form-select">
                                <option value="">-- Todas las categorías --</option>
                                <% 
                                    List<Tags> listaTags = (List<Tags>) request.getAttribute("listaTags");
                                    if(listaTags != null) {
                                        for(Tags tag : listaTags) {
                                %>
                                    <option value="<%= tag.getId() %>"><%= tag.getTitle() %></option>
                                <% 
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Desde</label>
                                <input type="date" name="dateFrom" class="form-control">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Hasta</label>
                                <input type="date" name="dateTo" class="form-control">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary btn-lg btn-search-adv">Buscar</button>
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
                    <p>Futsy League. Todos los derechos reservados</p>
                </div>
            </footer>
        </main>
    </body>
    <script src="js/footer.js"></script>
</html>