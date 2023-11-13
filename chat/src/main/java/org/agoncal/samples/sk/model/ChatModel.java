package org.agoncal.samples.sk.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

import java.util.HashMap;
import java.util.List;

public class ChatModel {

    public static class Message {
        public String content;
        @Schema(name = "role", enumeration = {"user", "assistant", "system"})
        public String role;

        public Message(String content, String role) {
            this.content = content;
            this.role = role;
        }

        public Message() {
        }
    }

    public static class ChatDebugDetails {
        public String thoughts;
        public List<String> dataPoints;
    }

    public static class ChatMessageContext extends HashMap<String, Object> {
        public String thoughts;
        public List<String> dataPoints;
    }

    public static class ChatMessage extends Message {
        public ChatMessageContext context;

        public ChatMessage(String content, String role) {
            super(content, role);
        }
    }

    public static class ChatResponse {
        public List<Choice> choices;
        public String error;
    }

    public static class ChatResponseChunk {
        public List<ChoiceDelta> choices;
        public String error;
    }

    public static class Choice {
        @Schema(name = "index of the choice in the list of choices", description = "This can differ to the actual index in the case of streaming. In the non-streaming case it is always identificae to the position in the choices array, and hence redundant")
        public int index;
        public ChatMessage message;

        public Choice(int index, ChatMessage message) {
            this.index = index;
            this.message = message;
        }

        public Choice(int index, String content, String role ) {
            this.index = index;
            this.message = new ChatMessage(content, role);
        }
    }

    public static class ChoiceDelta {
        public int index;
        public PartialChatMessage delta;
    }

    public static class PartialChatMessage {
        public String content;
        public String role;
        public ChatMessageContext context;
    }

    public enum Approaches {
        RTR, RRR
    }

    public enum RetrievalMode {
        HYBRID, VECTORS, TEXT
    }

    public static class ChatRequestOptions {
        public List<Message> messages;
        @SchemaProperty(name = "stream", defaultValue = "false")
        public boolean stream;
        public Approaches approach;
        public boolean suggestFollowupQuestions;
        public int chunkIntervalMs;
        public String apiUrl;
        public ChatRequestOverrides overrides;
    }

    public static class ChatRequestOverrides {
        public RetrievalMode retrievalMode;
        public boolean semanticRanker;
        public boolean semanticCaptions;
        public String excludeCategory;
        public Integer top;
        public Double temperature;
        public String promptTemplate;
        public String promptTemplatePrefix;
        public String promptTemplateSuffix;
    }
}
