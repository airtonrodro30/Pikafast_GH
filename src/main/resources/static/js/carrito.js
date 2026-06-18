(() => {
    const CLAVE_CARRITO = "carrito";
    const EVENTO_CARRITO_ACTUALIZADO = "carrito:actualizado";

    const carritoLista = document.getElementById("carrito-lista");
    const carritoVacio = document.getElementById("carrito-vacio");
    const resumenCantidad = document.getElementById("resumen-cantidad");
    const resumenSubtotal = document.getElementById("resumen-subtotal");
    const resumenTotal = document.getElementById("resumen-total");
    const botonPagar = document.querySelector(".pay-button");

    function leerCarrito() {
        const carritoGuardado = localStorage.getItem(CLAVE_CARRITO);
        if (!carritoGuardado) {
            return [];
        }

        try {
            const carrito = JSON.parse(carritoGuardado);
            return Array.isArray(carrito) ? carrito : [];
        } catch (error) {
            console.error("No se pudo parsear el carrito.", error);
            return [];
        }
    }

    function guardarCarrito(carrito) {
        localStorage.setItem(CLAVE_CARRITO, JSON.stringify(carrito));
        window.dispatchEvent(new CustomEvent(EVENTO_CARRITO_ACTUALIZADO, {
            detail: {carrito}
        }));
    }

    function formatearMoneda(monto) {
        return `S/. ${monto.toFixed(2)}`;
    }

    function crearImagenProducto(producto) {
        if (producto.imagen) {
            const imagen = document.createElement("img");
            imagen.className = "catalog-item-image";
            imagen.src = producto.imagen;
            imagen.alt = producto.titulo || "Producto";
            imagen.loading = "lazy";
            imagen.decoding = "async";
            return imagen;
        }

        const placeholder = document.createElement("div");
        placeholder.className = "catalog-item-placeholder";
        placeholder.innerHTML = '<i class="bi bi-image"></i>';
        return placeholder;
    }

    function recalcularResumen(carrito) {
        const totalArticulos = carrito.reduce((sum, producto) => sum + producto.cantidad, 0);
        const totalGeneral = carrito.reduce((sum, producto) => sum + (producto.precio * producto.cantidad), 0);

        resumenCantidad.textContent = totalArticulos;
        resumenSubtotal.textContent = formatearMoneda(totalGeneral);
        resumenTotal.textContent = formatearMoneda(totalGeneral);
        botonPagar.disabled = carrito.length === 0;
    }

    function actualizarCantidad(indice, delta) {
        const carrito = leerCarrito();
        const producto = carrito[indice];

        if (!producto) {
            return;
        }

        producto.cantidad += delta;

        if (producto.cantidad <= 0) {
            carrito.splice(indice, 1);
        }

        guardarCarrito(carrito);
        renderizarCarrito();
    }

    function eliminarProducto(indice) {
        const carrito = leerCarrito();
        carrito.splice(indice, 1);
        guardarCarrito(carrito);
        renderizarCarrito();
    }

    function renderizarCarrito() {
        const carrito = leerCarrito();
        carritoLista.innerHTML = "";

        const tieneProductos = carrito.length > 0;
        carritoVacio.classList.toggle("d-none", tieneProductos);
        carritoLista.classList.toggle("d-none", !tieneProductos);

        if (!tieneProductos) {
            recalcularResumen([]);
            return;
        }

        carrito.forEach((producto, indice) => {
            const subtotal = producto.precio * producto.cantidad;
            const descripcionHtml = producto.descripcion
                ? `<p class="catalog-description">${producto.descripcion}</p>`
                : "";
            const item = document.createElement("article");
            item.className = "catalog-item";

            const itemBody = document.createElement("div");
            itemBody.className = "catalog-item-body";
            itemBody.innerHTML = `
                <div class="catalog-item-top">
                    <h2>${producto.titulo}</h2>
                    <button type="button" class="btn btn-link p-0 border-0 catalog-trash" aria-label="Eliminar producto">
                        <i class="bi bi-trash3"></i>
                    </button>
                </div>
                <div class="catalog-item-bottom">
                    <div>
                        <div class="catalog-price">Precio: ${formatearMoneda(producto.precio)}</div>
                        <div class="catalog-subtotal">Subtotal: ${formatearMoneda(subtotal)}</div>
                        ${descripcionHtml}
                    </div>
                    <div class="catalog-qty">
                        <button type="button" class="btn-disminuir" aria-label="Disminuir cantidad">-</button>
                        <input type="text" value="${producto.cantidad}" readonly aria-label="Cantidad">
                        <button type="button" class="btn-aumentar" aria-label="Aumentar cantidad">+</button>
                    </div>
                </div>
            `;

            item.appendChild(crearImagenProducto(producto));
            item.appendChild(itemBody);

            itemBody.querySelector(".btn-disminuir").addEventListener("click", () => actualizarCantidad(indice, -1));
            itemBody.querySelector(".btn-aumentar").addEventListener("click", () => actualizarCantidad(indice, 1));
            itemBody.querySelector(".catalog-trash").addEventListener("click", () => eliminarProducto(indice));

            carritoLista.appendChild(item);
        });

        recalcularResumen(carrito);
    }

    document.addEventListener("DOMContentLoaded", renderizarCarrito);

    if (botonPagar) {
        botonPagar.addEventListener("click", () => {
            if (!botonPagar.disabled) {
                window.location.href = "/pago";
            }
        });
    }

    window.addEventListener(EVENTO_CARRITO_ACTUALIZADO, renderizarCarrito);
    window.addEventListener("storage", (event) => {
        if (event.key === CLAVE_CARRITO) {
            renderizarCarrito();
        }
    });
})();
