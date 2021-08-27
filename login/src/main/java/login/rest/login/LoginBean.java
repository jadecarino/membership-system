package login.rest.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtBuilder;

@ApplicationScoped
@WebServlet(urlPatterns = "/login")
public class LoginBean extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String auth = request.getHeader("Authorization");

        if (auth == null || auth.isEmpty()){
            sendJsonError(response, "Unauthorized");

        } else {

            auth = auth.substring(6);
            String decoded = new String(Base64.getDecoder().decode(auth));
            String[] usernameAndPassword = decoded.split(":");

            String username = usernameAndPassword[0];
            String password = usernameAndPassword[1];

            try {
                request.logout();
                request.login(username, password);
            } catch (ServletException e) {
                throw new ServletException("Invalid credentials");
            }

            Set<String> roles = getRoles(request);

            String remoteUser = request.getRemoteUser();

            if (remoteUser != null && remoteUser.equals(username)) {

                String jwt;
                try {
                    jwt = buildJwt(username, roles);
                } catch (Exception e) {
                    throw new ServletException("Error generating Json Web Token", e);
                }

                if (jwt != null && !jwt.isEmpty()){

                    JsonObject object = Json.createObjectBuilder().add("jwt", jwt).build();

                    try {	 		 
                        PrintWriter out = response.getWriter();
                        response.setStatus(200);
                        response.setContentType("Application/json");
                        out.print(object);
                        out.close();
                    } catch (Exception e) {
                        throw new ServletException("Error returning Json Web Token", e);
                    }

                } else {
                    throw new ServletException("Error generating Json Web Token");
                }

            } else {
                throw new ServletException("The user is not authorized for remote login");
            }

        }

    }

    private String buildJwt(String userName, Set<String> roles) throws Exception {
        return JwtBuilder.create("defaultJWT")
                          .claim(Claims.SUBJECT, userName)
                          .claim("upn", userName)
                          .claim("groups", roles.toArray(new String[roles.size()]))
                          .buildJwt()
                          .compact();
    }

    private Set<String> getRoles(HttpServletRequest request) {
        Set<String> roles = new HashSet<String>();
        boolean isAdmin = request.isUserInRole("admin");
        boolean isUser = request.isUserInRole("user");
        if (isAdmin) { roles.add("admin");}
        if (isUser) { roles.add("user");}
        return roles;
    }

    private void sendJsonError(HttpServletResponse response, String reason) throws IOException {
        PrintWriter out = response.getWriter();
		response.setStatus(401);
		response.setContentType("Application/json");
        JsonObject error = Json.createObjectBuilder().add("Error", reason).build();
        out.print(error);
        out.close();
    }

}