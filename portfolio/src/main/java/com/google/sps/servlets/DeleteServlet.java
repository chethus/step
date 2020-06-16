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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
    
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // If we are deleting a specific comment.
        if (request.getParameter("id") != null) {
            
            // Get the ID and create its key.
            long id = Long.parseLong(request.getParameter("id"));
            Key k = KeyFactory.createKey("comment", id);

            try {
                // Test if the comment exists.
                datastore.get(k);

                datastore.delete(k);
            } catch (EntityNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {

            // Query for and delete all comments.
            Query query = new Query("comment");
            PreparedQuery results = datastore.prepare(query);
            for (Entity entity: results.asIterable()) {
                datastore.delete(entity.getKey());
            }
        }
    }
}
