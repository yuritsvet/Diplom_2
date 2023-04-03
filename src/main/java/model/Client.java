package model;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
public class Client {
    private final String START_URL = "https://stellarburgers.nomoreparties.site/";
    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(START_URL)
                .build();
    }
}