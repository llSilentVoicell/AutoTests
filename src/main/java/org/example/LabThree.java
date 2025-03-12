package org.example;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class LabThree {
    private static final String BASE_URL = "https://b6e5b7c4-8bb1-4367-a72c-79e904e77561.mock.pstmn.io";
    private static final String GET_SUCCESS = "/data/success";
    private static final String GET_FORBIDDEN = "/data/unsuccess";
    private static final String POST = "/create";
    private static final String PUT_SERVER_ERROR = "/update";
    private static final String DELETE_GONE = "/delete";

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyGetSuccess() {
        given()
                .get(GET_SUCCESS)
                .then()
                .statusCode(HttpStatus.SC_OK) // 200 OK
                .body("status", equalTo("success"))
                .body("data", notNullValue());
    }

    @Test
    public void verifyGetForbidden() {
        given()
                .get(GET_FORBIDDEN)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN) // 403 Forbidden
                .body("status", equalTo("error"))
                .body("message", containsString("Access denied"));
    }

    @Test
    public void verifyPostSuccess() {
        given()
                .queryParam("permission", "yes")
                .post(POST)
                .then()
                .statusCode(HttpStatus.SC_OK) // 200 OK
                .body("status", equalTo("created"))
                .body("message", equalTo("Resource successfully created."));
    }

    @Test
    public void verifyPostBadRequest() {
        given().post(POST)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST) // 400 Bad Request
                .body("status", equalTo("error"))
                .body("message", equalTo("Permission denied. Cannot create resource."));
    }

    @Test
    public void verifyPutServerError() {
        Map<String, ?> updateBody = Map.of(
                "id", 123,
                "name", "UpdatedResource"
        );

        given()
                .body(updateBody)
                .put(PUT_SERVER_ERROR)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                .body("status", equalTo("error"))
                .body("message", containsString("Server failure"));
    }
    
    @Test
    public void verifyDeleteGone() {
        given()
                .delete(DELETE_GONE)
                .then()
                .statusCode(HttpStatus.SC_GONE) // 410 Gone
                .body("status", equalTo("deleted"))
                .body("message", containsString("Resource no longer available"));
    }
}