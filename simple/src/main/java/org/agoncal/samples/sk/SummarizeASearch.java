// Copyright (c) Microsoft. All rights reserved.
package org.agoncal.samples.sk;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.KernelConfig;
import com.microsoft.semantickernel.builders.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.AIProviderSettings;
import com.microsoft.semantickernel.connectors.ai.openai.util.AzureOpenAISettings;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class SummarizeASearch {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendEmail.class);

  public static void main(String[] args) throws IOException {

    // Create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("src/main/resources/conf.properties");
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(new AzureKeyCredential(settings.getKey())).buildAsyncClient();

    // Create an instance of the TextCompletion service and register it for the Kernel configuration
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, settings.getDeploymentName());
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("text-completion", kernel -> textCompletion).build();

    // Instantiates the Kernel and registers skills
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
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
      @SKFunctionInputAttribute
      @SKFunctionParameters(description = "Text to search", name = "input")
      String input) {
      return Mono.just("Gran Torre Santiago is the tallest building in South America");
    }
  }
}
