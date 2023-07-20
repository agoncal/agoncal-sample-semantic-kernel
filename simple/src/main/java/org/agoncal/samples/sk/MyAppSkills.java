package org.agoncal.samples.sk;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;

public class MyAppSkills {
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
