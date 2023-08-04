package org.agoncal.samples.superheroes;

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

public class WriteSuperHeroBio {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WriteSuperHeroBio.class);

  public static void main(String[] args) throws IOException {

    // Create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("src/main/resources/conf.properties");
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(new AzureKeyCredential(settings.getKey())).buildAsyncClient();

    // Create an instance of the TextCompletion service and register it for the Kernel configuration
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, settings.getDeploymentName());
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("text-completion", kernel -> textCompletion).build();

    // Instantiates the Kernel and registers skills
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("SuperHeroesSkill", "src/main/resources", "SuperHeroesSkill");
    CompletionSKFunction fightFunction = skill.getFunction("SuperHero", CompletionSKFunction.class);

    // Ask for a Joke
    SKContext bioContext = SKBuilders.context().build();
    bioContext.setVariable("hero_name", "Chewbacca");
    bioContext.setVariable("hero_powers", "Agility, Longevity, Marksmanship, Natural Weapons, Stealth, Super Strength, Weapons Master");
    bioContext.setVariable("hero_level", "5");
    Mono<SKContext> result = fightFunction.invokeAsync(bioContext);

    LOGGER.info("The bio of {}: \n\n{}", bioContext.getVariables().get("hero_name"), result.block().getResult());
  }
}
