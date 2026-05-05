document.addEventListener("DOMContentLoaded", function() {
    //FRONTEND
    const temaGuardado = localStorage.getItem('temaUniPlanner') || 'light';
    document.documentElement.setAttribute('data-bs-theme', temaGuardado);
    actualizarIconoTema(temaGuardado);
    sweetAlertError();
    sweetAlertSuccess();
    actualizarReloj();
    setInterval(actualizarReloj, 1000);
    renderizarCalendario();

    //BACKEND
    cargarMateriasSelect();
    cargarTareas();
    materiasContenedor();
    ultimas2Tareas();
    tareasProximasAVencer();
    materiasHorarioContainer();
    tareasNotificaciones();
})

const temaGuardado = localStorage.getItem('temaUniPlanner') || 'light';
document.documentElement.setAttribute('data-bs-theme', temaGuardado);

let checkboxActual = null;
let modalEntregaInstancia = null;

///////////////////////////////////////////////////////////BACKEND///////////////////////////////////////////////////////

async function cargarMateriasSelect() {
    const selectMateria = document.getElementById('selectMaterias');
    const selectMateria2 = document.getElementById('materia');

    try {
        const response = await fetch('/selectMaterias');
        const materias = await response.json();


        selectMateria.innerHTML = '<option value="" selected disabled>Selecciona una materia</option>';

        materias.forEach(materia => {
            const option = document.createElement('option');
            option.value = materia.id_materia;
            option.textContent = materia.nombre;
            selectMateria.appendChild(option);
            selectMateria2.appendChild(option.cloneNode(true));
        });
    } catch (error) {
        console.error('Error al cargar materias:', error);
    }
}
async function cargarTareas() {
    const divTareas = document.getElementById('vista-lista');

    try {
        const response = await fetch('/tareasContainer');
        const tareas = await response.json();

        divTareas.innerHTML = '';

        tareas.forEach(tarea => {
            const div = document.createElement('div');
            div.className = 'list-group-item d-flex justify-content-between align-items-center p-3 mb-2 shadow-sm border-0 rounded-4';

            let badgeClass = 'bg-secondary';
            if (tarea.prioridad.nombre === 'Alta') badgeClass = 'bg-danger';
            if (tarea.prioridad.nombre === 'Media') badgeClass = 'bg-warning text-dark';
            if (tarea.prioridad.nombre === 'Baja') badgeClass = 'bg-success';


            const fechaFormateada = new Date(tarea.fecha_entrega).toLocaleDateString('es-MX', {
                day: 'numeric',
                month: 'short'
            });

            div.innerHTML = `
    <div class="d-flex align-items-center">
        <span class="badge ${badgeClass} rounded-pill me-3 px-3">
            ${tarea.prioridad.nombre.toUpperCase()}
        </span>
        <div>
            <strong class="d-block">${tarea.titulo}</strong>
            <small class="text-muted">
                ${tarea.materia.nombre} • ${fechaFormateada}
            </small>
        </div>
    </div>
    <div class="d-flex align-items-center gap-2">
        <button class="btn btn-link text-info p-0 d-flex align-items-center justify-content-center"
                style="width: 1.25em; height: 1.25em; font-size: 1.25rem;"
                onclick="verDetalleTarea(${tarea.id_tarea})">
            <i class="bi bi-info-circle"></i>
        </button>

        <button class="btn btn-link text-secondary p-0 d-flex align-items-center justify-content-center"
                style="width: 1.25em; height: 1.25em; font-size: 1.25rem;"
                onclick="cargarDatosEditar(${tarea.id_tarea})">
            <i class="bi bi-pencil-square"></i>
        </button>

        <input type="checkbox" class="form-check-input ms-2 fs-5 m-0"
               ${tarea.estatus === 'Entregada' ? 'checked' : ''}
               onclick="prepararEntrega(this, '${tarea.titulo}', ${tarea.id_tarea})">
    </div>
        `;

            divTareas.appendChild(div);
        });

        if (tareas.length === 0) {
            divTareas.innerHTML = '<p class="text-center text-muted py-4">No tienes tareas pendientes. ¡Buen trabajo!</p>';
        }

    } catch (error) {
        console.error("Error cargando tareas:", error);
        divTareas.innerHTML = '<p class="text-danger text-center">Error al cargar las tareas.</p>';
    }
}
async function cargarDatosEditar(id) {
    try{
        const response = await fetch(`/datos/${id}`);
        const tareaInformacion = await response.json();
        document.getElementById("id_tarea").value = id;
        document.getElementById("titulo").value = tareaInformacion.titulo ;
        document.getElementById("prioridad").value = tareaInformacion.prioridad.id_prioridad;
        document.getElementById("fechaEntrega").value = tareaInformacion.fecha_entrega;
        document.getElementById("materia").value = tareaInformacion.materia.id_materia;
        document.getElementById("descripcion").value = tareaInformacion.descripcion;
        const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarTarea'));
        modalEditar.show();
    }catch(error){
        console.error(error);
    }
}
async function materiasContenedor(){
    try{
        const res = await fetch('/materiasContainer');
        const materias = await res.json();
        const ul = document.getElementById("listaMaterias");
        ul.innerHTML = '';
        materias.forEach(materia => {
            const li = document.createElement("li");
            li.className = 'nav-item small d-flex align-items-center justify-content-between item-materia p-1 rounded'
            li.innerHTML = `<div><input type="checkbox" checked class="form-check-input me-2">${materia.nombre}</div>
                    <div class="acciones-materia d-flex">
                        <button class="btn btn-sm btn-link text-secondary p-0 mx-1"
                                onclick="edicionMateria(${materia.id_materia})"
                                ><i class="bi bi-pencil-square"></i></button>
                        <button class="btn btn-sm btn-link text-danger p-0 mx-1"
                                onclick="eliminarMateria(${materia.id_materia})"
                                ><i class="bi bi-trash"></i></button>
                    </div>`

            ul.appendChild(li);
        })
    } catch (error) {console.error(error);}
}
async function eliminarMateria(id) {
    const resultado = await Swal.fire({
        title: '¿Estás seguro?',
        text: "Se eliminarán todos los horarios asociados a esta materia.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    });

    if (resultado.isConfirmed) {
        try {
            const res = await fetch(`/eliminarMateria/${id}`, {
                method: 'POST'
            });

            if (res.ok) {
                window.alertaSuccess = "Materia eliminada correctamente";
                sweetAlertSuccess();

                materiasContenedor();
                cargarMateriasSelect();
                cargarTareas();
            } else {
                window.alertaError = "No se pudo eliminar la materia";
                sweetAlertError();
            }
        } catch (error) {
            console.error("Error al eliminar:", error);
        }
    }
}
async function edicionMateria(id) {
    try{
        const response = await fetch(`/datosMateria/${id}`);
        const materia = await response.json();
        document.getElementById("id_materia").value = materia.id_materia;
        document.getElementById("input-editar-nombre").value = materia.nombre;
        document.getElementById("input-editar-color").value = materia.color;
        const modalEditarMateria = new bootstrap.Modal(document.getElementById("modalEditarMateria"));
        modalEditarMateria.show();
    }catch(error){console.error(error)}
}
async function completarEntrega() {
    try{
        id = document.getElementById("id_tareaCompletada").value;
        console.log(id);
        const res = await fetch(`/entregarTarea/${id}`, {
            method: 'POST'
        });
        if (res.ok) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalEntregarTarea'));
            modal.hide();
            window.location.href = "/index?alertaSuccess=Tarea completada";
        }
    }catch (error){
        console.error(error);
    }
}
async function ultimas2Tareas(){
    try{
        const res = await fetch("/ultimas2Tareas");
        const tareas = await res.json();
        contenedorTareas = document.getElementById("contenedor-tareas-pendientes")

        tareas.forEach(tareas => {
            const div = document.createElement("div");
            div.className = "class=border-0 py-2 px-0 d-flex align-items-start opacity-75";
            div.innerHTML = ` <i class="bi bi-check2-all text-success me-2 fs-5"></i>
                        <div>
                            <div class="small fw-bold text-decoration-line-through">${tareas.titulo}</div>
                            <div class="text-muted" style="font-size: 0.7rem;">Terminado el ${tareas.fecha_entregada}</div>
                        </div>`
            contenedorTareas.appendChild(div);
        })

    }catch(error){
        console.error(error);
    }
}
async function verDetalleTarea(id) {
    try {
        const response = await fetch(`/datos/${id}`);
        const t = await response.json();

        document.getElementById('det-titulo').textContent = t.titulo;
        document.getElementById('det-materia').textContent = t.materia.nombre;
        document.getElementById('det-fecha').textContent = t.fecha_entrega;
        document.getElementById('det-descripcion').textContent = t.descripcion || 'Sin descripción adicional.';

        const badge = document.getElementById('det-prioridad');
        badge.textContent = t.prioridad.nombre.toUpperCase();
        badge.className = 'badge rounded-pill ' +
            (t.prioridad.nombre === 'Alta' ? 'bg-danger' :
                (t.prioridad.nombre === 'Media' ? 'bg-warning text-dark' : 'bg-success'));

        const modal = new bootstrap.Modal(document.getElementById('modalDetalleTarea'));
        modal.show();
    } catch (error) {
        console.error("Error al ver detalle:", error);
    }
}
async function tareasProximasAVencer(){
    const res = await fetch(`/tareasProximas`);
    const tareas = await res.json();
    const contenedor = document.getElementById("contenedor-tareasProximas");

    tareas.forEach(tareas => {
        const div = document.createElement("div");
        div.className = "p-2 rounded-3 bg-light border-start border-danger border-4 mb-3";

        div.innerHTML = `<div class="fw-bold small">${tareas.materia.nombre} - ${tareas.titulo}</div>
                    <div class="text-muted" style="font-size: 0.7rem;">Vence el ${tareas.fecha_entrega}</div>`

        contenedor.appendChild(div);
    })
}
async function materiasHorarioContainer() {
    try {
        // 1. Obtener los datos del backend
        const response = await fetch('/materiasHorario');
        const horarios = await response.json();

        const tbody = document.querySelector('#panel-horario tbody');
        tbody.innerHTML = '';

        if (horarios.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-muted text-center py-4">No tienes horarios registrados aún.</td></tr>`;
            return;
        }


        const horasUnicas = [];
        horarios.forEach(h => {
            const inicio = h.hora_inicio.substring(0, 5);
            const final = h.hora_final.substring(0, 5);
            const rango = `${inicio}-${final}`;

            if (!horasUnicas.some(r => r.rango === rango)) {
                horasUnicas.push({ inicio, final, rango });
            }
        });
        horasUnicas.sort((a, b) => a.inicio.localeCompare(b.inicio));

        const diasSemana = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

        horasUnicas.forEach(hora => {
            const tr = document.createElement('tr');

            const tdHora = document.createElement('td');
            tdHora.className = 'text-muted small';
            tdHora.innerHTML = `${hora.inicio}<br>a<br>${hora.final}`;
            tr.appendChild(tdHora);

            // --- B. Crear celdas para cada Día ---
            diasSemana.forEach(diaFiltro => {
                const tdDia = document.createElement('td');

                const claseEncontrada = horarios.find(h => {
                    const diaBD = h.dia.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
                    const diaColumna = diaFiltro.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

                    return diaBD === diaColumna &&
                        h.hora_inicio.substring(0, 5) === hora.inicio &&
                        h.hora_final.substring(0, 5) === hora.final;
                });

                if (claseEncontrada) {
                    tdDia.className = 'p-1';

                    const nombreMateria = claseEncontrada.materia.nombre;
                    const colorMateria = claseEncontrada.materia.color || 'primary';

                    const esHex = colorMateria.startsWith('#');
                    let bloqueHTML = "";

                    if (esHex) {
                        bloqueHTML = `
                            <div class="bloque-horario rounded-3 w-100 p-2" 
                                 style="background-color: ${colorMateria}15; color: ${colorMateria}; border: 1px solid ${colorMateria}40;">
                                <small class="fw-bold lh-sm">${nombreMateria.replace(/ /g, '<br>')}</small>
                            </div>
                        `;
                    } else {
                        bloqueHTML = `
                            <div class="bloque-horario bg-${colorMateria} bg-opacity-10 text-${colorMateria} rounded-3 w-100 p-2">
                                <small class="fw-bold lh-sm">${nombreMateria.replace(/ /g, '<br>')}</small>
                            </div>
                        `;
                    }

                    tdDia.innerHTML = bloqueHTML;
                }

                tr.appendChild(tdDia);
            });

            tbody.appendChild(tr);
        });

    } catch (error) {
        console.error("Error cargando el horario:", error);
        Swal.fire({
            icon: 'error',
            title: 'Error de servidor',
            text: 'No pudimos armar tu horario. Intenta recargar la página.',
            toast: true,
            position: 'top'
        });
    }
}
async function tareasNotificaciones(){
    try{
        const response = await fetch("/tareasRecordatorio")
        const tareas = await response.json();

        const contenedor = document.getElementById("contenedor-notificaciones");
        contenedor.innerHTML = "";
        tareas.forEach(tarea =>{
            const li = document.createElement("li");
            li.innerHTML = `
            <a class="dropdown-item py-2 px-3 rounded small d-flex align-items-start">
                        <i class="bi bi-exclamation-circle-fill text-danger me-2 mt-1"></i>
                        <div>
                            <strong class="d-block text-wrap" >${tarea.titulo} de ${tarea.materia.nombre}</strong>
                            <span class="text-muted text-wrap">Vence el ${tarea.fecha_entrega}</span>
                        </div>
                    </a>`
            contenedor.appendChild(li);
        })

    }catch(error){console.error(error)}
}
////////////////////////////////////////////////////FRONTEND///////////////////////////////////////////////////////////////////////

function agregarFilaHorario() {
    const contenedor = document.getElementById('contenedorHorarios');
    const nuevaFila = document.createElement('div');
    nuevaFila.className = 'row g-2 mb-2 fila-horario';
    nuevaFila.innerHTML = `
        <div class="col-md-4">
            <select name="dia[]" class="form-select form-select-sm" required>
                <option value="Lunes">Lunes</option>
                <option value="Martes">Martes</option>
                <option value="Miércoles">Miércoles</option>
                <option value="Jueves">Jueves</option>
                <option value="Viernes">Viernes</option>
                <option value="Sábado">Sábado</option>
            </select>
        </div>
        <div class="col-md-3">
            <input type="time" name="horaInicio[]" class="form-control form-select-sm" required>
        </div>
        <div class="col-md-3">
            <input type="time" name="horaFinal[]" class="form-control form-select-sm" required>
        </div>
        <div class="col-md-2 d-flex align-items-end">
            <button type="button" class="btn btn-sm btn-outline-danger w-100" onclick="eliminarFila(this)">
                <i class="bi bi-trash"></i>
            </button>
        </div>
    `;
    contenedor.appendChild(nuevaFila);
}
function eliminarFila(boton) {
    const filas = document.querySelectorAll('.fila-horario');
    if (filas.length > 1) {
        boton.closest('.fila-horario').remove();
    } else {
        Swal.fire({
            icon: 'warning',
            title: 'Atención',
            text: 'La materia debe tener al menos un horario.',
            toast: true,
            position: 'top'
        });
    }
}
function sweetAlertError() {
    if (window.alertaError) {
        const temaActual = document.documentElement.getAttribute('data-bs-theme') || 'light';

        const Toast = Swal.mixin({
            toast: true,
            position: 'top',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            background: temaActual === 'dark' ? '#212529' : '#fff',
            color: temaActual === 'dark' ? '#fff' : '#000',
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        });

        Toast.fire({
            icon: 'error',
            title: window.alertaError
        });
    }
}
function sweetAlertSuccess() {
    if (window.alertaSuccess) {
        const temaActual = document.documentElement.getAttribute('data-bs-theme') || 'light';

        const Toast = Swal.mixin({
            toast: true,
            position: 'top',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            background: temaActual === 'dark' ? '#212529' : '#fff',
            color: temaActual === 'dark' ? '#fff' : '#000',
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        });

        Toast.fire({
            icon: 'success',
            title: window.alertaSuccess
        });
    }
}
function cambiarTema() {
    const html = document.documentElement;
    const temaActual = html.getAttribute('data-bs-theme') || 'light';
    const nuevoTema = temaActual === 'dark' ? 'light' : 'dark';

    html.setAttribute('data-bs-theme', nuevoTema);
    localStorage.setItem('temaUniPlanner', nuevoTema);
    actualizarIconoTema(nuevoTema);
}
function actualizarIconoTema(tema) {
    const icono = document.querySelector('#btn-modo-oscuro i');
    if (icono) {
        if (tema === 'dark') {
            icono.className = 'bi bi-sun-fill text-warning';
        } else {
            icono.className = 'bi bi-moon-stars-fill text-secondary';
        }
    }
}
function prepararEntrega(checkbox, nombreTarea, idTarea) {
    if (checkbox.checked) {
        checkbox.checked = false;
        checkboxActual = checkbox;
        document.getElementById('nombre-tarea-entregar').textContent = nombreTarea;
        document.getElementById('id_tareaCompletada').value = idTarea;
        modalEntregaInstancia = new bootstrap.Modal(document.getElementById('modalEntregarTarea'));
        modalEntregaInstancia.show();
    }
}
function cancelarEntrega() {
    checkboxActual = null;
}
function cambiarPanel(panelDestino) {
    const panelTareas = document.getElementById('panel-tareas');
    const panelHorario = document.getElementById('panel-horario');

    if (panelDestino === 'tareas') {
        panelTareas.classList.remove('d-none');
        panelHorario.classList.add('d-none');
    } else {
        panelTareas.classList.add('d-none');
        panelHorario.classList.remove('d-none');
    }
}
function actualizarReloj() {
    const ahora = new Date();
    const opcionesFecha = { weekday: 'long', day: 'numeric', month: 'long' };
    let fechaTexto = ahora.toLocaleDateString('es-ES', opcionesFecha);
    fechaTexto = fechaTexto.charAt(0).toUpperCase() + fechaTexto.slice(1);
    const opcionesHora = { hour: '2-digit', minute: '2-digit', hour12: true };
    const horaTexto = ahora.toLocaleTimeString('es-ES', opcionesHora);
    const relojElemento = document.getElementById('fecha-actual');
    if (relojElemento) relojElemento.textContent = `${fechaTexto} | ${horaTexto}`;
}
let fechaBase = new Date();
function renderizarCalendario() {
    const contenedorDias = document.getElementById('calendario-dias');
    const etiquetaMes = document.getElementById('mes-nombre');
    if (!contenedorDias || !etiquetaMes) return;
    const año = fechaBase.getFullYear();
    const mes = fechaBase.getMonth();
    const opcionesMes = { month: 'long', year: 'numeric' };
    etiquetaMes.textContent = fechaBase.toLocaleDateString('es-ES', opcionesMes).toUpperCase();
    const primerDia = new Date(año, mes, 1).getDay();
    const ultimoDia = new Date(año, mes + 1, 0).getDate();
    contenedorDias.innerHTML = '';
    for (let i = 0; i < primerDia; i++) {
        const vacio = document.createElement('div');
        vacio.className = 'p-1 opacity-25';
        contenedorDias.appendChild(vacio);
    }
    const hoy = new Date();
    for (let dia = 1; dia <= ultimoDia; dia++) {
        const diaElemento = document.createElement('div');
        diaElemento.className = 'p-1 small calendar-day-hover';
        diaElemento.textContent = dia;
        if (dia === hoy.getDate() && mes === hoy.getMonth() && año === hoy.getFullYear()) {
            diaElemento.classList.add('bg-primary', 'text-white', 'rounded-circle');
        }
        contenedorDias.appendChild(diaElemento);
    }
}
function cambiarMes(direccion) {
    fechaBase.setMonth(fechaBase.getMonth() + direccion);
    renderizarCalendario();
}



