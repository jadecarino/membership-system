package synoptic.project.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import synoptic.project.rest.employee.Employee;

public class EmployeeTests extends Tests {

    protected HashMap<String, String> employeeForm;

    private static final String EMPLOYEES = "/employees";

    /**
     *  Makes a GET request to the /employees endpoint
     */
    protected Response getRequest(String jwt) {
        webTarget = client.target(baseUrl + EMPLOYEES);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).get();
        return response;
    }

    /**
     *  Makes a GET request to the /employees/{employeeId} endpoint
     */ 
    protected Response getRequestIndividual(String jwt, String employeeId) {
        webTarget = client.target(baseUrl + EMPLOYEES + "/" + employeeId);
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
        webTarget = client.target(baseUrl + EMPLOYEES + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);  
        response = webTarget.request().header("Authorization", "Bearer " + jwt).post(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a PUT request to the employees/{employeeId}?name={name}&phoneNumber={phoneNumber}
     *  &emailAddress={emailAddress}&company={company}&cardNumber={cardNumber} endpoint
     */
    protected Response updateRequest(String jwt, HashMap<String, String> formDataMap, String employeeId, String name, String phoneNumber,
                                String emailAddress, String company, String cardNumber) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + EMPLOYEES + "/" + employeeId + "?name=" + name + "&phoneNumber=" + phoneNumber + "&emailAddress=" + 
                                    emailAddress + "&company=" + company + "&cardNumber=" + cardNumber);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).put(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a DELETE request to /employees/{employeeId} endpoint
     */
    protected Response deleteRequest(String jwt, String employeeId) {
        webTarget = client.target(baseUrl + EMPLOYEES + "/" + employeeId);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).delete();
        return response;
    }

    /**
     *  Makes a GET request to the /employees endpoint and returns the employee provided
     *  if it exists
     */
    protected JsonObject findEmployee(String jwt, Employee e) {
        JsonObject object = getRequest(jwt).readEntity(JsonObject.class);
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

    /**
     *  Makes a DELETE request to the /employees/clear endpoint to clear the database
     */
    protected Response clearDatabase(String jwt){
        webTarget = client.target(baseUrl + EMPLOYEES + "/clear");
        response = webTarget.request().header("Authorization", "Bearer " + jwt).delete();
        return response;
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