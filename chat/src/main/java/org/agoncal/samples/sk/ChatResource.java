package org.agoncal.samples.sk;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoncal.samples.sk.model.ChatModel;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {

    @Inject
    Logger LOGGER;

    @Operation(summary = "Chat with the bot")
    @APIResponse(responseCode = "200", description = "Chat with the bot", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChatModel.ChatResponse.class)))
    @POST
    public Response chat(ChatModel.ChatRequest message) {
        LOGGER.info("Message received: " + message);

        ChatModel.ChatResponse response = new ChatModel.ChatResponse();
        response.choices = List.of(new ChatModel.Choice(1, "I am good, thank you", "assistant"), new ChatModel.Choice(2, "And what about you", "assistant"));
        response.error = "no error";

        return Response.ok(response).build();
    }
}
