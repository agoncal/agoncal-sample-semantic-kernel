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
import reactor.core.publisher.Mono;

import java.io.IOException;

public class Joke {

  public static void main(String args[]) throws IOException {

    // First step is to create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("simple/src/main/resources/conf.properties");
    AzureKeyCredential credential = new AzureKeyCredential(settings.getKey());
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(credential).buildAsyncClient();


    // Next, we create an instance of the TextCompletion service configured for the text-davinci-003 model and register it for our Kernel configuration
    //TextCompletion textCompletionService = SKBuilders.textCompletionService().build(client, "gpt-35-turbo");
    // KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("gpt-35-turbo", k -> textCompletionService).build();
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("davinci", kernel -> SKBuilders.textCompletionService().build(client, "gpt-35-turbo")).build();

    // To register these skills into the Kernel while instantiating it, we perform the following
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("FunSkill", "simple/src/main/resources", "FunSkill");

    // Ask for a JOke
    CompletionSKFunction function = skill.getFunction("Joke", CompletionSKFunction.class);

    Mono<SKContext> result = function.invokeAsync("time travel to dinosaur age");

    if (result != null) {
      System.out.println(result.block().getResult());
    }
  }
}
