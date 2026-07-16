document.addEventListener("DOMContentLoaded", () => {
    // -- LOGIN
    const passwordInput = document.querySelector('#password-input');
    const btnTogglePassword = document.querySelector(".btn-toggle-pass");
    
    //Funciones
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

