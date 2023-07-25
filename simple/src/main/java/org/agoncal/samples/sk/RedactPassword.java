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
import com.microsoft.semantickernel.planner.actionplanner.Plan;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import org.slf4j.Logger;

import java.io.IOException;

public class RedactPassword {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RedactPassword.class);

  public static void main(String[] args) throws IOException {

    // Create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("src/main/resources/conf.properties");
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(new AzureKeyCredential(settings.getKey())).buildAsyncClient();

    // Create an instance of the TextCompletion service and register it for the Kernel configuration
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, settings.getDeploymentName());
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("text-completion", kernel -> textCompletion).build();

    // Instantiates the Kernel and registers skills
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
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
      @SKFunctionInputAttribute String input) {
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
