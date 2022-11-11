package transfers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class CreateNewTransfer {
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
    public void createNewTransferNotAuthorized()
    {
        HashMap<String,String> newTransfer = new HashMap<>();
        newTransfer.put("Player name","maichel");
        newTransfer.put("From","jj");
        newTransfer.put("To","kk");
        newTransfer.put("Cost","25571");


        Response response =
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .body(newTransfer)
        .when()
                .post("/660/Transfers")
        .then()
                .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test
    public void createNewTransferWithAuthorization()
    {
        HashMap<String,String> newTransfer = new HashMap<>();
        newTransfer.put("Player name","maichel");
        newTransfer.put("From","jj");
        newTransfer.put("To","kk");
        newTransfer.put("Cost","25571");

        Response res =
        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(newTransfer)
        .when()
                .post("/660/Transfers")
        .then()
                .extract().response();
        id = Integer.toString( res.path("id"));
    }
    }

