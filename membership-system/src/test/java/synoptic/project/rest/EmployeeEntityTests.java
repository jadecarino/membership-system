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

public class EmployeeEntityTests extends EmployeeTests {

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

    private static final String ADMIN_USERNAME = "jade";
    private static final String ADMIN_PASSWORD = "jadepwd";

    @BeforeAll
    public static void beforeAll() {
        loginPort = "6000";
        loginBaseUrl = "http://localhost:" + loginPort + "/membership-system/login";

        membershipSystemPort = "9080";
        baseUrl = "http://localhost:" + membershipSystemPort + "/membership-system/employees";
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

        /* Logging in as an Admin so all services under test can be accessed */
        adminLoginResponse = loginRequest(ADMIN_USERNAME, ADMIN_PASSWORD);
        adminJwt = adminLoginResponse.readEntity(JsonObject.class).getString("jwt").toString();

    }

    @Test
    public void testReadAll(){
        int getResponse = getRequest(adminJwt).getStatus();
        assertEquals(OK_CODE, getResponse,
            "Trying to read all employees with an authenticated request should return the HTTP response code " + OK_CODE);
    }
    
    @Test
    public void testInvalidRead() {
        int getResponse = getRequestIndividual(adminJwt, "-1").getStatus();
        assertEquals(NOT_FOUND_CODE, getResponse,
          "Trying to read an employee that does not exist should return the HTTP response code " + NOT_FOUND_CODE);
    }

    @Test
    public void testInvalidCreate(){
        postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        int postResponse = postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(BAD_REQUEST_CODE, postResponse,
            "Trying to create an employee that already exists should return the HTTP response code " + BAD_REQUEST_CODE);
    }

    @Test
    public void testInvalidUpdate() {
        int doesNotExistUpdateResponse = updateRequest(adminJwt, employeeForm, "-1", UPDATE_EMPLOYEE_NAME, UPDATE_EMPLOYEE_PHONENUMBER,
            UPDATE_EMPLOYEE_EMAILADDRESS, UPDATE_EMPLOYEE_COMPANY, UPDATE_EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(NOT_FOUND_CODE, doesNotExistUpdateResponse, 
            "Trying to update an employee that does not exist should return the HTTP response code " + NOT_FOUND_CODE);

        int postResponse = postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(OK_CODE, postResponse, 
            "Creating an employee should return the HTTP response code " + OK_CODE);

        Employee e = new Employee(EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        JsonObject employee = findEmployee(adminJwt, e);
        int existsUpdateResponse = updateRequest(adminJwt, employeeForm, employee.get("employeeId").toString(), EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER,
            EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(BAD_REQUEST_CODE, existsUpdateResponse, 
            "Trying to update an employee with the same details should return the HTTP response code " + BAD_REQUEST_CODE);
    }

    @Test
    public void testInvalidDelete() {
        int deleteResponse = deleteRequest(adminJwt, "-1").getStatus();
        assertEquals(NOT_FOUND_CODE, deleteResponse,
            "Trying to delete an employee that does not exist should return the HTTP response code " + NOT_FOUND_CODE);
    }

    @Test
    public void testCRUD() {
        int employeeCount = getRequest(adminJwt).readEntity(JsonObject.class).size();

        int postResponse = postRequest(adminJwt, employeeForm, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(OK_CODE, postResponse, 
          "Creating an employee should return the HTTP response code " + OK_CODE);
     
        Employee e = new Employee(EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);
        JsonObject employee = findEmployee(adminJwt, e);
        assertData(employee, EMPLOYEE_NAME, EMPLOYEE_PHONENUMBER, EMPLOYEE_EMAILADDRESS, EMPLOYEE_COMPANY, EMPLOYEE_CARDNUMBER);

        employeeForm.put(JSONFIELD_NAME, UPDATE_EMPLOYEE_NAME);
        employeeForm.put(JSONFIELD_PHONENUMBER, UPDATE_EMPLOYEE_PHONENUMBER);
        employeeForm.put(JSONFIELD_EMAILADDRESS, UPDATE_EMPLOYEE_EMAILADDRESS);
        employeeForm.put(JSONFIELD_COMPANY, UPDATE_EMPLOYEE_COMPANY);
        employeeForm.put(JSONFIELD_CARDNUMBER, UPDATE_EMPLOYEE_CARDNUMBER);
        int updateResponse = updateRequest(adminJwt, employeeForm, employee.get("employeeId").toString(), UPDATE_EMPLOYEE_NAME, UPDATE_EMPLOYEE_PHONENUMBER,
            UPDATE_EMPLOYEE_EMAILADDRESS, UPDATE_EMPLOYEE_COMPANY, UPDATE_EMPLOYEE_CARDNUMBER).getStatus();
        assertEquals(OK_CODE, updateResponse, 
          "Updating an employee should return the HTTP response code " + OK_CODE);
        
        e = new Employee(UPDATE_EMPLOYEE_NAME, UPDATE_EMPLOYEE_PHONENUMBER, UPDATE_EMPLOYEE_EMAILADDRESS,
            UPDATE_EMPLOYEE_COMPANY, UPDATE_EMPLOYEE_CARDNUMBER);
        employee = findEmployee(adminJwt, e);
        assertData(employee, UPDATE_EMPLOYEE_NAME, UPDATE_EMPLOYEE_PHONENUMBER, UPDATE_EMPLOYEE_EMAILADDRESS,
             UPDATE_EMPLOYEE_COMPANY, UPDATE_EMPLOYEE_CARDNUMBER);

        int deleteResponse = deleteRequest(adminJwt, employee.get("employeeId").toString()).getStatus();
        assertEquals(OK_CODE, deleteResponse, 
          "Deleting an employee should return the HTTP response code " + OK_CODE);

        assertEquals(employeeCount, getRequest(adminJwt).readEntity(JsonObject.class).size(), 
          "Total number of employees stored should be the same after testing CRUD operations.");
    }

    @AfterEach
    public void afterEach() {
        clearDatabase(adminJwt);
        response.close();
        client.close();
    }

}