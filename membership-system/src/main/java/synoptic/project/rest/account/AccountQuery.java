package synoptic.project.rest.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.security.jwt.InvalidConsumerException;
import com.ibm.websphere.security.jwt.InvalidTokenException;
import com.ibm.websphere.security.jwt.JwtConsumer;
import com.ibm.websphere.security.jwt.JwtToken;


@RequestScoped
@Path("/accounts")
public class AccountQuery {

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
     * Get the account balance
     * 
     * @throws ServletException
     */
    @GET
    @Path("/{cardNumber}/balance")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getBalance(@HeaderParam("Authorization") String auth, @PathParam("cardNumber") String cardNumber)
            throws ServletException {

        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")){

            JsonObjectBuilder builder = Json.createObjectBuilder();

            Account account = accountDAO.readAccount(cardNumber);
            if (account != null){
                builder.add("balance", account.getBalance());
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Account not found").build();
            }
    
            return Response.status(Response.Status.OK).entity(builder.build()).build();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Must be a User to access this service").build();
        }

    }

    /**
     * Top up the account balance
     * 
     * @throws ServletException
     */
    @PUT
    @Path("/{cardNumber}/topup")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response topUp(@HeaderParam("Authorization") String auth, @PathParam("cardNumber") String cardNumber,
            @QueryParam("amount") int amount) throws ServletException {

        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        if (amount < 0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")){

            Account account = accountDAO.readAccount(cardNumber);
            if (account == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Account does not exist").build();
            }

            int newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
            accountDAO.updateAccount(account);

            return Response.status(Response.Status.OK).entity("Balance topped up").build();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Must be a User to access this service").build();
        }

    }

    /**
     * Deduct from the account balance
     * 
     * @throws ServletException
     */
    @PUT
    @Path("/{cardNumber}/pay")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response pay(@HeaderParam("Authorization") String auth, @PathParam("cardNumber") String cardNumber,
            @QueryParam("amount") int amount) throws ServletException {

        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }

        if (amount < 0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> groups = getRoles(auth);

        if (groups.contains("admin") || groups.contains("user")){

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

            return Response.status(Response.Status.OK).entity("Thanks for your purchase").build();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Must be a User to access this service").build();
        }

    }
    
}