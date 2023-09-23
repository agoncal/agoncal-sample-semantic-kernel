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

public class AnalyzeIncidentWithFrameworks {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AnalyzeIncidentWithFrameworks.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().withOpenAIClient(client).withModelId("deploy-semantic-kernel-16k").build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    ReadOnlyFunctionCollection skill = kernel.importSkillFromDirectory("AppCatSkill", "appcat/src/main/resources", "AppCatSkill");
    CompletionSKFunction function = skill.getFunction("AnalyzeWithFrameworks", CompletionSKFunction.class);

    // Asks for an analysis
    Mono<SKContext> result = function.invokeAsync();

    LOGGER.info("The analysis is: \n\n{}", result.block().getResult());
  }

}
