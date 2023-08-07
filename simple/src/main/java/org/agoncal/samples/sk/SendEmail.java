package org.agoncal.samples.sk;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.builders.SKBuilders;
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

public class SendEmail {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendEmail.class);

  public static void main(String[] args) throws ConfigurationException {

    // Creates an Azure OpenAI client
    OpenAIAsyncClient client = OpenAIClientProvider.getClient();

    // Creates an instance of the TextCompletion service
    TextCompletion textCompletion = SKBuilders.chatCompletion().build(client, "deploy-semantic-kernel");

    // Instantiates the Kernel
    Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletion).build();

    // Registers skills
    kernel.importSkill(new StringFunctions(), "StringFunctions");
    kernel.importSkill(new Emailer(), "Emailer");
    kernel.importSkill(new Names(), "Names");

    // Creates a plan based on the prompt, and combines skills to perform a set of actions expected by the user
    SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

    Plan plan = planner.createPlanAsync("Send the input as an email to Steven and make sure I use his preferred name in the email").block();

    System.out.println("\n\n=============================== Plan to execute ===============================");
    System.out.println(plan.toPlanString());
    System.out.println("===============================================================================");


    // Set up the context
    SKContext emailContext = SKBuilders.context().build();
    emailContext.setVariable("subject", "Update");
    emailContext.setVariable("message", "Hay Steven, just wanted to let you know I have finished.");

    System.out.println("\n\nExecuting plan...");
    SKContext planResult = plan.invokeAsync(emailContext).block();

    System.out.println(" === Result of the plan === ");
    System.out.println(planResult.getResult());
  }

  public static class StringFunctions {
    @DefineSKFunction(
      name = "stringReplace",
      description = "Takes a message and substitutes string 'from' to 'to'")
    public String stringReplace(
      @SKFunctionInputAttribute(description = "the input")
      @SKFunctionParameters(name = "input", description = "The string to perform the replacement on")
      String input,
      @SKFunctionParameters(name = "from", description = "The string to replace")
      String from,
      @SKFunctionParameters(name = "to", description = "The string to replace with")
      String to
    ) {
      return input.replaceAll(from, to);
    }
  }

  public static class Names {
    @DefineSKFunction(name = "getNickName", description = "Retrieves the nick name for a given user")
    public String getNickName(
      @SKFunctionInputAttribute(description = "the input")
      @SKFunctionParameters(name = "name", description = "The name of the person to get an nick name for")
      String name) {
      switch (name) {
        case "Steven":
          return "Code King";
        default:
          throw new RuntimeException("Unknown user: " + name);
      }
    }

  }

  public static class Emailer {
    @DefineSKFunction(name = "getEmailAddress", description = "Retrieves the email address for a given user")
    public String getEmailAddress(
      @SKFunctionInputAttribute(description = "the name")
      @SKFunctionParameters(name = "name", description = "The name of the person to get an email address for")
      String name) {
      switch (name) {
        case "Steven":
          return "codeking@example.com";
        default:
          throw new RuntimeException("Unknown user: " + name);
      }
    }

    @DefineSKFunction(name = "sendEmail", description = "Sends an email")
    public String sendEmail(
      @SKFunctionParameters(name = "subject", description = "The email subject") String subject,
      @SKFunctionParameters(name = "message", description = "The message to email") String message,
      @SKFunctionParameters(name = "emailAddress", description = "The emailAddress to send the message to") String emailAddress) {
      System.out.println("================= Sending Email ====================");
      System.out.printf("To: %s%n", emailAddress);
      System.out.printf("Subject: %s%n", subject);
      System.out.printf("Message: %s%n", message);
      System.out.println("====================================================");
      return "Message sent";
    }
  }
}

