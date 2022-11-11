package clubs;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class ModifyClubData extends AddNewClub {
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
    public void modifyClubDataNoAuthorization()
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
                        .put("/660/Clubs"+id)
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String responseMsg = response.asString();
        String expectedMsg = "\"Missing authorization header\"";
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test
    public void modifyClubDataWithAuthorization()
    {
        HashMap<String,String> clubData = new HashMap<>();
        clubData.put("Club Name","Talaae3");
        clubData.put("Main Uniform","reeeed");
        clubData.put("Club Rank","15");
        clubData.put("Sub Uniform","White");
        clubData.put("Coach Name","tarek");
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
    }
}
