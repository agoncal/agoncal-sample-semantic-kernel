package org.agoncal.samples.sk;

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

public class SayAJoke {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SayAJoke.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().withOpenAIClient(client).setModelId("deploy-semantic-kernel").build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("FunSkill", "simple/src/main/resources", "FunSkill");
    CompletionSKFunction jokeFunction = skill.getFunction("Joke", CompletionSKFunction.class);

    // Asks for a Joke
    SKContext jokeContext = SKBuilders.context().build();
    jokeContext.setVariable("style", "a poem written in the style of Shakespeare");
    jokeContext.setVariable("input", "about dinosaur");
    Mono<SKContext> result = jokeFunction.invokeAsync(jokeContext);

    LOGGER.info("The joke is: \n\n{}", result.block().getResult());
  }
}
