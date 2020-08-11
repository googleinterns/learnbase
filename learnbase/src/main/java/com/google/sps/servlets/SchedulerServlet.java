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

@WebServlet("/scheduler")
public class SchedulerServlet extends HttpServlet{
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
      String hour = entity.getProperty("hour").toString();
      String minute = entity.getProperty("minute").toString();
      if (minute.equals("0")){
        minute += "0";
      }
      String recordedTime = hour + ":" + minute;
      System.out.println("Time recorded: " + recordedTime);
      String newTime = "";
      newTime = request.getParameter("time");
      System.out.println("New time: " + newTime);
      if (newTime != null && !newTime.isEmpty()){
        String[] time = newTime.split(":");
        entity.setProperty("hour", Integer.parseInt(time[0]));
        entity.setProperty("minute", Integer.parseInt(time[1]));

        datastore.put(entity); 

      } else {

        newTime = recordedTime; 
      }
    
      response.getWriter().println(newTime);
      System.out.println("Printed " + newTime);
  }


}