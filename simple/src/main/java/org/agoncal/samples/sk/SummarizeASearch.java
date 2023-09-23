// Copyright (c) Microsoft. All rights reserved.
package org.agoncal.samples.sk;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public class SummarizeASearch {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendEmail.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().withOpenAIClient(client).withModelId("deploy-semantic-kernel").build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    kernel.importSkill(new SearchEngineSkill(), null);
    kernel.importSkillFromDirectory("SummarizeSkill", "src/main/resources", "SummarizeSkill");

    // Run
    String question = "What's the tallest building in South America?";
    LOGGER.info("The question: \"{}\"", question);

    Mono<SKContext> result = kernel.runAsync(question,
      kernel.getSkills().getFunction("Search", null)
    );

    LOGGER.info("The answer to the question: \"{}\"", result.block().getResult());

    result = kernel.runAsync(question,
      kernel.getSkills().getFunction("Search", null),
      kernel.getSkill("SummarizeSkill").getFunction("Summarize", null)
    );

    LOGGER.info("The summarized answer: \"{}\"", result.block().getResult());
  }

  public static class SearchEngineSkill {
    @DefineSKFunction(description = "Append the day variable", name = "search")
    public Mono<String> search(
      @SKFunctionInputAttribute(description = "the input")
      @SKFunctionParameters(description = "Text to search", name = "input")
      String input) {
      return Mono.just("Gran Torre Santiago is the tallest building in South America");
    }
  }
}
