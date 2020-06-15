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

import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet 
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService savedComments = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = savedComments.prepare(query);

        List<Task> tasks = new ArrayList<>();

        for (Entity entity : results.asIterable()) 
        {
            long id = entity.getKey().getId();
            long timestamp = (long) entity.getProperty("timestamp");
            String comment = (String) entity.getProperty("comment");
            
            Task task = new Task(id, timestamp, comment);
            tasks.add(task);
        }

        Gson gson = new Gson();

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(tasks));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        String comment = getParameter(request, "text-input", "");
        long timestamp = System.currentTimeMillis();

        Entity taskEntity = new Entity("Task");
        taskEntity.setProperty("comment", comment);
        taskEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);

        response.sendRedirect("/index.html");
    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) 
    {
        String value = request.getParameter(name);
        
        if (value == null) 
        {
        return defaultValue;
        }

        return value;
    }
}

class Task 
{
    private final long id, timestamp;
    private final String comment;

    public Task(long id, long timestamp, String comment) 
    {
        this.id = id;
        this.timestamp = timestamp;
        this.comment = comment;
    }
}