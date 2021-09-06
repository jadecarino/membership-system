package synoptic.project.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

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

public class SecurityTests extends EmployeeTests {

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

    private static final String UPDATE_EMPLOYEE_NAME = "JohnSmith";
    private static final String UPDATE_EMPLOYEE_PHONENUMBER = "07100500300";
    private static final String UPDATE_EMPLOYEE_EMAILADDRESS = "john@email.com";
    private static final String UPDATE_EMPLOYEE_COMPANY = "Firebrand";
    private static final String UPDATE_EMPLOYEE_CARDNUMBER = "87654321";

    private static final int OK_CODE = Status.OK.getStatusCode();
    private static final int NOT_FOUND_CODE = Status.NOT_FOUND.getStatusCode();
    private static final int BAD_REQUEST_CODE = Status.BAD_REQUEST.getStatusCode();
    private static final int FORBIDDEN_CODE = Status.FORBIDDEN.getStatusCode();

    private static final String ADMIN_USERNAME = "jade";
    private static final String ADMIN_PASSWORD = "jadepwd";

    private static final String USER_USERNAME = "frank";
    private static final String USER_PASSWORD = "frankpwd";


    @BeforeAll
    public static void beforeAll() {
        loginPort = "6000";
        loginBaseUrl = "http://localhost:" + loginPort + "/membership-system/login";

        membershipSystemPort = "9080";
        baseUrl = "http://localhost:" + membershipSystemPort + "/membership-system";
    }

    /**
     * This test class is to test the security of the application so both an Admin
     * and User login are needed
     * 
     * @throws FileNotFoundException
     */
    @BeforeEach
    public void beforeEach() throws FileNotFoundException {
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

        userLoginResponse = loginRequest(USER_USERNAME, USER_PASSWORD);
        userJwt = userLoginResponse.readEntity(JsonObject.class).getString("jwt").toString();

        /* 
         * Cannot generate a JWT from my Login API for a user who is not either a User or Admin
         * JWT for a user in no group has been generated manually for testing purposes
         */
        Scanner s = new Scanner(new File("src/main/resources/noGroupJwt.txt"));
		while (s.hasNextLine()) {
			noGroupJwt = s.next();
		}
    }

    @Test
    public void testReadAll(){
        int getResponse = getRequest(adminJwt).getStatus();
        assertEquals(OK_CODE, getResponse,
            "Trying to read all employees with an Admin JWT should return the HTTP response code " + OK_CODE);

        getResponse = getRequest(userJwt).getStatus();
        assertEquals(FORBIDDEN_CODE, getResponse,
            "Trying to read all employees with a User JWT should return the HTTP response code " + FORBIDDEN_CODE);
    }

    @Test
    public void testReadIndividual(){
        int getResponse = getRequestIndividual(noGroupJwt, "-1").getStatus();
        assertEquals(FORBIDDEN_CODE, getResponse,
            "Trying to read an employee that doesn't exist without being in a security group should return the HTTP response code " 
            + FORBIDDEN_CODE + " over the HTTP response code " + NOT_FOUND_CODE);
    }

    @Test
    public void testCreate(){
        int postResponse = postRequest(userJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(OK_CODE, postResponse, 
            "Creating an employee with a User JWT should return the HTTP response code " + OK_CODE);
        postResponse = postRequest(noGroupJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(FORBIDDEN_CODE, postResponse,
            "Trying to create an employee that already exists without being in a security group should return the HTTP response code "
            + FORBIDDEN_CODE + " over the HTTP response code " + BAD_REQUEST_CODE);
    }

    @Test
    public void testUpdate(){
        int doesNotExistUpdateResponse = updateRequest(noGroupJwt, employeeForm, "-1", UPDATE_EMPLOYEE_NAME, UPDATE_EMPLOYEE_PHONENUMBER,
            UPDATE_EMPLOYEE_EMAILADDRESS, UPDATE_EMPLOYEE_COMPANY, UPDATE_EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(FORBIDDEN_CODE, doesNotExistUpdateResponse, 
            "Trying to update an employee that does not exist without being in a security group should return the HTTP response code "
            + FORBIDDEN_CODE + " over the HTTP response code " + NOT_FOUND_CODE);

        int postResponse = postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(OK_CODE, postResponse, 
            "Creating an employee with an Admin JWT should return the HTTP response code " + OK_CODE);

        Employee e = new Employee(EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        JsonObject employee = findEmployee(adminJwt, e);
        int existsUpdateResponse = updateRequest(noGroupJwt, employeeForm, employee.get("employeeId").toString(), EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER,
            EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(FORBIDDEN_CODE, existsUpdateResponse, 
            "Trying to update an employee with the same details without being in a security group should return the HTTP response code "
            + FORBIDDEN_CODE + " over the HTTP response code " + BAD_REQUEST_CODE);
    }

    @Test
    public void testDelete(){
        int deleteResponse = deleteRequest(noGroupJwt, "-1").getStatus();
        assertEquals(FORBIDDEN_CODE, deleteResponse,
            "Trying to delete an employee that does not exist without being in a security group should return the HTTP response code "
            + FORBIDDEN_CODE + " over the HTTP response code " + NOT_FOUND_CODE);
    }

    @Test
    public void testDeleteAll(){
        int deleteResponse = clearDatabase(adminJwt).getStatus();
        assertEquals(OK_CODE, deleteResponse,
            "Clearing the database with an Admin JWT should return the HTTP response code " + OK_CODE);
        
        deleteResponse = clearDatabase(userJwt).getStatus();
        assertEquals(FORBIDDEN_CODE, deleteResponse,
            "Trying to clear the database with a User JWT should return the HTTP response code " + FORBIDDEN_CODE);
    }

    @AfterEach
    public void afterEach() {
        clearDatabase(adminJwt);
        response.close();
        client.close();
    }
    
}