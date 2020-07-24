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

@WebServlet("/deleteTopic")
public class DeleteTopicServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

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
        String topics = (String) entity.getProperty("topics"); 
        String [] listedTopics = topics.split(",");

        String removedTopic = request.getParameter("topic");
        String editedTopics = "";
        for (int i = 0; i < listedTopics.length; i++){
          if (!listedTopics[i].equals(removedTopic)){
            editedTopics += listedTopics[i]; 
            if (i+1 < listedTopics.length){
              editedTopics+=",";
            }
          }
        }
        if (editedTopics.substring(editedTopics.length()-1).equals(",")){
          editedTopics = editedTopics.substring(0, editedTopics.length()-1);
        }
        entity.setProperty("topics", editedTopics);
        datastore.put(entity); 
        response.sendRedirect("/search.html");
    }

}