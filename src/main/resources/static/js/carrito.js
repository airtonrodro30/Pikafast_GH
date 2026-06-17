// Crear contenedores
const carritoIcono = document.getElementById("cart-icon");

const carritoContainer = document.getElementById("cart-container");
carritoContainer.classList.add("show-cart-container");

const productosContainer = document.createElement("div");
productosContainer.classList.add("products-container");

const carritoInfo = document.createElement("div");
carritoInfo.classList.add("cart-info");
carritoInfo.innerHTML = `
    <div class="cart-total">
        <h6>Total:</h6>
        <span class="total-pagar">S/. 0</span>
    </div>
    <button class="btn btn-success">Realizar Pedido</button>
`;

// Mensaje "carrito vacío" separado
const mensajeVacio = document.createElement("p");
mensajeVacio.classList.add("cart-empty", "d-none");
mensajeVacio.textContent = "El carrito está vacío";

// Añadir al DOM
carritoContainer.appendChild(productosContainer);
carritoContainer.appendChild(carritoInfo);
carritoContainer.appendChild(mensajeVacio);
// Añaadir carrito container al icono
carritoIcono.appendChild(carritoContainer);

// Variables
let allProducts = [];

const valorTotal = carritoInfo.querySelector(".total-pagar");

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
    // ⬇️ Recuperar carrito guardado al cargar
    const carritoGuardado = localStorage.getItem("carrito");
    if (carritoGuardado) {
        allProducts = JSON.parse(carritoGuardado);
        renderizarCarrito();
        actualizarTotal();
        actualizarEstadoCarrito();
    }

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



// Añadir contenedores al DOM
carritoContainer.appendChild(productosContainer);
carritoContainer.appendChild(carritoInfo);
carritoContainer.appendChild(mensajeVacio);
carritoIcono.parentElement.style.position = "relative"; // asegúrate que el padre tenga posición relativa
carritoIcono.parentElement.appendChild(carritoContainer);



// Guardar Datos en Local Storage
function guardarCarritoEnLocalStorage() {
    localStorage.setItem("carrito", JSON.stringify(allProducts));
}
