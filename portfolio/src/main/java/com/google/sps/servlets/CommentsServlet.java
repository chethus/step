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
import com.google.sps.data.OwnComment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import com.google.gson.Gson;
import java.net.URL;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.MalformedURLException;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private ImagesService imagesService = ImagesServiceFactory.getImagesService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
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
            if (request.getParameter("commentId") != null) {
                long commentId = Long.parseLong(request.getParameter("commentId"));
                Key k = KeyFactory.createKey("comment", commentId);
                try {
                    commentEntity = datastore.get(k);
                } catch (EntityNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
            commentEntity.setProperty("userId", userId);
            commentEntity.setProperty("nickname", nickname);
            commentEntity.setProperty("timestamp", System.currentTimeMillis());
            commentEntity.setProperty("text", text);
            commentEntity.setProperty("imageSrc", getFileUrl(request, "image"));
            datastore.put(commentEntity);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // If a user is logged in, get their id.
        String userId = null;
        if (userService.isUserLoggedIn()) {
            userId = userService.getCurrentUser().getUserId();
        }

        // Get comment limit parameter.
        int max = Integer.parseInt(request.getParameter("max"));

        // Get page number.
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        ArrayList<Comment> comments = new ArrayList<>();

        // Create a query for the Comments.
        Query query = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        // Skip earlier pages and enforce Comment limit.
        FetchOptions options = FetchOptions.Builder.withLimit(max);
        options.offset((page - 1) * max);

        for (Entity entity : results.asIterable(options)) {
            Comment c = Comment.makeComment(entity);
            if (userId != null && userId.equals((String)entity.getProperty("userId"))) {
                c = new OwnComment(c, entity.getKey().getId());
            }
            comments.add(c);
        }

        // Send JSON back to site.
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(comments));
    }

    /** Returns file URL or null if no file was uploaded. */
    public String getFileUrl(HttpServletRequest request, String formElementName) {
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get(formElementName);

        // If no file was uploaded, there is no URL to return.
        if (blobKeys == null || blobKeys.isEmpty()) {
            return null;
        }

        // We are assuming there is only one image so we get the first key.
        BlobKey blobKey = blobKeys.get(0);

        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

        // If no uploaded file on live server, return null URL.
        if (blobInfo.getSize() == 0) {
            blobstoreService.delete(blobKey);
            return null;
        }

        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

        // Return URL with relative path of ImagesService URL if possible.
        try {
            URL url = new URL(imagesService.getServingUrl(options));
            return url.getPath();
        } catch (MalformedURLException e) {
            return imagesService.getServingUrl(options);
        }
    }
}
