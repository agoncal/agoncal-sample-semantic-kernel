package org.agoncal.samples.superheroes;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.ai.embeddings.EmbeddingGeneration;
import com.microsoft.semantickernel.builders.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.AzureOpenAISettings;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.memory.MemoryStore;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.skilldefinition.ReadOnlyFunctionCollection;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public class NarrateAFightWithMemory {

  private static final String MEMORY_COLLECTION_NAME = "SuperHeroesMemory";

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NarrateAFightWithMemory.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, "deploy-semantic-kernel");

    // Create an instance of the EmbeddingGeneration service
    EmbeddingGeneration textEmbeddingGeneration = SKBuilders.textEmbeddingGenerationService().build(client, "text-embedding-ada-002");

    // Create a memory store
    MemoryStore memoryStore = SKBuilders.memoryStore().build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel()
      .withDefaultAIService(textCompletion)
      .withDefaultAIService(textEmbeddingGeneration)
      .withMemoryStorage(memoryStore)
      .build();

    // Registers skills
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("SuperHeroesSkill", "src/main/resources", "SuperHeroesSkill");
    CompletionSKFunction fightFunction = skill.getFunction("FightInOnePrompt", CompletionSKFunction.class);
    CompletionSKFunction heroFunction = skill.getFunction("SuperHero", CompletionSKFunction.class);
    CompletionSKFunction villainFunction = skill.getFunction("SuperVillain", CompletionSKFunction.class);

    // Asks for the supper hero biography and stores it in memory
    SKContext heroContext = SKBuilders.context().build();
    heroContext.setVariable("hero_name", "Chewbacca");
    heroContext.setVariable("hero_powers", "Agility, Longevity, Marksmanship, Natural Weapons, Stealth, Super Strength, Weapons Master");
    heroContext.setVariable("hero_level", "5");
    Mono<SKContext> result = heroFunction.invokeAsync(heroContext);
    kernel.getMemory().saveInformationAsync(MEMORY_COLLECTION_NAME, result.toString(), "bioHero", null, null).block();

    // Asks for the supper villain biography and stores it in memory
    SKContext villainContext = SKBuilders.context().build();
    villainContext.setVariable("villain_name", "Darth Vader");
    villainContext.setVariable("villain_powers", "Accelerated Healing, Agility, Astral Projection, Cloaking, Danger Sense, Durability, Electrokinesis, Energy Blasts, Enhanced Hearing, Enhanced Senses, Force Fields, Hypnokinesis, Illusions, Intelligence, Jump, Light Control, Marksmanship, Precognition, Psionic Powers, Reflexes, Stealth, Super Speed, Telekinesis, Telepathy, The Force, Weapons Master");
    villainContext.setVariable("villain_level", "13");
    result = villainFunction.invokeAsync(heroContext);
    kernel.getMemory().saveInformationAsync(MEMORY_COLLECTION_NAME, result.toString(), "bioVillain", null, null).block();


    LOGGER.info("The narration for the fight is: \n\n{}", result.block().getResult());
  }
}
