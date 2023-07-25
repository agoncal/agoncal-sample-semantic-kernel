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
import com.microsoft.semantickernel.skilldefinition.ReadOnlyFunctionCollection;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class SayAJoke {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SayAJoke.class);

  public static void main(String args[]) throws IOException {

    // Create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("src/main/resources/conf.properties");
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(new AzureKeyCredential(settings.getKey())).buildAsyncClient();

    // Create an instance of the TextCompletion service and register it for our Kernel configuration
    TextCompletion textCompletionService = SKBuilders.textCompletionService().build(client, "text-embedding-ada-002");
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("ada", k -> textCompletionService).build();

    // Register skills into the Kernel and instantiate it
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("FunSkill", "src/main/resources", "FunSkill");

    // Ask for a Joke
    CompletionSKFunction function = skill.getFunction("Joke", CompletionSKFunction.class);

    Mono<SKContext> result = function.invokeAsync("time travel to dinosaur age");

    if (result != null) {
      System.out.println(result.block().getResult());
    }
  }
}
