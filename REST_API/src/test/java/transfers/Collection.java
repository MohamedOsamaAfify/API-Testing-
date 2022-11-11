package transfers;

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
    public void getAllTransfersDataNoAuthorization() {

        given()
                .spec(request)
        .when()
                .get("/Transfers")
        .then()
                .assertThat().statusCode(200)
                .log().all();
    }

    @Test (priority = 2,groups = {"smoke"})
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

    @Test (priority = 3,groups = {"smoke"})
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

    @Test (priority = 4,groups = {"smoke"})
    public void createNewTransferWithAuthorization() throws InterruptedException {
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
        Thread.sleep(2000);
    }
    @Test (priority = 5,groups = {"smoke"})
    public void modifyTransferDataNoAuthorization()
    {
        HashMap<String,String> newTransfer = new HashMap<>();
        newTransfer.put("Player name","maichel ahmed");
        newTransfer.put("From","jj");
        newTransfer.put("To","kk");
        newTransfer.put("Cost","25571");


        Response response =
                given()
                        .spec(request)
                        .header("Content-Type","application/json")
                        .body(newTransfer)
                .when()
                        .put("/660/Transfers")
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 6,groups = {"smoke"})
    public void modifyTournamentDataWithAuthorization() throws InterruptedException {
        HashMap<String,String> newTransfer = new HashMap<>();
        newTransfer.put("Player name","maichel ahmed");
        newTransfer.put("From","jj");
        newTransfer.put("To","kk");
        newTransfer.put("Cost","25571");

        given()
                .spec(request)
                .header("Content-Type","application/json")
                .auth().oauth2(token)
                .body(newTransfer)
        .when()
                .put("/660/Transfers/"+id)
        .then()
                .assertThat().statusCode(200)
                .log().all();
        Thread.sleep(2000);
    }
    @Test (priority = 7,groups = {"smoke"})
    public void cancelTransferDataNotAuthorized()
    {
        Response response =
                given()
                        .spec(request)
                .when()
                        .delete("/660/Transfers/"+id)
                .then()
                        .assertThat().extract().response();

        Assert.assertEquals(response.statusCode(),401);

        String expectedMsg = "\"Missing authorization header\"";
        String responseMsg = response.asString();
        Assert.assertEquals(responseMsg,expectedMsg);
    }

    @Test (priority = 8,groups = {"smoke"})
    public void cancelTransferWithAuthorization()
    {
        given()
                .spec(request)
                .auth().oauth2(token)
        .when()
                .delete("/660/Transfers/"+id)
        .then()
                .assertThat().statusCode(200);
    }
}
