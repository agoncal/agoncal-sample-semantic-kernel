package org.agoncal.sample.semantic.kernel;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class FightNarrationResourceTest {

  @Test
  public void testHelloEndpoint() {
    given()
      .when().get("/narration")
      .then()
      .statusCode(200)
      .body(is("Hello RESTEasy"));
  }
}
