package synoptic.project.rest.account;

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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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

    @GET
    @Path("/balance/{cardNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response doGet(@PathParam("cardNumber") String cardNumber) throws ServletException {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        Account account = accountDAO.readAccount(cardNumber);
        if (account != null){
            builder.add("balance", account.getBalance());
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Account not found").build();
        }

        return Response.status(Response.Status.OK).entity(builder.build()).build();

    }
    
}