package org.agoncal.samples.sk;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoncal.samples.sk.model.ChatModel;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {

  @Operation(summary = "Chat with the bot")
  @APIResponse(responseCode = "200", description = "Chat with the bot", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ChatModel.ChatResponse.class)))
  @POST
  public Response chat(ChatModel.ChatRequestOptions message) {
    return Response.ok("Hello from RESTEasy Reactive").build();
  }
}
