package org.agoncal.samples.superheroes;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.builders.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.skilldefinition.ReadOnlyFunctionCollection;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public class WriteSuperHeroBio {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WriteSuperHeroBio.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, "deploy-semantic-kernel");

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
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
