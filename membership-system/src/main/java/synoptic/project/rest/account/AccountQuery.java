package synoptic.project.rest.account;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@RequestScoped
@Path("/accounts")
public class AccountQuery {

    @Inject
    private AccountDataAccessObject accountDAO;

    /**
     * Get the account balance
     */
    @GET
    @Path("/balance/{cardNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getBalance(@PathParam("cardNumber") String cardNumber) throws ServletException {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        Account account = accountDAO.readAccount(cardNumber);
        if (account != null){
            builder.add("balance", account.getBalance());
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Account not found").build();
        }

        return Response.status(Response.Status.OK).entity(builder.build()).build();

    }

    /**
     * Top up the account balance
     */
    @PUT
    @Path("/topup/{cardNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response topUp(@PathParam("cardNumber") String cardNumber, @QueryParam("amount") int amount) throws ServletException {

        Account account = accountDAO.readAccount(cardNumber);
        if (account == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Account does not exist").build();
        }

        int newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        accountDAO.updateAccount(account);

        return Response.status(Response.Status.OK).entity("Balance topped up.").build();

    }

    /**
     * Deduct from the account balance
     */
    @PUT
    @Path("/pay/{cardNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response pay(@PathParam("cardNumber") String cardNumber, @QueryParam("amount") int amount) throws ServletException {

        Account account = accountDAO.readAccount(cardNumber);
        if (account == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Account does not exist").build();
        }

        if (account.getBalance() < amount){
            return Response.status(Response.Status.BAD_REQUEST).entity("Insufficient funds").build();
        }

        int newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        accountDAO.updateAccount(account);

        return Response.status(Response.Status.OK).entity("Thanks for your purchase.").build();

    }
    
}