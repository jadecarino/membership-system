package synoptic.project.rest.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.security.jwt.InvalidConsumerException;
import com.ibm.websphere.security.jwt.InvalidTokenException;
import com.ibm.websphere.security.jwt.JwtConsumer;
import com.ibm.websphere.security.jwt.JwtToken;

import synoptic.project.rest.account.Account;
import synoptic.project.rest.account.AccountDataAccessObject;

@RequestScoped
@Path("/employees")
public class EmployeeQuery {

    @Inject
    private EmployeeDataAccessObject employeeDAO;

    @Inject
    private AccountDataAccessObject accountDAO;

    private List<String> getRoles(String auth) throws ServletException {

        auth = auth.substring(7);

        JwtToken jwtToken = null;
        try {
            jwtToken = JwtConsumer.create("defaultJWT").createJwt(auth);
        } catch (InvalidTokenException e) {
            throw new ServletException("Invalid JWT", e);
        } catch (InvalidConsumerException e) {
            throw new ServletException("Invalid JWT Consumer", e);
        }

        List<String> groups = new ArrayList<String>();

        if (jwtToken != null){ 
            String groupClaim = jwtToken.getClaims().get("groups").toString();
            groupClaim = groupClaim.substring(1, groupClaim.length() - 1);
            groups = Arrays.asList(groupClaim.split(","));
        }

        return groups;
    }

    /**
     * Get all employees
     * 
     * @throws ServletException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllEmployees(@HeaderParam("Authorization") String auth) throws ServletException {

        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin")) {

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

        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Must be an Admin to access this service").build();
        }
    }

    /**
     * Get the employee with specified employeeId
     * 
     * @throws ServletException
     */
    @GET
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getEmployee(@HeaderParam("Authorization") String auth, @PathParam("employeeId") int employeeId) throws ServletException {

        if (auth == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")){

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

        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Must be a User to access this service").build();
        }

    }

    /**
     * Create an employee with the specified fields
     * 
     * @throws ServletException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createEmployee(@HeaderParam("Authorization") String auth, @QueryParam("name") String name,
            @QueryParam("phoneNumber") String phoneNumber, @QueryParam("emailAddress") String emailAddress,
            @QueryParam("company") String company, @QueryParam("cardNumber") String cardNumber)
            throws ServletException {

        if (auth == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")) {

            Employee newEmployee = new Employee(name, phoneNumber, emailAddress, company, cardNumber);
            if (!employeeDAO.findEmployee(name, phoneNumber, emailAddress, company, cardNumber).isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Employee already exists").build();
            }
            employeeDAO.createEmployee(newEmployee);

            Account newAccount = new Account(cardNumber, 0);
            accountDAO.createAccount(newAccount);

            return Response.status(Response.Status.OK).entity("Employee created and Account opened").build(); 

        } else {
           return Response.status(Response.Status.FORBIDDEN).entity("Must be a User to access this service").build();
        }

        
    }

    /**
     * Update an employee with the specified employeeId with the specified fields
     * 
     * @throws ServletException
     */
    @PUT
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateEmployee(@HeaderParam("Authorization") String auth, @PathParam("employeeId") int employeeId,
            @QueryParam("name") String newName, @QueryParam("phoneNumber") String newPhoneNumber,
            @QueryParam("emailAddress") String newEmailAddress, @QueryParam("company") String newCompany,
            @QueryParam("cardNumber") String newCardNumber) throws ServletException {

        if (auth == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")) {

            Employee prevEmployee = employeeDAO.readEmployee(employeeId);
            if (prevEmployee == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Employee does not exist").build();
            }

            if (!employeeDAO.findEmployee(newName, newPhoneNumber, newEmailAddress, newCompany, newCardNumber).isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Employee already exists").build();
            }

            String confirmationMessage = "Employee updated";
            if (prevEmployee.getCardNumber() != newCardNumber){
                Account oldAccount = accountDAO.readAccount(prevEmployee.getCardNumber());
                int balance = oldAccount.getBalance();
                Account newAccount = new Account(newCardNumber, balance);
                accountDAO.createAccount(newAccount);
                accountDAO.deleteAccount(oldAccount);
                confirmationMessage = confirmationMessage + " and new Account opened with new Card Number";
            }

            prevEmployee.setName(newName);
            prevEmployee.setPhoneNumber(newPhoneNumber);
            prevEmployee.setEmailAddress(newEmailAddress);
            prevEmployee.setCompany(newCompany);
            prevEmployee.setCardNumber(newCardNumber);
            employeeDAO.updateEmployee(prevEmployee);

            return Response.status(Response.Status.OK).entity(confirmationMessage).build();

        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Must be a User to access this service").build();
        }

    }

    /**
     * Delete the employee with the specified employeeId
     * 
     * @throws ServletException
     */
    @DELETE
    @Path("/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteEmployee(@HeaderParam("Authorization") String auth, @PathParam("employeeId") int employeeId) throws ServletException {

        if (auth == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")) {
            
            Employee employee = employeeDAO.readEmployee(employeeId);

            if (employee == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Employee does not exist").build();
            }
    
            Account account = accountDAO.readAccount(employee.getCardNumber());
            accountDAO.deleteAccount(account);
            employeeDAO.deleteEmployee(employee);
    
            return Response.status(Response.Status.OK).entity("Employee deleted and Account closed").build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Must be a User to access this service").build();
        }
    }

    @DELETE
    @Path("/clear")
    @Transactional
    public Response clearDatabase(@HeaderParam("Authorization") String auth) throws ServletException {

        if (auth == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin")){
            for (Employee employee : employeeDAO.readAllEmployees()){
                Account account = accountDAO.readAccount(employee.getCardNumber());
                if (account != null){
                    accountDAO.deleteAccount(account);
                }
                employeeDAO.deleteEmployee(employee);
            }
            return Response.status(Response.Status.OK).entity("Database cleared").build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Must be an Admin to access this service").build();
        }

    }
    
}