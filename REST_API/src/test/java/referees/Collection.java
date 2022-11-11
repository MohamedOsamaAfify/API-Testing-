package referees;

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

    @BeforeTest (groups = {"smoke"})
    public void getLocalHost() {
        request = given()
                .baseUri("http://localhost:3000");
    }

    @BeforeTest (groups = {"smoke"})
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
    public void getAllRefereesDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Referees")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 2,groups = {"smoke"})
    public void getAllRefereesDataWithAuthorization() {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .get("/Referees")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }
    @Test (priority = 3,groups = {"smoke"})
    public void addNewRefereesNotAuthorized()
    {
        HashMap<String,String> refereeData = new HashMap<>();
        refereeData.put("Referee Name","maichel");
        refereeData.put("Height","165");
        refereeData.put("Weight","880");


        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(refereeData)
                .when()
                        .post("/660/Referees")
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 4,groups = {"smoke"})
    public void addNewRefereesWithAuthorization() throws InterruptedException {
        HashMap<String,String> refereeData = new HashMap<>();
        refereeData.put("Referee Name","maichel");
        refereeData.put("Height","165");
        refereeData.put("Weight","880");

        Response res =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .auth().oauth2(token)
                        .body(refereeData)
                .when()
                        .post("/660/Referees")
                .then()
                        .extract().response();
        id = Integer.toString( res.path("id"));
        Thread.sleep(2000);
    }
    @Test (priority = 5,groups = {"smoke"})
    public void modifyRefereesDataNoAuthorization()
    {
        HashMap<String,String> refereeData = new HashMap<>();
        refereeData.put("Referee Name","maichel");
        refereeData.put("Height","165");
        refereeData.put("Weight","88");


        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(refereeData)
                .when()
                        .put("/660/Referees"+id)
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 6,groups = {"smoke"})
    public void modifyRefereesDataWithAuthorization() throws InterruptedException {
        HashMap<String,String> refereeData = new HashMap<>();
        refereeData.put("Referee Name","maichel");
        refereeData.put("Height","165");
        refereeData.put("Weight","88");
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(refereeData)
        .when()
                .put("/660/Referees/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
        Thread.sleep(2000);
    }
    @Test (priority = 7,groups = {"smoke"})
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

    @Test (priority = 8,groups = {"smoke"})
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
