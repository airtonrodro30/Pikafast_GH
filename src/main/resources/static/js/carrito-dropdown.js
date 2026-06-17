const CLAVE_CARRITO = "carrito";
const EVENTO_CARRITO_ACTUALIZADO = "carrito:actualizado";

// Crear contenedores
const carritoIcono = document.getElementById("cart-icon");
const carritoContainer = document.getElementById("cart-container");

if (!carritoIcono || !carritoContainer) {
    throw new Error("No se encontraron los elementos base del carrito.");
}

carritoContainer.classList.add("show-cart-container", "hide-cart-container");

const productosContainer = document.createElement("div");
productosContainer.classList.add("products-container");

const carritoInfo = document.createElement("div");
carritoInfo.classList.add("cart-info");
carritoInfo.innerHTML = `
    <div class="cart-total">
        <h6>Total:</h6>
        <span class="total-pagar">S/. 0</span>
    </div>
    <a href="/carrito" class="btn btn-success">Realizar Pedido</a>
`;

// Mensaje "carrito vacío" separado
const mensajeVacio = document.createElement("p");
mensajeVacio.classList.add("cart-empty", "d-none");
mensajeVacio.textContent = "El carrito está vacío";

// Variables
let allProducts = [];

const valorTotal = carritoInfo.querySelector(".total-pagar");

function obtenerCarritoDesdeLocalStorage() {
    const carritoGuardado = localStorage.getItem(CLAVE_CARRITO);
    if (!carritoGuardado) {
        return [];
    }

    try {
        const carrito = JSON.parse(carritoGuardado);
        return Array.isArray(carrito) ? carrito : [];
    } catch (error) {
        console.error("No se pudo leer el carrito guardado.", error);
        return [];
    }
}

// Funciones auxiliares
function actualizarTotal() {
    const total = allProducts.reduce((sum, p) => sum + p.precio * p.cantidad, 0);
    valorTotal.textContent = `S/. ${total.toFixed(2)}`;
}

function actualizarEstadoCarrito() {
    const hayProductos = allProducts.length > 0;

    productosContainer.classList.toggle("d-none", !hayProductos);
    carritoInfo.classList.toggle("d-none", !hayProductos);
    mensajeVacio.classList.toggle("d-none", hayProductos);
}

function renderizarCarrito() {
    productosContainer.innerHTML = "";

    allProducts.forEach(producto => {
        const carritoItem = document.createElement("div");
        carritoItem.classList.add("cart-item");

        carritoItem.innerHTML = `
            <div class="d-flex align-items-center gap-1">
                <button class="btn btn-warning btn-sm reducir-item">
                    <i class="bi bi-dash"></i>
                </button>
                <h6 class="cantidad m-0">${producto.cantidad}</h6>
                <button class="btn btn-warning btn-sm aumentar-item">
                    <i class="bi bi-plus"></i>
                </button>
            </div>
            <figure class="img-container">
                <img src="${producto.imagen}" alt="producto" loading="lazy" decoding="async" />
            </figure>
            <div class="product-info">
                <div>
                    <h6 class="item-name">${producto.titulo}</h6>
                    <p class="item-price">S/. ${producto.precio.toFixed(2)}</p>
                </div>
                <button class="btn-delete-item btn btn-danger btn-lg">
                    <i class="bi bi-trash3-fill"></i>
                </button>
            </div>
        `;

        // Elementos clave para actualizar sin redibujar
        const cantidadElemento = carritoItem.querySelector(".cantidad");

        // ➖ Reducir
        carritoItem.querySelector(".reducir-item").addEventListener("click", () => {
            if (producto.cantidad > 1) {
                producto.cantidad--;
                cantidadElemento.textContent = producto.cantidad;
            } else {
                allProducts = allProducts.filter(p => p.titulo !== producto.titulo);
                carritoItem.remove();
            }
            actualizarTotal();
            actualizarEstadoCarrito();
            guardarCarritoEnLocalStorage();
        });


        // ➕ Aumentar
        carritoItem.querySelector(".aumentar-item").addEventListener("click", () => {
            producto.cantidad++;
            cantidadElemento.textContent = producto.cantidad; // solo actualiza cantidad
            actualizarTotal();
            guardarCarritoEnLocalStorage();
        });

        // 🗑️ Eliminar directo
        carritoItem.querySelector(".btn-delete-item").addEventListener("click", () => {
            allProducts = allProducts.filter(p => p.titulo !== producto.titulo);
            carritoItem.remove(); // elimina del DOM
            actualizarTotal();
            actualizarEstadoCarrito();
            guardarCarritoEnLocalStorage();
        });

        productosContainer.appendChild(carritoItem);
    });
}

// Evento agregar producto
document.addEventListener("DOMContentLoaded", () => {
    allProducts = obtenerCarritoDesdeLocalStorage();
    renderizarCarrito();
    actualizarTotal();
    actualizarEstadoCarrito();

    const botonesAgregar = document.querySelectorAll(".btn-add-cart");

    botonesAgregar.forEach(boton => {
        boton.addEventListener("click", () => {
            const card = boton.closest(".catalog-product-card");
            const nombre = card.querySelector(".product-title").textContent.trim();
            const precioTexto = card.querySelector(".product-price").textContent.trim();
            const match = precioTexto.match(/[\d]+(?:\.\d+)?/);
            const precioNumero = match ? parseFloat(match[0]) : 0;
            const imagen = card.querySelector("img").getAttribute("src");

            const index = allProducts.findIndex(p => p.titulo === nombre);
            if (index >= 0) {
                allProducts[index].cantidad++;
            } else {
                allProducts.push({titulo: nombre, precio: precioNumero, imagen, cantidad: 1});
            }

            renderizarCarrito();
            actualizarTotal();
            actualizarEstadoCarrito();
            guardarCarritoEnLocalStorage(); // ✅ Aquí se guarda el cambio
        });
    });

    actualizarEstadoCarrito(); // Solo por si acaso
});

// Mostrar/Ocultar carrito
// Bloquear la propagación del evento de Bootstrap
carritoIcono.addEventListener("click", (e) => {
    e.stopPropagation();
    carritoContainer.classList.toggle("hide-cart-container");
});

// Evita que se cierre el carrito al hacer clic dentro de él
carritoContainer.addEventListener("click", (e) => {
    e.stopPropagation();
});

// Guardar Datos en Local Storage
function guardarCarritoEnLocalStorage() {
    localStorage.setItem(CLAVE_CARRITO, JSON.stringify(allProducts));
    window.dispatchEvent(new CustomEvent(EVENTO_CARRITO_ACTUALIZADO, {
        detail: {carrito: [...allProducts]}
    }));
}

function sincronizarCarritoDesdeStorage() {
    allProducts = obtenerCarritoDesdeLocalStorage();
    renderizarCarrito();
    actualizarTotal();
    actualizarEstadoCarrito();
}

window.addEventListener(EVENTO_CARRITO_ACTUALIZADO, sincronizarCarritoDesdeStorage);
window.addEventListener("storage", (event) => {
    if (event.key === CLAVE_CARRITO) {
        sincronizarCarritoDesdeStorage();
    }
});

// Añadir contenedores al DOM
carritoContainer.appendChild(productosContainer);
carritoContainer.appendChild(carritoInfo);
carritoContainer.appendChild(mensajeVacio);
carritoIcono.parentElement.style.position = "relative";
carritoIcono.parentElement.appendChild(carritoContainer);
