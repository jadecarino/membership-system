package synoptic.project.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import synoptic.project.rest.employee.Employee;

public class AccountEntityTests extends AccountTests {

    private static final String JSONFIELD_NAME = "name";
    private static final String JSONFIELD_PHONENUMBER = "phoneNumber";
    private static final String JSONFIELD_EMAILADDRESS = "emailAddress";
    private static final String JSONFIELD_COMPANY = "company";
    private static final String JSONFIELD_CARDNUMBER = "cardNumber";

    private static final String EMPLOYEE_NAME = "JadeCarino";
    private static final String EMPLOYEE_PHONENUMBER = "07500100200";
    private static final String EMPLOYEE_EMAILADDRESS = "jade@email.com";
    private static final String EMPLOYEE_COMPANY = "IBM";
    private static final String EMPLOYEE_CARDNUMBER = "12345678";

    private static final String INVALID_CARD_NUMBER = "-1";

    private static final int OK_CODE = Status.OK.getStatusCode();
    private static final int NOT_FOUND_CODE = Status.NOT_FOUND.getStatusCode();
    private static final int BAD_REQUEST_CODE = Status.BAD_REQUEST.getStatusCode();

    private static final String ADMIN_USERNAME = "jade";
    private static final String ADMIN_PASSWORD = "jadepwd";

    @BeforeAll
    public static void beforeAll() {
        loginPort = "6000";
        loginBaseUrl = "http://localhost:" + loginPort + "/membership-system/login";

        membershipSystemPort = "9080";
        baseUrl = "http://localhost:" + membershipSystemPort + "/membership-system";
    }

    @BeforeEach
    public void beforeEach() {
        form = new Form();
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        employeeForm = new HashMap<String, String>();
        employeeForm.put(JSONFIELD_NAME, EMPLOYEE_NAME);
        employeeForm.put(JSONFIELD_PHONENUMBER, EMPLOYEE_PHONENUMBER);
        employeeForm.put(JSONFIELD_EMAILADDRESS, EMPLOYEE_EMAILADDRESS);
        employeeForm.put(JSONFIELD_COMPANY, EMPLOYEE_COMPANY);
        employeeForm.put(JSONFIELD_CARDNUMBER, EMPLOYEE_CARDNUMBER);

        adminLoginResponse = loginRequest(ADMIN_USERNAME, ADMIN_PASSWORD);
        adminJwt = adminLoginResponse.readEntity(JsonObject.class).getString("jwt").toString();
 
    }

    @Test
    public void testReadBalance(){
        int getResponse = getRequest(adminJwt, INVALID_CARD_NUMBER).getStatus();
        assertEquals(NOT_FOUND_CODE, getResponse,
            "Trying to read an Account balance with a Card Number that doesn't exist should return the HTTP response code " + NOT_FOUND_CODE);
    }

    @Test
    public void testTopUpBalance(){
        /** Create an Employee to open an Account */
        postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        Employee e = new Employee(EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        String cardNumber = findEmployee(adminJwt, e).getString("cardNumber");
        int updateResponse = topUpRequest(adminJwt, cardNumber, 100).getStatus();
        assertEquals(OK_CODE, updateResponse,
            "Trying to top up an Account balance should return the HTTP response code " + OK_CODE);

        int balance = getRequest(adminJwt, e.getCardNumber()).readEntity(JsonObject.class).getInt("balance");
        assertEquals(balance, 100,
            "The account balance should equal " + 100);

        updateResponse = topUpRequest(adminJwt, cardNumber, -100).getStatus();
        assertEquals(BAD_REQUEST_CODE, updateResponse,
            "Trying to top up an Account balance with a negative amount should return the HTTP response code " + BAD_REQUEST_CODE);
    }

    @Test
    public void testPayForItem(){
        /** Create an Employee to open an Account */
        postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        Employee e = new Employee(EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        String cardNumber = findEmployee(adminJwt, e).getString("cardNumber");
        int updateResponse = payRequest(adminJwt, cardNumber, 20).getStatus();
        assertEquals(updateResponse, BAD_REQUEST_CODE,
            "Trying to pay for an item with insufficient funds should return the HTTP response code " + BAD_REQUEST_CODE);
        int balance = getRequest(adminJwt, e.getCardNumber()).readEntity(JsonObject.class).getInt("balance");
        assertEquals(balance, 0,
            "The account balance should equal " + 0);

        topUpRequest(adminJwt, cardNumber, 100);
        updateResponse = payRequest(adminJwt, cardNumber, 20).getStatus();
        
        balance = getRequest(adminJwt, e.getCardNumber()).readEntity(JsonObject.class).getInt("balance");
        assertEquals(balance, 80,
            "The account balance should equal " + 80);
        assertEquals(OK_CODE, updateResponse,
            "Trying to pay for an item with sufficient funds should return the HTTP response code " + OK_CODE);
    }

    @AfterEach
    public void afterEach() {
        clearDatabase(adminJwt);
        response.close();
        client.close();
    }
    
}