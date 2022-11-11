package tournaments;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class Collection {
    RequestSpecification request;
    String token;
    String id;

    @BeforeTest(groups = {"smoke"})
    public void getLocalHost() {
        request = given()
                .baseUri("http://localhost:3000");
    }

    @BeforeTest(groups = {"smoke"})
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
    @Test (priority = 1,groups = {"smoke"})
    public void getAllTournamentsDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Tournaments")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 2,groups = {"smoke"})
    public void getAllTournamentsDataWithAuthorization() {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .get("/Tournaments")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }
    @Test (priority = 3,groups = {"smoke"})
    public void addNewTournamentNotAuthorized()
    {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","PL League");
        tournamentData.put("Duration in days","16500");
        tournamentData.put("Reward in EGP","880");


        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(tournamentData)
                .when()
                        .post("/660/Tournaments")
                .then()
                        .assertThat().extract().response();
        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 4,groups = {"smoke"})
    public void addNewTournamentWithAuthorization() throws InterruptedException {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","PL League");
        tournamentData.put("Duration in days","16500");
        tournamentData.put("Reward in EGP","880");

        Response res =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .auth().oauth2(token)
                        .body(tournamentData)
                .when()
                        .post("/660/Tournaments")
                .then()
                        .extract().response();
        id = Integer.toString( res.path("id"));
        Thread.sleep(2000);
    }
    @Test (priority = 5,groups = {"smoke"})
    public void modifyTournamentDataNoAuthorization()
    {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","PL League");
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

    @Test (priority = 6,groups = {"smoke"})
    public void modifyTournamentDataWithAuthorization() throws InterruptedException {
        HashMap<String,String> tournamentData = new HashMap<>();
        tournamentData.put("Tournment Name","PL League");
        tournamentData.put("Duration in days","165");
        tournamentData.put("Reward in EGP","880");

        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(tournamentData)
        .when()
                .put("/660/Tournaments/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
        Thread.sleep(2000);
    }
    @Test (priority = 7,groups = {"smoke"})
    public void deleteTournamentDataNotAuthorized()
    {
        given()
                .spec(request)
        .when()
                .delete("/660/Tournaments/"+id)
        .then()
                .assertThat().statusCode(401);
    }

    @Test (priority = 8,groups = {"smoke"})
    public void deleteTournamentDataWithAuthorization()
    {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .delete("/660/Tournaments/"+id)
        .then()
                .assertThat().statusCode(200);
    }
}
