package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.*;
import java.io.PrintWriter;
import java.io.*; 
import java.util.*; 

@WebServlet("/topics")
public class TopicServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        UserService userService = UserServiceFactory.getUserService(); 
        User user = userService.getCurrentUser();
        String userId = user.getUserId(); 
        Query query = 
        new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query); 
        Entity entity = results.asSingleEntity(); 
        String topics = (String) entity.getProperty("topics"); 
        System.out.println(topics);
        while(topics.substring(0,1).equals(",")){
            topics = topics.substring(1);
        }
        String [] listedTopics = topics.split(",");
        System.out.println(Arrays.toString(listedTopics));
        Gson gson = new Gson(); 
        String returnTopics = gson.toJson(listedTopics);
        response.getWriter().println(returnTopics);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService(); 
        User user = userService.getCurrentUser();
        String userId = user.getUserId(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = 
        new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query); 
        Entity entity = results.asSingleEntity(); 
        if (entity == null){
            response.sendRedirect("/search.html");
        }

        String topic = request.getParameter("topic");
        String topics = (String) entity.getProperty("topics"); 

        if (topics.equals("")){
            entity.setProperty("topics", topic);
        }  else {
            topics += ",";
            topics += topic;
            entity.setProperty("topics", topics);
        }
        datastore.put(entity); 
        response.sendRedirect("/search.html");
    }

}