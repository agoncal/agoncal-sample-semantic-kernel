package org.agoncal.samples.sk;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.KernelConfig;
import com.microsoft.semantickernel.builders.SKBuilders;
import com.microsoft.semantickernel.connectors.ai.openai.util.AIProviderSettings;
import com.microsoft.semantickernel.connectors.ai.openai.util.AzureOpenAISettings;
import com.microsoft.semantickernel.planner.actionplanner.Plan;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.textcompletion.TextCompletion;

import java.io.IOException;

public class PasswordRedact {

  public static void main(String args[]) throws IOException {

    // First step is to create an Azure OpenAI client
    AzureOpenAISettings settings = AIProviderSettings.getAzureOpenAISettingsFromFile("simple/src/main/resources/conf.properties");
    AzureKeyCredential credential = new AzureKeyCredential(settings.getKey());
    OpenAIAsyncClient client = new OpenAIClientBuilder().endpoint(settings.getEndpoint()).credential(credential).buildAsyncClient();


    // Next, we create an instance of the TextCompletion service configured for the text-davinci-003 model and register it for our Kernel configuration
    TextCompletion textCompletionService = SKBuilders.textCompletionService().build(client, "gpt-35-turbo");
    KernelConfig config = SKBuilders.kernelConfig().addTextCompletionService("gpt-35-turbo", k -> textCompletionService).build();

    // To register these skills into the Kernel while instantiating it, we perform the following
    Kernel kernel = SKBuilders.kernel().withKernelConfig(config).build();
    kernel.importSkill(new PasswordSkill(), "PasswordSkill");

    // We use the concept called Planner, which creates a plan based on the prompt, and combines skills to perform a set of actions expected by the user
    SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

    Plan plan = planner.createPlanAsync("For any input with passwords, redact the passwords and send redacted input to sysadmin@corp.net").block();

    System.out.println(plan.toPlanString());

    String message = "Password changed to password.db=123456abc";
    String result = plan.invokeAsync(message).block().getResult();

    System.out.println(" === Result of the plan === ");
    System.out.println(result);
  }
}
