document.addEventListener("DOMContentLoaded", () => {
    // Referencias DOM
    // -- CALENDARIO
    const datePicker = document.getElementById("date-picker");
    const displayDate = document.getElementById("display-date");
    const calendarTrigger = document.querySelector(".order-date-outer-container");
    
    // -- LOGIN
    const passwordInput = document.querySelector('#password-input');
    const btnTogglePassword = document.querySelector(".btn-toggle-pass");
    
    //CODIGO
    showCalendar(datePicker, displayDate, calendarTrigger);
    showAndHidePassword(passwordInput, btnTogglePassword);

});

// Mostrar/Ocultar contraseña
function showAndHidePassword(passwordInput, btnTogglePassword) {
    if (passwordInput && btnTogglePassword) {
        btnTogglePassword.addEventListener("click", () => {
            const input = document.getElementById("password-input");
            const icon = btnTogglePassword.querySelector("i");
            if (input.type === "password") {
                input.type = "text";
                icon.classList.replace("bi-eye-slash", "bi-eye");
            } else {
                input.type = "password";
                icon.classList.replace("bi-eye", "bi-eye-slash");
            }
        });
    }
}

// Mostrar Calendario
function showCalendar(datePicker, displayDate, calendarTrigger) {
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
}