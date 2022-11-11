package tournaments;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class ModifyTournamentData extends AddNewTournament {
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
    public void modifyTournamentDataNoAuthorization()
    {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","maichel");
        tournamentData.put("Duration in days","165");
        tournamentData.put("Reward in EGP","880");


        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(tournamentData)
                .when()
                        .put("/660/Tournaments")
                .then()
                        .assertThat().extract().response();
        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test
    public void modifyTournamentDataWithAuthorization()
    {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","maichel");
        tournamentData.put("Duration in days","165");
        tournamentData.put("Reward in EGP","880");

        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(tournamentData)
        .when()
                .put("/660/Tournament/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }
}
