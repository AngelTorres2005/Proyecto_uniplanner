document.addEventListener("DOMContentLoaded", function() {
    //FRONTEND
    const temaGuardado = localStorage.getItem('temaUniPlanner') || 'light';
    document.documentElement.setAttribute('data-bs-theme', temaGuardado);
    //actualizarIconoTema(temaGuardado);
    sweetAlertError();
    sweetAlertSuccess();

    //BACKEND
    datosPerfil();
})


////////////////////////FRONTEND/////////////////////////////
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


////////////////////////BACKEND/////////////////////////////
async function datosPerfil(){
    try {
        const response = await fetch("/datosPerfil");
        const perfil = await response.json();
        console.log(perfil);
        
        document.getElementById('input-perfil-nombre').value = perfil.nombre || '';
        document.getElementById('input-perfil-apellidos').value = perfil.apellidos || '';
        document.getElementById('input-perfil-correo').value = perfil.correo || '';
    } catch (error) {
        console.error('Error al cargar datos del perfil:', error);
    }
}