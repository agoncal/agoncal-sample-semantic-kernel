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

public class NarrateAFightInOnePrompt {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NarrateAFightInOnePrompt.class);

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
    CompletionSKFunction fightFunction = skill.getFunction("Fight", CompletionSKFunction.class);

    // Ask for a Joke
    SKContext fightContext = SKBuilders.context().build();
    fightContext.setVariable("villain_name", "Darth Vader");
    fightContext.setVariable("villain_powers", "Accelerated Healing, Agility, Astral Projection, Cloaking, Danger Sense, Durability, Electrokinesis, Energy Blasts, Enhanced Hearing, Enhanced Senses, Force Fields, Hypnokinesis, Illusions, Intelligence, Jump, Light Control, Marksmanship, Precognition, Psionic Powers, Reflexes, Stealth, Super Speed, Telekinesis, Telepathy, The Force, Weapons Master");
    fightContext.setVariable("villain_level", "13");
    fightContext.setVariable("hero_name", "Chewbacca");
    fightContext.setVariable("hero_powers", "Agility, Longevity, Marksmanship, Natural Weapons, Stealth, Super Strength, Weapons Master");
    fightContext.setVariable("hero_level", "5");
    Mono<SKContext> result = fightFunction.invokeAsync(fightContext);

    LOGGER.info("The narration for the fight is: \n\n{}", result.block().getResult());
  }
}
