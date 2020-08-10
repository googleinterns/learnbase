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

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
  
  //Gets the current days information for each topic 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
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
    
    //If the user has not searched any topics yet, print that they have no topics
    if (topics.trim().equals("")) {
      Gson gson = new Gson();
      ArrayList<String> topicsInfo = new ArrayList<>();
      topicsInfo.add("<h2>No topics yet! Search something you want to know more about to get info</h2>");
      response.getWriter().println(gson.toJson(topicsInfo));
      return;
    }

    ArrayList<String> topicsInfo = new ArrayList<>();
    String[] topicsArray = topics.split(",");

    //Goes through each topic and gets that days info, 
    //then adds it to the arraylist that stores the info to print
    for (String topic : topicsArray) {
      String topicName = topic+"topic";
      String iteratorName = topic+"iterator";
      String iterator = (String) entity.getProperty(iteratorName);
      ArrayList<String> urls = (ArrayList<String>) entity.getProperty(topicName);
      int iteratorNum = Integer.parseInt(iterator);
      
      //If there are no more urls for this topic, 
      //Print that there are none and then go to next topic 
      if (iteratorNum >= urls.size()) {
	topicsInfo.add(0,"No more info for this topic!");
	topicsInfo.add(0, "<h1>"+topic+":</h1>");
	continue;
      }

      String info = getInfo(urls, iterator);
      topicsInfo.add(0, info);
      topicsInfo.add(0, "<h1>"+topic+":</h1>");

      //Increment iterator so that the next day they get new info 
      iterator = Integer.toString(Integer.parseInt(iterator)+1);
      entity.setProperty(iteratorName, iterator);
    }
    datastore.put(entity);
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(topicsInfo));

  }
  
  //Gets the info off a page for a given url 
  private String getInfo(ArrayList<String> urls, String currentUrl) throws IOException {
    String info = "";
    int currentUrlNum = Integer.parseInt(currentUrl);
    String url = urls.get(currentUrlNum);

    Document doc = Jsoup.connect(url).get();
    Elements results = doc.select("p, a");

    for(Element result : results) {
      info = info + result.toString();
    }
    return info;
  }
}
