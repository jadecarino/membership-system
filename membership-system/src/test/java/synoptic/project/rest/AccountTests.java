package synoptic.project.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Form;

public class AccountTests extends EmployeeTests {

    private static final String ACCOUNTS = "/accounts";

    /**
     *  Makes a GET request to the /accounts/{cardNumber}/balance endpoint
     */
    protected Response getRequest(String jwt, String cardNumber) {
        webTarget = client.target(baseUrl + ACCOUNTS + "/" + cardNumber + "/balance");
        response = webTarget.request().header("Authorization", "Bearer " + jwt).get();
        return response;
    }

    /**
     *  Makes a PUT request to the /accounts/{cardNumber}/topup?amount={amount} endpoint
     */
    protected Response topUpRequest(String jwt, String cardNumber, int amount) {
        webTarget = client.target(baseUrl + ACCOUNTS + "/" + cardNumber + "/topup?amount=" + amount);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).method(HttpMethod.PUT);
        form = new Form();
        return response;
    }


    /**
     *  Makes a PUT request to the /accounts/{cardNumber}/pay?amount={amount} endpoint
     */
    protected Response payRequest(String jwt, String cardNumber, int amount) {
        webTarget = client.target(baseUrl + ACCOUNTS + "/" + cardNumber + "/pay?amount=" + amount);
        response = webTarget.request().header("Authorization", "Bearer " + jwt).method(HttpMethod.PUT);
        form = new Form();
        return response;
    }
    
}