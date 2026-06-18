(() => {
    const CLAVE_CARRITO = "carrito";
    const COSTO_ENVIO = 12;
    const ENDPOINT_CONFIRMAR_COMPRA = "/api/compras/confirmar";

    const listaProductos = document.getElementById("pago-lista-productos");
    const carritoVacio = document.getElementById("pago-carrito-vacio");
    const subtotalLabel = document.getElementById("pago-subtotal-label");
    const subtotalElemento = document.getElementById("pago-subtotal");
    const envioElemento = document.getElementById("pago-envio");
    const totalElemento = document.getElementById("pago-total");
    const botonFinalizar = document.getElementById("btn-finalizar-compra");
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "";
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "X-CSRF-TOKEN";
    let compraEnProceso = false;

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

    function construirPayloadCompra() {
        const carrito = leerCarrito();

        return {
            metodoPago: obtenerMetodoSeleccionado(),
            tipoEnvio: "DELIVERY",
            items: carrito.map((producto) => ({
                idProducto: producto.idProducto,
                cantidad: producto.cantidad
            }))
        };
    }

    function validarPayload(payload) {
        if (!payload.metodoPago) {
            throw new Error("Debes seleccionar un método de pago.");
        }

        if (!Array.isArray(payload.items) || payload.items.length === 0) {
            throw new Error("No se puede confirmar una compra con el carrito vacío.");
        }

        payload.items.forEach((item) => {
            if (!Number.isInteger(item.idProducto)) {
                throw new Error("Hay productos en el carrito sin un identificador válido. Vuelve a agregarlos desde el catálogo.");
            }
            if (!Number.isInteger(item.cantidad) || item.cantidad <= 0) {
                throw new Error("Hay cantidades inválidas en el carrito.");
            }
        });
    }

    async function confirmarCompra() {
        const payload = construirPayloadCompra();
        validarPayload(payload);

        const headers = {
            "Content-Type": "application/json"
        };

        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(ENDPOINT_CONFIRMAR_COMPRA, {
            method: "POST",
            headers,
            body: JSON.stringify(payload)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok || data.ok === false) {
            throw new Error(data.mensaje || "No se pudo registrar la compra.");
        }

        localStorage.removeItem(CLAVE_CARRITO);
        window.dispatchEvent(new CustomEvent("carrito:actualizado", {
            detail: {carrito: []}
        }));

        alert(data.mensaje || "Compra registrada correctamente.");
        window.location.href = "/carrito";
    }

    document.addEventListener("DOMContentLoaded", () => {
        renderizarResumen();

        botonFinalizar.addEventListener("click", async () => {
            if (botonFinalizar.disabled || compraEnProceso) {
                return;
            }

            compraEnProceso = true;
            botonFinalizar.disabled = true;

            try {
                await confirmarCompra();
            } catch (error) {
                console.error("Error al confirmar la compra.", error);
                alert(error.message || "Ocurrió un error al confirmar la compra.");
                compraEnProceso = false;
                renderizarResumen();
            }
        });
    });

    window.addEventListener("storage", (event) => {
        if (event.key === CLAVE_CARRITO) {
            renderizarResumen();
        }
    });
})();
