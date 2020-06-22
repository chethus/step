package com.google.sps.servlets;

import com.google.sps.data.User;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;

import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private UserService userService = UserServiceFactory.getUserService();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        Gson gson = new Gson();

        if (!userService.isUserLoggedIn()) {
            // If the user is not logged in, return a login link for a button.
            String loginUrl = userService.createLoginURL("/user");
            out.println(gson.toJson(loginUrl));
        } else {
            // Otherwise, get user login information.
            String id = userService.getCurrentUser().getUserId();
            String email = userService.getCurrentUser().getEmail();

            // Query the user's nickname.
            Query query = new Query("user").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
            PreparedQuery results = datastore.prepare(query);
            Entity entity = results.asSingleEntity();

            // Return the user information with JSON.
            String nickname = null;
            if (entity != null) {
                nickname = (String) entity.getProperty("nickname");
            }
            User user = new User(email, nickname);
            out.println(gson.toJson(user));
        }
    }

    @Override 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.isUserLoggedIn()) {
            // If the user isn't logged in, redirect to the login page.
            String loginUrl = userService.createLoginURL("/user");
            response.sendRedirect(loginUrl);
        } else {
            // Otherwise, we are changing the nickname.
            // Get the user's id and nickname.
            String id = userService.getCurrentUser().getUserId();
            String nickname = request.getParameter("nickname");

            // Query for the user's original entry.
            Query query = new Query("user").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
            PreparedQuery results = datastore.prepare(query);
            Entity entity = results.asSingleEntity();

            // Modify the original entry with the new nickname and update the datastore.
            entity.setProperty("nickname", nickname);
            datastore.put(entity);
        }
    }
}