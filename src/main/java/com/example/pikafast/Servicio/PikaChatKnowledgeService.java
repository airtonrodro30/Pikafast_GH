package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Categoria;
import com.example.pikafast.Entidad.DetallePedido;
import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Entidad.Producto;
import com.example.pikafast.Enums.Rol;
import com.example.pikafast.Repositorio.CategoriaRepositorio;
import com.example.pikafast.Repositorio.ClienteRepositorio;
import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import com.example.pikafast.Repositorio.ProductoRepositorio;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PikaChatKnowledgeService {

    private static final String OFF_TOPIC_MESSAGE = "Lo siento, pero solo puedo responder con cosas relacionadas a los productos o pedidos realizados";
    private static final String GREETING_MESSAGE = "Hola, dime cual es tu consulta";
    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("\\b(?:pedido|orden|compra)\\s*(?:numero\\s*)?(\\d+)\\b");

    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;

    private String baseKnowledgeDocument;

    public PikaChatKnowledgeService(ProductoRepositorio productoRepositorio,
            CategoriaRepositorio categoriaRepositorio,
            PedidoRepositorio pedidoRepositorio,
            ClienteRepositorio clienteRepositorio,
            DetallePedidoRepositorio detallePedidoRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.detallePedidoRepositorio = detallePedidoRepositorio;
    }

    @PostConstruct
    void loadKnowledgeDocument() throws IOException {
        ClassPathResource resource = new ClassPathResource("ai/pikafast-chat-knowledge.md");
        byte[] bytes = resource.getInputStream().readAllBytes();
        baseKnowledgeDocument = new String(bytes, StandardCharsets.UTF_8);
    }

    public String getOffTopicMessage() {
        return OFF_TOPIC_MESSAGE;
    }

    public String getGreetingMessage() {
        return GREETING_MESSAGE;
    }

    public ChatIntent classifyIntent(String message) {
        String normalizedMessage = normalize(message);

        if (normalizedMessage.isBlank()) {
            return ChatIntent.OUT_OF_SCOPE;
        }

        if (isGreetingIntent(normalizedMessage)) {
            return ChatIntent.GREETING;
        }

        if (isOrderDetailIntent(normalizedMessage)) {
            return ChatIntent.ORDER_DETAIL;
        }

        if (isOrderIntent(normalizedMessage)) {
            return ChatIntent.ORDER_STATUS;
        }

        if (isCheapestProductIntent(normalizedMessage)) {
            return ChatIntent.CHEAPEST_PRODUCT;
        }

        if (isCategoryIntent(normalizedMessage)) {
            return ChatIntent.CATEGORY_LIST;
        }

        if (isProductIntent(normalizedMessage)) {
            return ChatIntent.PRODUCT_LIST;
        }

        if (containsKnownProduct(normalizedMessage)) {
            return ChatIntent.PRODUCT_LIST;
        }

        if (containsKnownCategory(normalizedMessage)) {
            return ChatIntent.CATEGORY_LIST;
        }

        return ChatIntent.OUT_OF_SCOPE;
    }

    public boolean canAccessOrderStatus(Rol role) {
        return role == Rol.CLIENTE;
    }

    public String buildSystemPrompt(String userMessage, String authenticatedEmail, ChatIntent intent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(baseKnowledgeDocument).append("\n\n");
        prompt.append("Intencion detectada: ").append(intent.name()).append("\n\n");
        prompt.append(buildIntentInstructions(intent)).append("\n\n");
        prompt.append(buildCatalogSection()).append("\n\n");
        prompt.append(buildCategorySection()).append("\n\n");
        prompt.append(buildCheapestProductSection()).append("\n\n");
        prompt.append(buildOrderSection(authenticatedEmail)).append("\n\n");
        prompt.append(buildOrderDetailSection(userMessage, authenticatedEmail)).append("\n\n");
        prompt.append("Pregunta del usuario: ").append(userMessage);

        return prompt.toString();
    }

    private String buildCatalogSection() {
        List<Producto> availableProducts = productoRepositorio.findAll().stream()
                .filter(producto -> !Boolean.FALSE.equals(producto.getDisponible()))
                .sorted(Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (availableProducts.isEmpty()) {
            return "Productos disponibles: no hay productos disponibles registrados.";
        }

        String products = availableProducts.stream()
                .map(producto -> String.format(Locale.US, "- %s | categoria: %s | precio: %s | descripcion: %s",
                        safeValue(producto.getNombre()),
                        producto.getCategoria() != null ? safeValue(producto.getCategoria().getNombre()) : "Sin categoria",
                        formatPrice(producto.getPrecio()),
                        safeValue(producto.getDescripcion())))
                .collect(Collectors.joining("\n"));

        return "Productos disponibles en el catalogo:\n" + products;
    }

    private String buildCategorySection() {
        List<Categoria> categories = categoriaRepositorio.findAll().stream()
                .sorted(Comparator.comparing(Categoria::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (categories.isEmpty()) {
            return "Categorias disponibles: no hay categorias registradas.";
        }

        String categoryLines = categories.stream()
                .map(categoria -> String.format(Locale.US, "- %s | descripcion: %s",
                        safeValue(categoria.getNombre()),
                        safeValue(categoria.getDescripcion())))
                .collect(Collectors.joining("\n"));

        return "Categorias disponibles:\n" + categoryLines;
    }

    private String buildCheapestProductSection() {
        Optional<Producto> cheapestProduct = productoRepositorio.findAll().stream()
                .filter(producto -> !Boolean.FALSE.equals(producto.getDisponible()))
                .filter(producto -> producto.getPrecio() != null)
                .min(Comparator.comparing(Producto::getPrecio));

        if (cheapestProduct.isEmpty()) {
            return "Producto mas economico: no disponible.";
        }

        Producto producto = cheapestProduct.get();
        return String.format(Locale.US,
                "Producto mas economico:\n- %s | categoria: %s | precio: %s | descripcion: %s",
                safeValue(producto.getNombre()),
                producto.getCategoria() != null ? safeValue(producto.getCategoria().getNombre()) : "Sin categoria",
                formatPrice(producto.getPrecio()),
                safeValue(producto.getDescripcion()));
    }

    private String buildOrderSection(String authenticatedEmail) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            return "Estado de pedidos del usuario autenticado: no disponible porque el usuario no ha iniciado sesion.";
        }

        return clienteRepositorio.findByUsuarioEmail(authenticatedEmail)
                .map(cliente -> {
                    List<Pedido> orders = pedidoRepositorio.findByCliente_IdClienteOrderByFechaDesc(cliente.getIdCliente());

                    if (orders.isEmpty()) {
                        return "Estado de pedidos del usuario autenticado: el usuario no tiene pedidos registrados.";
                    }

                    String orderLines = orders.stream()
                            .limit(5)
                            .map(pedido -> String.format(Locale.US,
                                    "- Pedido #%d\nestado: %s\nfecha: %s\ntotal: %s\ntipo de envio: %s",
                                    pedido.getIdPedido(),
                                    pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "SIN_ESTADO",
                                    pedido.getFecha() != null ? pedido.getFecha().format(ORDER_DATE_FORMAT) : "Sin fecha",
                                    formatPrice(pedido.getPrecioTotal()),
                                    pedido.getTipoEnvio() != null ? pedido.getTipoEnvio().name() : "No definido"))
                            .collect(Collectors.joining("\n"));

                    return "Estado de pedidos del usuario autenticado:\n" + orderLines;
                })
                .orElse("Estado de pedidos del usuario autenticado: no se encontro un cliente asociado a la cuenta autenticada.");
    }

    private String buildOrderDetailSection(String userMessage, String authenticatedEmail) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            return "Detalle de pedido solicitado: no disponible porque el usuario no ha iniciado sesion.";
        }

        Optional<Integer> requestedOrderId = extractOrderId(userMessage);
        if (requestedOrderId.isEmpty()) {
            return "Detalle de pedido solicitado: no se solicito un numero de pedido especifico.";
        }

        return clienteRepositorio.findByUsuarioEmail(authenticatedEmail)
                .map(cliente -> pedidoRepositorio.findById(requestedOrderId.get())
                        .filter(pedido -> pedido.getCliente() != null
                                && Objects.equals(pedido.getCliente().getIdCliente(), cliente.getIdCliente()))
                        .map(this::formatOrderDetailSection)
                        .orElse("Detalle de pedido solicitado: el pedido indicado no pertenece al usuario autenticado o no existe."))
                .orElse("Detalle de pedido solicitado: no se encontro un cliente asociado a la cuenta autenticada.");
    }

    private String formatOrderDetailSection(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepositorio.findByPedido_IdPedido(pedido.getIdPedido());
        if (detalles.isEmpty()) {
            return "Detalle de pedido solicitado:\n"
                    + String.format(Locale.US, "Pedido #%d\nNo hay productos registrados para este pedido.", pedido.getIdPedido());
        }

        String detailLines = detalles.stream()
                .map(detalle -> String.format(Locale.US,
                        "- Pedido #%d\nproducto: %s\ncantidad: %s\nprecio unitario: %s\nsubtotal: %s",
                        pedido.getIdPedido(),
                        detalle.getProducto() != null ? safeValue(detalle.getProducto().getNombre()) : "Producto no disponible",
                        detalle.getCantidad() != null ? detalle.getCantidad() : 0,
                        formatPrice(detalle.getPrecioUnitario()),
                        formatPrice(detalle.getSubtotal())))
                .collect(Collectors.joining("\n\n"));

        return "Detalle de pedido solicitado:\n" + detailLines;
    }

    private String buildIntentInstructions(ChatIntent intent) {
        return switch (intent) {
            case PRODUCT_LIST -> """
                    Instrucciones de respuesta para PRODUCT_LIST:
                    - Responde solo con productos.
                    - No listes categorias si el usuario no las pidio.
                    - Prioriza nombre y precio.
                    - Solo menciona categoria si ayuda a diferenciar productos con nombres parecidos.
                    - Si haces una lista, usa un producto por linea.
                    """;
            case CATEGORY_LIST -> """
                    Instrucciones de respuesta para CATEGORY_LIST:
                    - Responde solo con categorias.
                    - No listes productos salvo que el usuario lo pida.
                    - Si es posible, menciona una descripcion breve por categoria.
                    """;
            case CHEAPEST_PRODUCT -> """
                    Instrucciones de respuesta para CHEAPEST_PRODUCT:
                    - Responde con el producto o los productos de menor precio.
                    - Prioriza nombre, precio y una descripcion corta.
                    - No listes todas las categorias salvo que el usuario las pida.
                    """;
            case ORDER_STATUS -> """
                    Instrucciones de respuesta para ORDER_STATUS:
                    - Responde solo con informacion de pedidos del usuario autenticado.
                    - No mezcles productos o categorias salvo que sea necesario para aclarar un pedido.
                    - Prioriza numero de pedido, estado, fecha y total.
                    - No uses el caracter | como separador.
                    - Cuando listes pedidos, coloca cada campo en una linea distinta.
                    """;
            case ORDER_DETAIL -> """
                    Instrucciones de respuesta para ORDER_DETAIL:
                    - Responde solo con el detalle del pedido especifico solicitado por el usuario autenticado.
                    - Si el usuario no indica un numero de pedido valido, dilo claramente.
                    - Prioriza numero de pedido, productos, cantidades, precio unitario y subtotal.
                    - No uses el caracter | como separador.
                    - Coloca cada campo en una linea distinta.
                    """;
            case GREETING -> """
                    Instrucciones de respuesta para GREETING:
                    - Responde exactamente: Hola, dime cual es tu consulta
                    """;
            case OUT_OF_SCOPE -> """
                    Instrucciones de respuesta para OUT_OF_SCOPE:
                    - Responde exactamente con el mensaje de fuera de alcance.
                    """;
        };
    }

    private boolean isGreetingIntent(String normalizedMessage) {
        return matchesAny(normalizedMessage,
                "hola", "holi", "hello", "buenas", "buenos dias", "buenas tardes", "buenas noches",
                "como estas", "que tal", "como te va", "me puedes ayudar", "puedes ayudarme",
                "me ayudas", "puedes ayudar", "estas ahi", "hay alguien")
                || (containsAny(normalizedMessage, "hola", "buenas", "ayuda", "ayudar")
                && normalizedMessage.split(" ").length <= 5);
    }

    private boolean isOrderDetailIntent(String normalizedMessage) {
        return extractOrderId(normalizedMessage).isPresent()
                && containsAny(normalizedMessage, "producto", "productos", "detalle", "detalles", "incluye", "incluyen", "contenido", "contiene", "cantidad", "cantidades");
    }

    private boolean isOrderIntent(String normalizedMessage) {
        return matchesAny(normalizedMessage,
                "estado de mi pedido", "estado de mis pedidos", "mi pedido", "mis pedidos",
                "mi orden", "mis ordenes", "como va mi pedido", "como van mis pedidos",
                "donde esta mi pedido", "pedido realizado", "pedidos realizados",
                "pedido pendiente", "pedidos pendientes", "pedido entregado",
                "pedido cancelado", "ultimo pedido", "ultimos pedidos",
                "ultima compra", "ultimas compras", "seguimiento de pedido")
                || (containsAny(normalizedMessage, "pedido", "pedidos", "orden", "ordenes", "compra", "compras")
                && containsAny(normalizedMessage, "estado", "seguimiento", "progreso", "situacion", "va", "van", "ultimo", "ultimos", "reciente", "recientes", "pendiente", "entregado", "cancelado"));
    }

    private boolean isCheapestProductIntent(String normalizedMessage) {
        return matchesAny(normalizedMessage,
                "mas economico", "mas economica", "mas barato", "mas barata",
                "producto barato", "producto economico", "menor precio", "precio mas bajo",
                "que producto cuesta menos", "que es lo mas barato", "lo mas economico",
                "lo mas barato", "que me recomiendas barato", "que recomiendas barato",
                "algo barato", "algo economico", "producto de menor precio")
                || (containsAny(normalizedMessage, "barato", "barata", "economico", "economica", "menor precio", "precio bajo")
                && containsAny(normalizedMessage, "producto", "productos", "catalogo", "venden", "tienen", "hay"));
    }

    private boolean isCategoryIntent(String normalizedMessage) {
        return matchesAny(normalizedMessage,
                "que categorias", "categorias disponibles", "categoria disponible",
                "tipos de productos", "secciones del catalogo", "que categorias tienen",
                "que tipos de productos tienen", "como se divide el catalogo",
                "que secciones hay", "cuales son las categorias", "cuales categorias tienen")
                || (containsAny(normalizedMessage, "categoria", "categorias", "secciones", "tipos")
                && containsAny(normalizedMessage, "hay", "tienen", "manejan", "disponibles", "catalogo", "venden"));
    }

    private boolean isProductIntent(String normalizedMessage) {
        return matchesAny(normalizedMessage,
                "catalogo", "catalogo disponible", "productos disponibles", "que productos",
                "que venden", "que hay", "productos en venta", "productos del catalogo",
                "que ofrecen", "que tienen disponible", "que me recomiendas",
                "recomendaciones de productos", "que snacks tienen", "que piqueos tienen")
                || (containsAny(normalizedMessage, "producto", "productos", "catalogo", "snacks", "piqueos")
                && containsAny(normalizedMessage, "hay", "tienen", "venden", "ofrecen", "disponibles", "recomiendas", "manejan"));
    }

    private boolean containsKnownProduct(String normalizedMessage) {
        return productoRepositorio.findAll().stream()
                .map(Producto::getNombre)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(name -> !name.isBlank() && normalizedMessage.contains(name));
    }

    private boolean containsKnownCategory(String normalizedMessage) {
        return categoriaRepositorio.findAll().stream()
                .map(Categoria::getNombre)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(name -> !name.isBlank() && normalizedMessage.contains(name));
    }

    private boolean matchesAny(String normalizedMessage, String... candidates) {
        for (String candidate : candidates) {
            if (normalizedMessage.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    private Optional<Integer> extractOrderId(String value) {
        String normalizedValue = normalize(value);
        Matcher matcher = ORDER_ID_PATTERN.matcher(normalizedValue);
        if (!matcher.find()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(matcher.group(1)));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private boolean containsAny(String normalizedMessage, String... candidates) {
        for (String candidate : candidates) {
            if (normalizedMessage.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "Sin detalle" : value;
    }

    private String formatPrice(BigDecimal value) {
        return value == null ? "Sin precio" : "S/. " + value.stripTrailingZeros().toPlainString();
    }

    public enum ChatIntent {
        GREETING,
        PRODUCT_LIST,
        CATEGORY_LIST,
        CHEAPEST_PRODUCT,
        ORDER_DETAIL,
        ORDER_STATUS,
        OUT_OF_SCOPE
    }
}
