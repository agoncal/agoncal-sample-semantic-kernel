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

  public static void main(String[] args) throws IOException {

    // Create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("src/main/resources/conf.properties");
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(new AzureKeyCredential(settings.getKey())).buildAsyncClient();

    // Create an instance of the TextCompletion service and register it for the Kernel configuration
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, settings.getDeploymentName());
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("text-completion", kernel -> textCompletion).build();

    // Instantiates the Kernel and registers skills
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("FunSkill", "src/main/resources", "FunSkill");
    CompletionSKFunction jokeFunction = skill.getFunction("Joke", CompletionSKFunction.class);

    // Ask for a Joke
    SKContext jokeContext = SKBuilders.context().build();
    jokeContext.setVariable("style", "a poem written in the style of Shakespeare");
    jokeContext.setVariable("input", "about dinosaur");
    Mono<SKContext> result = jokeFunction.invokeAsync(jokeContext);

    LOGGER.info("The joke is: \n\n{}", result.block().getResult());
  }
}
