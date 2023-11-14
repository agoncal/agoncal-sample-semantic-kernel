package org.agoncal.samples.sk.model;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ChatModelTest {

    @Test
    void shouldCreateARequest() {
        ChatModel.Message msg1 = new ChatModel.Message();
        msg1.content = "Hello";
        msg1.role = "user";
        ChatModel.Message msg2 = new ChatModel.Message();
        msg2.content = "How are you?";
        msg2.role = "user";

        ChatModel.ChatRequestOverrides overrides = new ChatModel.ChatRequestOverrides();
        overrides.excludeCategory = "excluded category";
        overrides.promptTemplate = "prompt template";
        overrides.promptTemplatePrefix = "prompt template prefix";
        overrides.retrievalMode = ChatModel.RetrievalMode.TEXT;
        overrides.top = 100;
        overrides.semanticCaptions = true;
        overrides.temperature = 0.234;
        overrides.semanticRanker = false;

        ChatModel.ChatRequest requestOptions = new ChatModel.ChatRequest();
        requestOptions.chunkIntervalMs = 1000;
        requestOptions.apiUrl = "http://api.url";
        requestOptions.approach = ChatModel.Approaches.RRR;
        requestOptions.suggestFollowupQuestions = false;
        requestOptions.overrides = overrides;
        requestOptions.stream = false;
        requestOptions.messages = List.of(msg1, msg2);

        JsonbConfig config = new JsonbConfig().withFormatting(true);
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();
        String json = jsonb.toJson(requestOptions);
        System.out.println(json);
    }

    @Test
    void shouldCreateAResponse() {
        ChatModel.ChatResponse response = new ChatModel.ChatResponse();
        response.choices = List.of(new ChatModel.Choice(1, "I am good, thank you", "assistant"), new ChatModel.Choice(2, "And what about you", "assistant"));
        response.error = "no error";

        JsonbConfig config = new JsonbConfig().withFormatting(true);
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();
        String json = jsonb.toJson(response);
        System.out.println(json);
    }
}
