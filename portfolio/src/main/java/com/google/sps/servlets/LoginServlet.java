import com.google.sps.data.Login;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService = UserServiceFactory.getUserService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Get login status and appropriate url.
        boolean loggedIn = userService.isUserLoggedIn();
        String url = null;
        if (loggedIn) {
            url = userService.createLogoutURL("/index.html");
        } else {
            url = userService.createLoginURL("/index.html");
        }
        
        // Create and respond with Login object.
        Login login = new Login(loggedIn, url);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.println(gson.toJson(login));
    }
}