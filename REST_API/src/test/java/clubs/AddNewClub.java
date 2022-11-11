package clubs;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class AddNewClub {
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
    public void addNewClubNotAuthorized()
    {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Ghazl");
        clubData.put("Main Uniform","Blue");
        clubData.put("Club Rank","16");
        clubData.put("Sub Uniform","White");
        clubData.put("Coach Name","Omar");

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

    @Test
    public void addNewClubWithAuthorization()
    {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Dakhlya");
        clubData.put("Main Uniform","Blue");
        clubData.put("Club Rank","16");
        clubData.put("Sub Uniform","White");
        clubData.put("Coach Name","Hosam");

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
    }
    }

