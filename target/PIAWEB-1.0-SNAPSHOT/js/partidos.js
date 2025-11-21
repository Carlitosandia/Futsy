(function () {
    // --- Referencias al DOM ---
    const scroller = document.getElementById('dates');
    const btnMundial = document.querySelector('.btn-mundial');
    const btnGeneral = document.querySelector('.btn-general');
    const secGeneral = document.getElementById('partidos-general');
    const secMundial = document.getElementById('partidos-mundial');
    const matchDateLabel = document.querySelector('.match-date');
    const cont = document.getElementById('dates');

    // --- Funciones Auxiliares ---

    // Extrae todas las fechas únicas que existen en las tarjetas HTML
    function getDatesFromDOM() {
        const allCards = document.querySelectorAll('.match-card');
        const uniqueDates = new Set();
        
        allCards.forEach(card => {
            const date = card.getAttribute('data-date');
            if (date) uniqueDates.add(date);
        });

        // Convertir a array y ordenar cronológicamente
        return Array.from(uniqueDates).sort();
    }

    // Formatea "2025-11-20" a "Jue. 20 nov"
    function formatDateLabel(dateString) {
        if (!dateString) return "";
        // Truco para evitar problemas de zona horaria: crear fecha con partes
        const parts = dateString.split('-'); // [2025, 11, 20]
        const date = new Date(parts[0], parts[1] - 1, parts[2]); 
        
        // Formato en español
        return date.toLocaleDateString('es-ES', { 
            weekday: 'short', 
            day: '2-digit', 
            month: 'short' 
        });
    }

    // --- Lógica de Filtrado ---
    function filterMatchesByDate(dateString) {
        const allCards = document.querySelectorAll('.match-card');
        let matchesFound = 0;

        allCards.forEach(card => {
            const cardDate = card.getAttribute('data-date');
            // Mostramos si coincide la fecha. 
            // IMPORTANTE: Quitamos 'display: flex' directo para que el CSS controle el layout
            // Usamos removeProperty para que retome el valor de tu hoja de estilos (grid o flex)
            if (cardDate === dateString) {
                card.style.display = ''; 
                matchesFound++;
            } else {
                card.style.display = 'none';
            }
        });
        
        console.log(`Mostrando ${matchesFound} partidos para ${dateString}`);
    }

    // --- Control de Pestañas ---
    function showPartidosGeneral() {
        btnGeneral.classList.add('is-active');
        btnMundial.classList.remove('is-active');
        secGeneral.hidden = false;
        secMundial.hidden = true;
        
        // Al cambiar de tab, forzamos un re-filtro con la fecha activa actual
        const activeBtn = cont.querySelector('.pill.is-active');
        if(activeBtn && activeBtn.dataset.rawDate) {
            filterMatchesByDate(activeBtn.dataset.rawDate);
        }
    }

    function showPartidosMundial() {
        btnMundial.classList.add('is-active');
        btnGeneral.classList.remove('is-active');
        secMundial.hidden = false;
        secGeneral.hidden = true;
        
        const activeBtn = cont.querySelector('.pill.is-active');
        if(activeBtn && activeBtn.dataset.rawDate) {
            filterMatchesByDate(activeBtn.dataset.rawDate);
        }
    }

    // --- Inicialización ---
    function init() {
        // Event Listeners básicos
        btnGeneral.addEventListener('click', showPartidosGeneral);
        btnMundial.addEventListener('click', showPartidosMundial);
        document.getElementById('prev').onclick = () => scroller.scrollBy({ left: -200, behavior: 'smooth' });
        document.getElementById('next').onclick = () => scroller.scrollBy({ left: 200, behavior: 'smooth' });

        // 1. Obtener fechas reales del HTML
        const datesList = getDatesFromDOM();
        
        cont.innerHTML = ''; // Limpiar contenedor

        if (datesList.length === 0) {
            // Si no hay fechas (porque no hay partidos), limpiamos label y salimos
            if(matchDateLabel) matchDateLabel.textContent = "Sin Partidos";
            return;
        }

        // 2. Generar botones dinámicos
        const dateButtons = {};

        datesList.forEach(dateStr => {
            const btn = document.createElement('button');
            btn.className = 'pill';
            btn.textContent = formatDateLabel(dateStr);
            btn.dataset.rawDate = dateStr; // Guardamos la fecha original para filtrar

            btn.addEventListener('click', () => {
                // Visual active
                cont.querySelectorAll('.pill.is-active').forEach(b => b.classList.remove('is-active'));
                btn.classList.add('is-active');

                // Texto encabezado
                if(matchDateLabel) matchDateLabel.textContent = formatDateLabel(dateStr);

                // Filtro
                filterMatchesByDate(dateStr);
            });

            cont.appendChild(btn);
            dateButtons[dateStr] = btn;
        });

        // 3. Seleccionar la última fecha disponible (o la primera, según prefieras)
        // Normalmente en futbol quieres ver "lo más reciente" o "lo de hoy"
        const lastDate = datesList[datesList.length - 1]; 
        
        if (dateButtons[lastDate]) {
            dateButtons[lastDate].click();
            setTimeout(() => {
                 dateButtons[lastDate].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
            }, 100);
        }
    }

    // Ejecutar
    init();

})();