package com.example.pikafast.Controlador;


import com.example.pikafast.DTO.ChatRequestDTO;
import com.example.pikafast.DTO.ChatResponseDTO;
import com.example.pikafast.Enums.Rol;
import com.example.pikafast.Servicio.PikaChatKnowledgeService;
import com.example.pikafast.Servicio.PikaChatKnowledgeService.ChatIntent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/chat")
public class AIChatController {

    private final ChatClient client;
    private final PikaChatKnowledgeService chatKnowledgeService;

    public AIChatController(ChatClient client, PikaChatKnowledgeService chatKnowledgeService) {
        this.client = client;
        this.chatKnowledgeService = chatKnowledgeService;
    }

    @GetMapping
    public String chat(@RequestParam(value = "message") String message, Authentication authentication){
        return buildChatResponse(message, authentication).getContent();
    }

    @GetMapping("/full")
    public ChatResponseDTO chatFullResponse(@RequestParam(value = "message") String message, Authentication authentication){
        return buildChatResponse(message, authentication);
    }

    @PostMapping("/full")
    public ChatResponseDTO chatFullResponse(@RequestBody ChatRequestDTO request, Authentication authentication){
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensaje no puede estar vacio");
        }

        return buildChatResponse(request.getMessage(), authentication);
    }

    private ChatResponseDTO buildChatResponse(String message, Authentication authentication) {
        ChatIntent intent = chatKnowledgeService.classifyIntent(message);

        if (intent == ChatIntent.GREETING) {
            return new ChatResponseDTO(chatKnowledgeService.getGreetingMessage(), 0, 0, 0);
        }

        if (intent == ChatIntent.OUT_OF_SCOPE) {
            return new ChatResponseDTO(chatKnowledgeService.getOffTopicMessage(), 0, 0, 0);
        }

        String authenticatedEmail = getAuthenticatedEmail(authentication);
        Rol authenticatedRole = getAuthenticatedRole(authentication);

        if (intent == ChatIntent.ORDER_STATUS && authenticatedEmail == null) {
            return new ChatResponseDTO("Para consultar el estado de tus pedidos debes iniciar sesion.", 0, 0, 0);
        }

        if (intent == ChatIntent.ORDER_STATUS && !chatKnowledgeService.canAccessOrderStatus(authenticatedRole)) {
            return new ChatResponseDTO("Solo los usuarios cliente pueden consultar el estado de sus pedidos.", 0, 0, 0);
        }

        String systemPrompt = chatKnowledgeService.buildSystemPrompt(message, authenticatedEmail, intent);
        ChatResponse chatResponse = client.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        int promptTokens = chatResponse.getMetadata().getUsage().getPromptTokens();
        int completionTokens = chatResponse.getMetadata().getUsage().getCompletionTokens();
        int totalTokens = chatResponse.getMetadata().getUsage().getTotalTokens();

        return  new ChatResponseDTO(content, promptTokens, completionTokens, totalTokens);
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return authentication.getName();
    }

    private Rol getAuthenticatedRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_CLIENTE".equals(authority.getAuthority())) {
                return Rol.CLIENTE;
            }
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return Rol.ADMIN;
            }
        }

        return null;
    }
}
