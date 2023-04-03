import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserEditDataTest {
    private User user;
    private UserClient userClient;
    private String accessToken;
    private String changeName = "ThisIsNewName";
    private String changeEmail = "new-email@ya.ru";

    @Before
    public void setUp() {
        user = UserRandomGenerator.random();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение имени авторизованного пользователя")
    public void changeNameAuthorizedUserTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        user.setName(changeName);
        ValidatableResponse changeResponse = userClient.change(UserChange.from(user), accessToken);
        changeResponse.assertThat()
                .body("user.name", equalTo(changeName))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Изменение email авторизованного пользователя")
    public void changeLoginAuthorizedUserTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        user.setEmail(changeEmail);
        ValidatableResponse changeResponse = userClient.change(UserChange.from(user), accessToken);
        changeResponse.assertThat()
                .body("user.email",equalTo(changeEmail))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Изменение имени неавторизованного пользователя")
    public void changeNameUnauthorizedUserTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        String accessTokenClean = accessToken;
        user.setName(changeName);
        accessTokenClean = "";
        ValidatableResponse changeResponse = userClient.change(UserChange.from(user), accessTokenClean);
        changeResponse.assertThat()
                .body("message", equalTo("You should be authorised"))
                .and().statusCode(401);
    }

    @Test
    @DisplayName("Изменение email неавторизованного пользователя")
    public void changeEmailUnauthorizedUserTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        String accessTokenClean = accessToken;
        user.setName(changeEmail);
        accessTokenClean = "";
        ValidatableResponse changeResponse = userClient.change(UserChange.from(user), accessTokenClean);
        changeResponse.assertThat()
                .body("message", equalTo("You should be authorised"))
                .and().statusCode(401);
    }

    @After
    public void cleanUserData() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}