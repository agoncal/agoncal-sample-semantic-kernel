package org.agoncal.samples.superheroes;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.skilldefinition.ReadOnlyFunctionCollection;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public class NarrateAFightInOnePrompt {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NarrateAFightInOnePrompt.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().withOpenAIClient(client).setModelId("deploy-semantic-kernel").build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("SuperHeroesSkill", "src/main/resources", "SuperHeroesSkill");
    CompletionSKFunction fightFunction = skill.getFunction("FightInOnePrompt", CompletionSKFunction.class);

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
