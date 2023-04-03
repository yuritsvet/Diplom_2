import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserClient;
import model.UserRandomGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserCreateTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserRandomGenerator.random();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void createUserNewTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        response.assertThat().log().all()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Попытка создания уже существующего пользователя")
    public void createUserExistTest() {
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        userClient.create(user);
        ValidatableResponse responseAgain = userClient.create(user);
        responseAgain.assertThat().body("message", equalTo("User already exists"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserNoPasswordTest() {
        user.setPassword("");
        ValidatableResponse response = userClient.create(user);
        if (response.extract().statusCode() == 200) {
            accessToken = response.extract().path("accessToken").toString();
        }
        response.assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @After
    public void cleanUserData() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
