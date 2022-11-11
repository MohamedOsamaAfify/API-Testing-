package referees;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class DeleteRefereeData extends AddNewReferee {
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
                .then()
                        .extract().response();
        token = res.path("accessToken");
    }

    @Test
    public void deleteRefereeDataNotAuthorized()
    {
        Response response =
        given()
                .spec(request)
        .when()
                .delete("/660/Referees/"+id)
        .then()
                .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test
    public void deleteRefereeDataWithAuthorization()
    {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .delete("/660/Referees/"+id)
        .then()
                .assertThat().statusCode(200);
    }
}
