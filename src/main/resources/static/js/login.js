document.addEventListener('DOMContentLoaded', () => {
    const temaGuardado = localStorage.getItem('temaUniPlanner') || 'light';
    document.documentElement.setAttribute('data-bs-theme', temaGuardado);
    actualizarIconoTema(temaGuardado);
    sweetAlertError();
    sweetAlertSuccess();
});

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
