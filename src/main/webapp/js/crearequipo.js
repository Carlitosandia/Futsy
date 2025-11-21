// Variables de estado para almacenar los IDs seleccionados
let seleccionadosGeneral = new Set();
let seleccionadosMundial = new Set();


function previewLogo(event) {
    const input = event.target;
    const previewImg = document.getElementById('logo-preview-img');

    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            previewImg.src = e.target.result;
            previewImg.hidden = false;
            previewImg.style.display = 'block'; 
        }

        reader.readAsDataURL(input.files[0]);
    }
}
/**
 * Filtra las tarjetas de jugadores según posicion y país.
 */
function filtrar(tipo) {
    const posVal = document.getElementById(`posicion-${tipo}`).value;
    let paisVal = 'TODOS';
    
    if(tipo === 'mundial') {
        const paisInput = document.getElementById('pais-mundial');
        if(paisInput) paisVal = paisInput.value;
    }

    const tarjetas = document.querySelectorAll(`#grid-${tipo} .player-card-item`);

    tarjetas.forEach(card => {
        const posCard = card.getAttribute('data-pos');
        const paisCard = card.getAttribute('data-country');

        const cumplePos = (posVal === 'TODAS') || (posCard === posVal);
        const cumplePais = (paisVal === 'TODOS') || (paisCard === paisVal);

        if (cumplePos && cumplePais) {
            card.style.display = ''; 
        } else {
            card.style.display = 'none';
        }
    });
}

/**
 * Maneja la selección y deseleccion de jugadores
 */
function toggleSeleccion(id, tipo) {
    let setActual;
    if (tipo === 'general') {
        setActual = seleccionadosGeneral ;
    } else{
        setActual =  seleccionadosMundial;
    }
        
    const card = document.getElementById(`card-${tipo}-${id}`);
    const btn = card.querySelector('button');

    if (setActual.has(id)) {
        setActual.delete(id);
        card.classList.remove('selected');
        btn.innerText = "AGREGAR";
        btn.style.backgroundColor = ""; 
    } else {
        if (setActual.size >= 11) {
            alert("Solo puedes seleccionar un máximo de 11 jugadores.");
            return;
        }
        setActual.add(id);
        card.classList.add('selected');
        btn.innerText = "QUITAR";
        btn.style.backgroundColor = "#dc3545"; 
    }

    actualizarPanel(tipo);
    actualizarInputHidden(tipo);
}

/**
 * Actualiza la lista visual en el panel izquierdo
 */
function actualizarPanel(tipo) {
    const setActual = (tipo === 'general') ? seleccionadosGeneral : seleccionadosMundial;
    const contenedorLista = document.querySelector(`#panel-jugadores-${tipo} .selected-list-container`);
    const contador = document.getElementById(`contador-${tipo}`);
    
    contenedorLista.innerHTML = '';
    
    contador.innerText = `${setActual.size}/11`;

    setActual.forEach(id => {
        const cardOriginal = document.getElementById(`card-${tipo}-${id}`);
        const nombre = cardOriginal.getAttribute('data-name');
        const pos = cardOriginal.getAttribute('data-pos');

        const itemDiv = document.createElement('div');
        itemDiv.style.cssText = "padding: 8px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; font-size: 0.9rem;";
        itemDiv.innerHTML = `
            <span><strong>${pos}</strong> ${nombre}</span>
            <span style="cursor:pointer; color:red;" onclick="toggleSeleccion('${id}', '${tipo}')">&times;</span>
        `;
        contenedorLista.appendChild(itemDiv);
    });
}

/**
 * Actualiza el input hidden que se enviará al servidor
 */
function actualizarInputHidden(tipo) {
    //Conseguimos el set 
    const setActual = (tipo === 'general') ? seleccionadosGeneral : seleccionadosMundial;
    // Conseguimos el elemento input, para darle un valor y mandar ese valor cuando hagamos submit del formulario
    const inputIds = document.getElementById(`input-ids-${tipo}`);
   // Al value del input que tenemos en el form para la lista de jugadores le vamos a pasar nuestra lista de jugadores seleccionados
  // Ya que esa lista es la lisa de jugadores que mi usuario quiere tener para su equipo 
  // Y se manda de tal manera en la que conseguimos el array y unimos cada valor de ese array por una coma
  // ej: chuchobenitezids,chacojimenezids,piojoalvaradoids
    inputIds.value = Array.from(setActual).join(',');
}