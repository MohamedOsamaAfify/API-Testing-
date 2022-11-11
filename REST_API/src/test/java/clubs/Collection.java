package clubs;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Properties;

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
    public void getAllClubsDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Clubs")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 2,groups = {"smoke"})
    public void getAllClubsDataWithAuthorization() {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .get("/Clubs")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 3,groups = {"smoke"})
    public void addNewClubNotAuthorized()
    {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Petrojet");
        clubData.put("Main Uniform","red");
        clubData.put("Club Rank","15");
        clubData.put("Sub Uniform","White");
        clubData.put("Coach Name","tarek");

        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(clubData)
                .when()
                        .post("/660/Clubs")
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String responseMsg = response.asString();
        String expectedMsg = "\"Missing authorization header\"";
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 4,groups = {"smoke"})
    public void addNewClubWithAuthorization() throws InterruptedException {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Petrojet");
        clubData.put("Main Uniform","red");
        clubData.put("Club Rank","15");
        clubData.put("Sub Uniform","White");
        clubData.put("Coach Name","tarek");

        Response res =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .auth().oauth2(token)
                        .body(clubData)
        .when()
                        .post("/660/Clubs")
        .then()
                        .extract().response();
        id = Integer.toString(res.path("id"));
        Thread.sleep(2000);
        //System.out.println(id);
    }

    @Test (priority = 5,groups = {"smoke"})
    public void modifyClubDataNoAuthorization()
    {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Petrojet");
        clubData.put("Main Uniform","red");
        clubData.put("Club Rank","15");
        clubData.put("Sub Uniform","White-red");
        clubData.put("Coach Name","omar");

        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(clubData)
                .when()
                        .put("/660/Clubs"+id)
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String responseMsg = response.asString();
        String expectedMsg = "\"Missing authorization header\"";
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 6,groups = {"smoke"})
    public void modifyClubDataWithAuthorization() throws InterruptedException {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Talaae3");
        clubData.put("Main Uniform","reeeed");
        clubData.put("Club Rank","15");
        clubData.put("Sub Uniform","White-red");
        clubData.put("Coach Name","omar");
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(clubData)
        .when()
                .put("/660/Clubs/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
        Thread.sleep(2000);
    }
    @Test (priority = 7,groups = {"smoke"})
    public void deleteClubDataNotAuthorized()
    {
        Response response =
                given()
                        .spec(request)
                .when()
                        .delete("/660/Clubs/"+id)
                .then()
                        .extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 8,groups = {"smoke"})
    public void deleteClubDataWithAuthorization()
    {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .delete("/660/Clubs/"+id)
        .then()
                .assertThat().statusCode(200);
    }
}
