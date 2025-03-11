package org.example.lab2;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class PetTest {
    private static final String baseUrl = "https://petstore.swagger.io/v2";

    private static final String PET = "/pet",
            PET_ID = PET + "/{petId}";

    private long petId;
    private String petName;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = baseUrl;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyCreatePet() {
        petId = Faker.instance().number().randomNumber();
        petName = Faker.instance().dog().name();

        Map<String, ?> body = Map.of(
                "id", petId,
                "name", petName,
                "status", "available"
        );

        given().body(body)
                .post(PET)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test(dependsOnMethods = "verifyCreatePet")
    public void verifyGetPet() {
        given().pathParam("petId", petId)
                .get(PET_ID)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("name", equalTo(petName));
    }

    @Test(dependsOnMethods = "verifyGetPet")
    public void verifyUpdatePet() {
        petName = Faker.instance().dog().name();

        Map<String, ?> body = Map.of(
                "id", petId,
                "name", petName,
                "status", "sold"
        );

        given().body(body)
                .put(PET)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("name", equalTo(petName));
    }

    @Test(dependsOnMethods = "verifyUpdatePet")
    public void verifyDeletePet() {
        given().pathParam("petId", petId)
                .delete(PET_ID)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}