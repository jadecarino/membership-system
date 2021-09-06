package synoptic.project.rest.load;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import synoptic.project.rest.account.Account;
import synoptic.project.rest.account.AccountDataAccessObject;
import synoptic.project.rest.employee.Employee;
import synoptic.project.rest.employee.EmployeeDataAccessObject;


@RequestScoped
@Path("/sampledata")
/** 
 * This class is for loading sample data into the database 
 * Load the sample data by sending a request to the /sampledata/load
 * 
 * You can also clear the sample data by sending a request to the /sampledata/clear endpoint
 */
public class SampleData {

    @Inject
    private EmployeeDataAccessObject employeeDAO;

    @Inject
    private AccountDataAccessObject accountDAO;

    @POST
    @Path("/load")
    @Transactional
    public Response loadSampleData(){
        doPost("Jade", "07500100200", "jade@ibm.com", "IBM", "40029441");
        doPost("Frank", "07300200400", "frank@firebrand.com", "Firebrand", "30042570");
        doPost("Lydia", "07800500600", "lydia@bcs.com", "BCS", "47502001");
        return Response.status(Response.Status.OK).entity("Sample data load into Database").build();

    }

    @DELETE
    @Path("/clear")
    @Transactional
    public Response deleteSampleData(){
        doDelete();
        return Response.status(Response.Status.OK).entity("Sample data cleared").build();
    }

    private void doPost(String name, String phoneNumber, String emailAddress, String company, String cardNumber){
        Employee employee = new Employee(name, phoneNumber, emailAddress, company, cardNumber);
        employeeDAO.createEmployee(employee);
        Account newAccount = new Account(cardNumber, 0);
        accountDAO.createAccount(newAccount);
    }

    private void doDelete(){
        for (Employee employee : employeeDAO.readAllEmployees()){
            Account account = accountDAO.readAccount(employee.getCardNumber());
            if (account != null){
                accountDAO.deleteAccount(account);
            }
            employeeDAO.deleteEmployee(employee);
        }
    }
}