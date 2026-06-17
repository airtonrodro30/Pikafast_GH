import {showCalendar} from "./shared/calendar.js"

document.addEventListener("DOMContentLoaded", () => {
    // Referencias DOM
    // -- CALENDARIO
    const datePicker = document.getElementById("date-picker");
    const displayDate = document.getElementById("display-date");
    const calendarTrigger = document.querySelector(".order-date-outer-container");
    
    // -- LOGIN
    const passwordInput = document.querySelector('#password-input');
    const btnTogglePassword = document.querySelector(".btn-toggle-pass");
    
    //Funciones
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

