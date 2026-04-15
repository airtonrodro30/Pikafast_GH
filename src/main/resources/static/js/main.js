document.addEventListener("DOMContentLoaded", () => {
    // --- 1. LÓGICA DEL CALENDARIO ---
    const datePicker = document.getElementById("date-picker");
    const displayDate = document.getElementById("display-date");
    const calendarTrigger = document.querySelector(".order-date-outer-container");

    if (datePicker && displayDate) {
        // Forzar la apertura del calendario al hacer clic en cualquier parte del bloque amarillo o blanco
        if (calendarTrigger) {
            calendarTrigger.addEventListener("click", () => {
                try {
                    datePicker.showPicker(); // Método oficial para abrir el selector de fecha
                } catch (err) {
                    console.log("El navegador no soporta showPicker, usando click alternativo");
                    datePicker.click();
                }
            });
        }

        // Actualizar el texto cuando el usuario selecciona una fecha
        datePicker.addEventListener("change", (e) => {
            const date = new Date(e.target.value);
            
            // Ajuste de zona horaria para evitar que se reste un día
            date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
            
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            
            if (!isNaN(date.getTime())) {
                displayDate.textContent = `${day}/${month}/${year}`;
            }
        });
    }

    // --- 2. LÓGICA DEL MENÚ DE USUARIO ---
    renderUserMenu();
});

function renderUserMenu() {
    const userContainer = document.getElementById("user-menu-container");
    if (!userContainer) return;

    const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

    if (isLoggedIn) {
        userContainer.innerHTML = `
            <div class="dropdown">
                <div class="user-icon-bg" data-bs-toggle="dropdown" style="cursor:pointer;">
                    <i class="bi bi-person bi-custom"></i>
                </div>
                <ul class="dropdown-menu dropdown-menu-end shadow border-0" style="border-radius: 15px; overflow: hidden; margin-top: 10px;">
                    <li><a class="dropdown-item py-2 fw-bold text-center" href="perfil.html">PERFIL</a></li>
                    <li><hr class="dropdown-divider m-0"></li>
                    <li><button class="dropdown-item py-2 fw-bold text-center bg-light text-danger" id="logout-btn">CERRAR SESIÓN</button></li>
                </ul>
            </div>
        `;
        document.getElementById("logout-btn").addEventListener("click", () => {
            localStorage.setItem("isLoggedIn", "false");
            location.reload();
        });
    } else {
        userContainer.innerHTML = `
            <a href="login.html" class="text-decoration-none">
                <div class="user-icon-bg">
                    <i class="bi bi-person bi-custom"></i>
                </div>
            </a>
        `;
    }
}

// Lógica para mostrar/ocultar contraseña
const togglePass = document.querySelector('.btn-toggle-pass');
const passwordInput = document.querySelector('#password-input');

if (togglePass && passwordInput) {
    togglePass.addEventListener('click', () => {
        // Cambiar el tipo de input
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        
        // Cambiar el icono
        const icon = togglePass.querySelector('i');
        icon.classList.toggle('bi-eye');
        icon.classList.toggle('bi-eye-slash');
    });
}