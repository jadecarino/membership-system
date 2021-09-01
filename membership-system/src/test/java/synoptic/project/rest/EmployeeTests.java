package synoptic.project.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Base64;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import synoptic.project.rest.employee.Employee;

public class EmployeeTests {

    private WebTarget webTarget;

    protected Form form;
    protected Client client;
    protected Response response;
    protected HashMap<String, String> employeeForm;

    protected static String loginBaseUrl;
    protected static String loginPort;

    protected static String baseUrl;
    protected static String membershipSystemPort;

    protected Response adminLoginResponse;
    protected String adminJwt;
    protected Response userLoginResponse;
    protected String userJwt;
    protected String noGroupJwt;

    /**
     * Makes a GET request to the /login endpoint
     */
    protected Response loginRequest(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = new String(Base64.getEncoder().encode(credentials.getBytes()));
        webTarget = client.target(loginBaseUrl);
        response = webTarget.request().header("Authorization", "Basic " + encoded).get();
        return response;
    }

    /**
     *  Makes a GET request to the /employees endpoint and returns result in a JsonObject containing a JsonArray
     */
    protected Response getRequest(String jwt) {
        webTarget = client.target(baseUrl);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).get();
        return response;
    }

    /**
     *  Makes a GET request to the /employees/{employeeId} endpoint and returns a Response
     */ 
    protected Response getRequestIndividual(String jwt, String employeeId) {
        webTarget = client.target(baseUrl + "/" + employeeId);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).get();
        return response;
    }

    /**
     *  Makes a POST request to the employees?name={name}&phoneNumber={phoneNumber}
     *  &emailAddress={emailAddress}&company={company}&cardNumber={cardNumber} endpoint
     */
    protected Response postRequest(String jwt, HashMap<String, String> formDataMap, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);  
        response = webTarget.request().header("Authorization", "Bearer " + jwt).post(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a PUT request to the employees/{employeeId}?name={name}&phoneNumber={phoneNumber}
     *  &emailAddress={emailAddress}&company={company}&cardNumber={cardNumber} endpoint
     */
    protected Response updateRequest(HashMap<String, String> formDataMap, String employeeId, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + "/" + employeeId + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);
        response = webTarget.request().header("Authorization", "Bearer " + adminJwt).put(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a DELETE request to /employees/{employeeId} endpoint and return the response 
     *  code 
     */
    protected Response deleteRequest(String employeeId) {
        webTarget = client.target(baseUrl + "/" + employeeId);
        response = webTarget.request().header("Authorization", "Bearer " + adminJwt).delete();
        return response;
    }

    /**
     *  Makes a GET request to the /employees endpoint and returns the employee provided
     *  if it exists. 
     */
    protected JsonObject findEmployee(Employee e) {
        JsonObject object = getRequest(adminJwt).readEntity(JsonObject.class);
        JsonArray employees = (JsonArray) object.get("employees");
        for (int i = 0; i < employees.size(); i++) {
            JsonObject testEmployee = employees.getJsonObject(i);
            Employee test = new Employee(testEmployee.getString("name"), testEmployee.getString("phoneNumber"), 
                            testEmployee.getString("emailAddress"), testEmployee.getString("company"), testEmployee.getString("cardNumber"));
            if (test.equals(e)) {
                return testEmployee;
            }
        }
        return null;
    }

    protected void clearDatabase(){
        webTarget = client.target(baseUrl + "/clear");
        response = webTarget.request().header("Authorization", "Bearer " + adminJwt).delete();
    }

    /**
     *  Asserts employee fields equal the provided fields
     */
    protected void assertData(JsonObject employee, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        assertEquals(employee.getString("name"), name);
        assertEquals(employee.getString("phoneNumber"), phoneNumber);
        assertEquals(employee.getString("emailAddress"), emailAddress);
        assertEquals(employee.getString("company"), company);
        assertEquals(employee.getString("cardNumber"), cardNumber);
    }
    
}