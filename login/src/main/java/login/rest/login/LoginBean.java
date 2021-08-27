package login.rest.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
@WebServlet(urlPatterns = "/login")
public class LoginBean extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        out.print("Hello world");
        out.close();
    }

}