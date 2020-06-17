// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.isUserLoggedIn()) {
            String loginUrl = userService.createLoginURL("/index.html");
            response.sendRedirect(loginUrl);
        } else {
            // Get comment text.
            String text = request.getParameter("text");
            
            // Find userId and user nickname.
            String userId = userService.getCurrentUser().getUserId();
            String nickname = userService.getCurrentUser().getNickname();
            Query query = new Query("user").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
            PreparedQuery results = datastore.prepare(query);
            Entity entity = results.asSingleEntity();
            if (entity != null) {
                nickname = (String) entity.getProperty("nickname");
            }
            
            // Set properties in Datastore entity.
            Entity commentEntity = new Entity("comment");
            commentEntity.setProperty("userId", userId);
            commentEntity.setProperty("nickname", nickname);
            commentEntity.setProperty("timestamp", System.currentTimeMillis());
            commentEntity.setProperty("text", text);
            datastore.put(commentEntity);

            // Return the comment ID.
            Gson gson = new Gson();
            response.setContentType("text/plain");
            response.getWriter().println(commentEntity.getKey().getId());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get comment limit parameter.
        int max;
        try {
            max = Integer.parseInt(request.getParameter("max"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().println("Invalid comment limit.");
            return;
        }

        // Get page number.
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().println("Invalid page number.");
            return;
        }
        
        ArrayList<Comment> comments = new ArrayList<>();

        // Create a query for the Comments.
        Query query = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        // Set the max # of comments fetched by the query to limit comments on page.
        FetchOptions options = FetchOptions.Builder.withLimit(max);
        
        // To get the correct comments, offset by (# previous pages) * (#comments/page).
        options.offset((page - 1) * max);

        for (Entity entity : results.asIterable(options)) {
            comments.add(Comment.makeComment(entity));
        }

        // Send JSON back to site.
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(comments));
    }

    /*
     * Gets a the parameter's value from the request or a default value if the request 
     * does not contain the parameter.
     */
    private static String getParamOrDefault(HttpServletRequest request, String paramName, String revert) {
        final String paramValue = request.getParameter(paramName);
        if (paramValue == null || paramValue.equals("")) {
            return revert;
        }
        return paramValue;
    }
}
