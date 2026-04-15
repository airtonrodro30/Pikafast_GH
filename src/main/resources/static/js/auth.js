document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("login-form");
    const btnToggle = document.querySelector(".btn-toggle-pass");

    // Mostrar/Ocultar contraseña
    if (btnToggle) {
        btnToggle.addEventListener("click", () => {
            const input = document.getElementById("password-input");
            const icon = btnToggle.querySelector("i");
            if (input.type === "password") {
                input.type = "text";
                icon.classList.replace("bi-eye-slash", "bi-eye");
            } else {
                input.type = "password";
                icon.classList.replace("bi-eye", "bi-eye-slash");
            }
        });
    }

    // Simulación de Login
    if (loginForm) {
        loginForm.addEventListener("submit", (e) => {
            e.preventDefault();
            localStorage.setItem("isLoggedIn", "true");
            window.location.href = "home.html";
        });
    }
});