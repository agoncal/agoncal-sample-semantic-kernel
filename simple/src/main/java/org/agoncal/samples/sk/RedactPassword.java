package org.agoncal.samples.sk;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.planner.actionplanner.Plan;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;

public class RedactPassword {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RedactPassword.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().withOpenAIClient(client).setModelId("deploy-semantic-kernel").build();

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    kernel.importSkill(new PasswordSkill(), "PasswordSkill");

    // Creates a plan based on the prompt, and combines skills to perform a set of actions expected by the user
    SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

    Plan plan = planner.createPlanAsync("For any input with passwords, redact the passwords and send redacted input to sysadmin@corp.net").block();

    System.out.println("\n\n=============================== Plan to execute ===============================");
    System.out.println(plan.toPlanString());
    System.out.println("===============================================================================");

    // Set up the context
    SKContext emailContext = SKBuilders.context().build();
    emailContext.setVariable("subject", "Update");
    emailContext.setVariable("email", "sysadmin@corp.net");

    System.out.println("\n\nExecuting plan...");
    SKContext planResult = plan.invokeAsync(emailContext).block();

    System.out.println(" === Result of the plan === ");
    System.out.println(planResult.getResult());
  }

  public static class PasswordSkill {
    @DefineSKFunction(name = "redactPassword", description = "Redacts passwords from a message")
    public String redactPassword(
      @SKFunctionInputAttribute(description = "the input") String input) {
      System.out.println("[redactPassword] Redacting passwords from input: " + input);
      return input.replaceAll("password.*", "******");
    }

    @DefineSKFunction(name = "sendEmail", description = "Sends a message to an email")
    public String sendEmail(
      @SKFunctionParameters(name = "message") String message,
      @SKFunctionParameters(name = "email") String email) {
      return String.format("[sendEmail] Emailing to %s the following message: %n  ->  %s%n", email, message);
    }
  }
}
