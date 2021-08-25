package synoptic.project.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    protected static String baseUrl;
    protected static String port;
    protected static final String EMPLOYEES = "employees";

    /**
     *  Makes a POST request to the employees?name={name}&phoneNumber={phoneNumber}
     *  &emailAddress={emailAddress}&company={company}&cardNumber={cardNumber} endpoint
     */
    protected int postRequest(HashMap<String, String> formDataMap, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + EMPLOYEES + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);     
        response = webTarget.request().post(Entity.form(form));
        form = new Form();
        return response.getStatus();
    }

    /**
     *  Makes a PUT request to the employees/{employeeId}?name={name}&phoneNumber={phoneNumber}
     *  &emailAddress={emailAddress}&company={company}&cardNumber={cardNumber} endpoint
     */
    protected int updateRequest(HashMap<String, String> formDataMap, String employeeId, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + EMPLOYEES + "/" + employeeId + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);
        response = webTarget.request().put(Entity.form(form));
        form = new Form();
        return response.getStatus();
    }

    /**
     *  Makes a DELETE request to /employees/{employeeId} endpoint and return the response 
     *  code 
     */
    protected int deleteRequest(String employeeId) {
        webTarget = client.target(baseUrl + EMPLOYEES + "/" + employeeId);
        response = webTarget.request().delete();
        return response.getStatus();
    }

    /**
     *  Makes a GET request to the /employees endpoint and returns result in a JsonObject containing a JsonArray
     */
    protected JsonObject getRequest() {
        webTarget = client.target(baseUrl + EMPLOYEES);
        response = webTarget.request().get();
        return response.readEntity(JsonObject.class);
    }

    /**
     *  Makes a GET request to the /employees/{employeeId} endpoint and returns a JsonObject
     */ 
    protected Response getIndividualEmployee(String employeeId) {
        webTarget = client.target(baseUrl + EMPLOYEES + "/employeeId=" + employeeId);
        response = webTarget.request().get();
        return response;
    }

    /**
     *  Makes a GET request to the /employees endpoint and returns the employee provided
     *  if it exists. 
     */
    protected JsonObject findEmployee(Employee e) {
        JsonObject object = getRequest();
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
        webTarget = client.target(baseUrl + EMPLOYEES + "/clear");
        response = webTarget.request().delete();
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