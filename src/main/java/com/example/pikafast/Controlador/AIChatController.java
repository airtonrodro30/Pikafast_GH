package com.example.pikafast.Controlador;


import com.example.pikafast.DTO.ChatResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class AIChatController {

    private final ChatClient client;

    public AIChatController(ChatClient client) {
        this.client = client;
    }

    @GetMapping
    public String chat(@RequestParam(value = "message") String message){
        return client.prompt().user(message).call().content();
    }

    @GetMapping("/full")
    public ChatResponseDTO chatFullResponse(@RequestParam(value = "message") String message){
        ChatResponse chatResponse = client.prompt().user(message).call().chatResponse();

        String text = chatResponse.getResult().getOutput().getText();
        int promptTokens = chatResponse.getMetadata().getUsage().getPromptTokens();
        int completionTokens = chatResponse.getMetadata().getUsage().getCompletionTokens();
        int totalTokens = chatResponse.getMetadata().getUsage().getTotalTokens();

        return  new ChatResponseDTO(text,promptTokens,completionTokens,totalTokens);
    }
}
