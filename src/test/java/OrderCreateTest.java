import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private  OrderClient orderClient;
    private String accessToken;
    private String email;
    private String[] ingredients;
    private User user;
    private UserClient userClient;
    private String invalidIngredient = "asdfasdf";

    public OrderCreateTest(String[] ingredients) {
        this.ingredients = ingredients;
    }

    @Before
    public void setting() {
        orderClient = new OrderClient();
        user = UserRandomGenerator.random();
        userClient = new UserClient();

    }

    @Parameterized.Parameters
    public static Object[] ingredientsTestData() {
        return new Object[][]{
                {new String[]{"61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa70"}}
        };
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreateTestNoAuthorized() {
        Order order = new Order(ingredients);
        accessToken = "";
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .body( "order.number", is(notNullValue()))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов")
    public void orderCreateTestNoAuthorizedNoIngredients() {
        Order order = new Order(null);
        accessToken = "";
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .body( "message", equalTo("Ingredient ids must be provided"))
                .and().statusCode(400);
    }

    @Test
    @DisplayName("Создание заказа с неверным ингредиентом без авторизации")
    public void orderCreateTestNoAuthorizedInvalidIngredients() {
        Order order = new Order();
        order.setIngredients(new String[]{invalidIngredient});
        accessToken = "";
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа c авторизацией")
    public void orderCreateTestAuthorized() {
        Order order = new Order(ingredients);
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        email = response.extract().path("user.email").toString();
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .body( "order.owner.email", equalTo(email))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа c авторизацией, без ингредиента")
    public void orderCreateTestAuthorizedNoIngredients() {
        Order order = new Order(null);
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        email = response.extract().path("user.email").toString();
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .body( "message", equalTo("Ingredient ids must be provided"))
                .and().statusCode(400);
    }

    @Test
    @DisplayName("Создание заказа с невалидным ингредиентом c авторизацией")
    public void orderCreateTestAuthorizedInvalidIngredients() {
        Order order = new Order(ingredients);
        order.setIngredients(new String[]{invalidIngredient});
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        email = response.extract().path("user.email").toString();
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        responseOrder.assertThat().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Получение заказа авторизованного пользователя")
    public void orderGetTestAuthorized() {
        Order order = new Order(ingredients);
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        ValidatableResponse responseGet = orderClient.getOrders(accessToken);
        responseGet.assertThat().log().all()
                .body("success", is(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Получение заказа неавторизованного пользователя")
    public void orderGetTestNoAuthorized() {
        Order order = new Order(ingredients);
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString();
        ValidatableResponse responseOrder = orderClient.create(order, accessToken);
        accessToken = "";
        ValidatableResponse responseGet = orderClient.getOrders(accessToken);
        responseGet.assertThat().log().all()
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