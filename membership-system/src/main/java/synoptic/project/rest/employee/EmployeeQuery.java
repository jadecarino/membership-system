package synoptic.project.rest.employee;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/employees")
public class EmployeeQuery {

    @Inject
    private EmployeeDataAccessObject employeeDAO;

    /**
     * Get all employees
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doGet(){

        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder finalArray = Json.createArrayBuilder();

        for (Employee employee : employeeDAO.readAllEmployees()) {
            builder.add("employeeId", employee.getEmployeeId())
                    .add("name", employee.getName())
                    .add("phoneNumber", employee.getPhoneNumber())
                    .add("emailAddress", employee.getEmailAddress())
                    .add("company", employee.getCompany())
                    .add("cardNumber", employee.getCardNumber());
            finalArray.add(builder.build());
        }

        JsonObject object = Json.createObjectBuilder().add("employees", finalArray).build();

        return Response.status(Response.Status.OK).entity(object).build();
    }

    /**
     * Get the employee with specified employeeId
     */
    @GET
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doGet(@PathParam("employeeId") int employeeId){

        JsonObjectBuilder builder = Json.createObjectBuilder();

        Employee employee = employeeDAO.readEmployee(employeeId);

        if (employee != null) {
            builder.add("employeeId", employee.getEmployeeId())
                    .add("name", employee.getName())
                    .add("phoneNumber", employee.getPhoneNumber())
                    .add("emailAddress", employee.getEmailAddress())
                    .add("company", employee.getCompany())
                    .add("cardNumber", employee.getCardNumber());
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Employee does not exist").build();
        }

        return Response.status(Response.Status.OK).entity(builder.build()).build(); 
    }

    /**
     * Create an employee with the specified fields
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doPost(@QueryParam("name") String name, @QueryParam("phoneNumber") String phoneNumber,
    @QueryParam("emailAddress") String emailAddress, @QueryParam("company") String company, @QueryParam("cardNumber") String cardNumber){

        Employee newEmployee = new Employee(name, phoneNumber, emailAddress, company, cardNumber);

        if (!employeeDAO.findEmployee(name, phoneNumber, emailAddress, company, cardNumber).isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Employee already exists").build();
        }

        employeeDAO.createEmployee(newEmployee);

        return Response.status(Response.Status.OK).entity("Employee created").build(); 
    }

    /**
     * Update an employee with the specified employeeId with the specified fields
     */
    @PUT
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doPut(@PathParam("employeeId") int employeeId, @QueryParam("name") String newName, @QueryParam("phoneNumber") String newPhoneNumber,
    @QueryParam("emailAddress") String newEmailAddress, @QueryParam("company") String newCompany, @QueryParam("cardNumber") String newCardNumber){

        Employee prevEmployee = employeeDAO.readEmployee(employeeId);

        if (prevEmployee == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Employee does not exist").build();
        }

        if (!employeeDAO.findEmployee(newName, newPhoneNumber, newEmailAddress, newCompany, newCardNumber).isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Employee already exists").build();
        }

        prevEmployee.setName(newName);
        prevEmployee.setPhoneNumber(newPhoneNumber);
        prevEmployee.setEmailAddress(newEmailAddress);
        prevEmployee.setCompany(newCompany);
        prevEmployee.setCardNumber(newCardNumber);

        employeeDAO.updateEmployee(prevEmployee);

        return Response.status(Response.Status.OK).entity("Employee updated").build();
    }

    /**
     * Delete the employee with the specified employeeId
     */
    @DELETE
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doDelete(@PathParam("employeeId") int employeeId){

        Employee employee = employeeDAO.readEmployee(employeeId);

        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Employee does not exist").build();
        }

        employeeDAO.deleteEmployee(employee);

        return Response.status(Response.Status.OK).entity("Employee deleted").build();
    }

    @DELETE
    @Path("/clear")
    @Transactional
    public Response clearDatabase(){

        for (Employee employee : employeeDAO.readAllEmployees()) {
            employeeDAO.deleteEmployee(employee);
        }

        return Response.status(Response.Status.OK).entity("Database cleared").build();
    }
    
}