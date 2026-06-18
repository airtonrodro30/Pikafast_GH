(() => {
    const CLAVE_CARRITO = "carrito";
    const COSTO_ENVIO = 12;

    const listaProductos = document.getElementById("pago-lista-productos");
    const carritoVacio = document.getElementById("pago-carrito-vacio");
    const subtotalLabel = document.getElementById("pago-subtotal-label");
    const subtotalElemento = document.getElementById("pago-subtotal");
    const envioElemento = document.getElementById("pago-envio");
    const totalElemento = document.getElementById("pago-total");
    const botonFinalizar = document.getElementById("btn-finalizar-compra");

    function leerCarrito() {
        const carritoGuardado = localStorage.getItem(CLAVE_CARRITO);
        if (!carritoGuardado) {
            return [];
        }

        try {
            const carrito = JSON.parse(carritoGuardado);
            return Array.isArray(carrito) ? carrito : [];
        } catch (error) {
            console.error("No se pudo leer el carrito en pago.", error);
            return [];
        }
    }

    function formatearMoneda(monto) {
        return `S/. ${monto.toFixed(2)}`;
    }

    function crearImagenProducto(producto) {
        if (producto.imagen) {
            const imagen = document.createElement("img");
            imagen.className = "payment-product-image";
            imagen.src = producto.imagen;
            imagen.alt = producto.titulo || "Producto";
            imagen.loading = "lazy";
            imagen.decoding = "async";
            return imagen;
        }

        const placeholder = document.createElement("div");
        placeholder.className = "payment-product-placeholder";
        placeholder.innerHTML = '<i class="bi bi-image"></i>';
        return placeholder;
    }

    function renderizarResumen() {
        const carrito = leerCarrito();
        listaProductos.innerHTML = "";

        const tieneProductos = carrito.length > 0;
        carritoVacio.classList.toggle("d-none", tieneProductos);
        listaProductos.classList.toggle("d-none", !tieneProductos);

        const subtotal = carrito.reduce((sum, producto) => sum + (producto.precio * producto.cantidad), 0);
        const totalProductos = carrito.reduce((sum, producto) => sum + producto.cantidad, 0);
        const envio = tieneProductos ? COSTO_ENVIO : 0;
        const total = subtotal + envio;

        subtotalLabel.textContent = `Subtotal (${totalProductos} productos)`;
        subtotalElemento.textContent = formatearMoneda(subtotal);
        envioElemento.textContent = formatearMoneda(envio);
        totalElemento.textContent = formatearMoneda(total);
        botonFinalizar.disabled = !tieneProductos;

        if (!tieneProductos) {
            return;
        }

        carrito.forEach((producto) => {
            const item = document.createElement("article");
            item.className = "payment-summary-item";

            const subtotalProducto = producto.precio * producto.cantidad;
            item.innerHTML = `
                <div class="payment-product-info">
                    <h3>${producto.titulo}</h3>
                    <p>${formatearMoneda(producto.precio)} x ${producto.cantidad}</p>
                </div>
                <div class="payment-product-subtotal">${formatearMoneda(subtotalProducto)}</div>
            `;

            item.prepend(crearImagenProducto(producto));
            listaProductos.appendChild(item);
        });
    }

    function obtenerMetodoSeleccionado() {
        const metodoSeleccionado = document.querySelector('input[name="metodoPago"]:checked');
        return metodoSeleccionado ? metodoSeleccionado.value : null;
    }

    document.addEventListener("DOMContentLoaded", () => {
        renderizarResumen();

        botonFinalizar.addEventListener("click", () => {
            if (botonFinalizar.disabled) {
                return;
            }

            console.log("Carrito a pagar:", leerCarrito());
            console.log("Método de pago seleccionado:", obtenerMetodoSeleccionado());
        });
    });

    window.addEventListener("storage", (event) => {
        if (event.key === CLAVE_CARRITO) {
            renderizarResumen();
        }
    });
})();
