package stadiums;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class AddNewStaduim {
    RequestSpecification request;
    String token;
    public String  id;

    @BeforeTest
    public void getLocalHost() {
        request = given()
                .baseUri("http://localhost:3000");
    }

    @BeforeTest
    public void makeAuthorizationAndGenerateToken()
    {
        HashMap<String,String> loginData = new HashMap<>();
        loginData.put("email","olivier@mail.com");
        loginData.put("password","bestPassw0rd");

        Response res =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(loginData)
                .when()
                        .post("/login")
                .then()
                        .extract().response();
        token = res.path("accessToken");
    }

    @Test
    public void addNewStaduimNotAuthorized()
    {
        HashMap<String,String> staduimData = new HashMap<>();
        staduimData.put("Staduim Name","ac");
        staduimData.put("Location","roma");
        staduimData.put("Capacity","12000");

        Response response =
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .body(staduimData)
        .when()
                .post("/660/Staduims")
        .then()
                .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test
    public void addNewStaduimWithAuthorization()
    {
        HashMap<String,String> staduimData = new HashMap<>();
        staduimData.put("Staduim Name","ac");
        staduimData.put("Location","roma");
        staduimData.put("Capacity","12000");

        Response res =
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(staduimData)
        .when()
                .post("/660/Staduims")
        .then()
                .extract().response();
        id = Integer.toString( res.path("id"));
    }
    }

