 
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.*; 
import java.util.*; 

@WebServlet("/interests")
public class InterestsServlet extends HttpServlet{
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService =  UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      User user = userService.getCurrentUser();
      String userId = user.getUserId();
      Query query = 
      new Query("UserInfo")
      .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
      PreparedQuery results = datastore.prepare(query);
      Entity entity = results.asSingleEntity();
      String topics = (String) entity.getProperty("topics");
      ArrayList<String> topicsOutput = new ArrayList<>();
      String[] topicsArray = topics.split(",");
      for (String topic : topicsArray) {
        String topicOutput = "<p>" + topic + "</p>";
	topicsOutput.add(topicOutput);
      }

      response.setContentType("application/json;");
      Gson gson = new Gson();
      response.getWriter().println(gson.toJson(topicsOutput));
    }
  }

}
