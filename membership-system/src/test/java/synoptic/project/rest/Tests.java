package synoptic.project.rest;

import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public class Tests {

    protected WebTarget webTarget;

    protected Form form;
    protected Client client;
    protected Response response;

    protected static String loginBaseUrl;
    protected static String loginPort;

    protected static String baseUrl;
    protected static String membershipSystemPort;

    protected Response adminLoginResponse;
    protected String adminJwt;
    protected Response userLoginResponse;
    protected String userJwt;
    protected String invalidJwt;

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
    
}