package transfers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class ViewTransferData {
    RequestSpecification request;
    String token;

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
                .then().extract().response();
        token = res.path("accessToken");
    }
    @Test
    public void getAllTransfersDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Transfers")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test
    public void getAllTransfersDataWithAuthorization() {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .get("/Transfers")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }
}
