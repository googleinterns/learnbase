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
      TimeZone timeZone = TimeZone.getDefault();
      int offset = -(int) ((timeZone.getOffset( System.currentTimeMillis())/(1000*60*60)));

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
      else if(Integer.parseInt(minute) <10){
        minute = "0" + minute; 
      }
      String recordedTime = hour + ":" + minute;
      System.out.println("Time recorded: " + recordedTime);
      String newTime = "";
      newTime = request.getParameter("time");
      if (newTime != null && !newTime.isEmpty()){
        String[] time = newTime.split(":");
        System.out.println("Offset: " + offset);
        int newHour = Integer.parseInt(time[0])+offset;
        if (newHour >= 24){
          newHour -=24;
        }
        if (newHour < 0){
          newHour += 24;
        }
        entity.setProperty("hour", );
        entity.setProperty("minute", Integer.parseInt(time[1]));
        entity.setProperty("offset", offset); 
        datastore.put(entity); 

      } else {
        String[] time = recordedTime.split(":");
        int newHour = (Integer.parseInt(time[0]) - offset); 
          if (newHour >= 24){
          newHour -=24;
        }
        if (newHour < 0){
          newHour += 24;
        }
        newTime = newHour + ":"+time[1];
      }
    
      response.getWriter().println(newTime);
      System.out.println("Printed " + newTime);
  }


}