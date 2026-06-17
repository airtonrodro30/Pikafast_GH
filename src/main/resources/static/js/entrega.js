// assets/js/entrega.js

// 1. Función para cambiar visualización (Delivery vs Recojo)
function toggleDelivery(type) {
    const deliveryView = document.getElementById('content-delivery');
    const pickupView = document.getElementById('content-pickup');
    const deliveryPrice = document.getElementById('delivery-price-row');
    const montoText = document.getElementById('monto-delivery-text');
    
    document.querySelectorAll('.delivery-tab').forEach(t => t.classList.remove('active'));
    
    if (type === 'delivery') {
        document.getElementById('tab-delivery').classList.add('active');
        deliveryView.style.display = 'block';
        pickupView.style.display = 'none';
        deliveryPrice.style.display = 'flex';
        montoText.innerText = "S/. 15.00";
    } else {
        document.getElementById('tab-pickup').classList.add('active');
        deliveryView.style.display = 'none';
        pickupView.style.display = 'block';
        deliveryPrice.style.display = 'none';
        montoText.innerText = "S/. 00.00";
    }
}

// 2. Lógica específica para el Distrito
function validarDistritoInput(valor) {
    const entrada = valor.toLowerCase().trim();
    const zonasCobertura = [
        "huancayo", "carhuacallanga", "chacapampa", "chicche", "chilca", 
        "chongos alto", "chupuro", "colca", "cullhuas", "el tambo", 
        "huacrapuquio", "hualhuas", "huancán", "huasicancha", "huayucachi", 
        "ingenio", "pariahuanca", "pilcomayo", "pucará", "quichuay", 
        "quilcas", "san agustín", "san jerónimo de tunán", 
        "santo domingo de acobamba", "sapallanga", "sicaya", "viques"
    ];
    
    // Solo muestra el modal si el usuario escribió algo y NO está en la lista
    if (entrada !== "" && !zonasCobertura.some(zona => entrada.includes(zona))) {
        const modalElement = document.getElementById('noCoberturaModal');
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    }
}

// 3. Función vacía para dirección (así no hace nada al salir del campo)
function validarDireccion(direccion) {
    // Aquí no ponemos nada para que no dispare el modal
    console.log("Validación de dirección desactivada.");
}