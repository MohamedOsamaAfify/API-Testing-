package stadiums;

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
    public String  id;

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
                .then()
                        .extract().response();
        token = res.path("accessToken");
    }

    @Test (priority = 1,groups = {"smoke"})
    public void getAllStaduimsDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Staduims")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 2,groups = {"smoke"})
    public void getAllStaduimsDataWithAuthorization() {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .get("/Staduims")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 3,groups = {"smoke"})
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

    @Test(priority = 4,groups = {"smoke"})
    public void addNewStaduimWithAuthorization() throws InterruptedException {
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
        Thread.sleep(2000);
    }
    @Test (priority = 5,groups = {"smoke"})
    public void modifyStaduimDataNoAuthorization()
    {
        HashMap<String,String> staduimData = new HashMap<>();
        staduimData.put("Staduim Name","ac");
        staduimData.put("Location","berlin");
        staduimData.put("Capacity","13000");

        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(staduimData)
                .when()
                        .put("/660/Staduims")
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 6,groups = {"smoke"})
    public void modifyStaduimDataWithAuthorization() throws InterruptedException {
        HashMap<String,String> staduimData = new HashMap<>();
        staduimData.put("Staduim Name","ac");
        staduimData.put("Location","berlin");
        staduimData.put("Capacity","13000");
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(staduimData)
        .when()
                .put("/660/Staduims/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
        Thread.sleep(2000);
    }
    @Test (priority = 7,groups = {"smoke"})
    public void deleteStaduimDataNotAuthorized()
    {
        Response response =
                given()
                        .spec(request)
                .when()
                        .delete("/660/Staduims/"+id)
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 8,groups = {"smoke"})
    public void deleteStaduimDataWithAuthorization()
    {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .delete("/660/Staduims/"+id)
        .then()
                .assertThat().statusCode(200);
    }
}
