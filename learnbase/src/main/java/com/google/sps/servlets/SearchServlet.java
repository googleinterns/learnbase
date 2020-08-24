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
import javax.script.*;
import java.net.URL;
import java.net.URLConnection;

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
      topicsInfo.add("<h2>No topics yet! Search something you want to know more about to get info.</h2>");
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
      Boolean advanced = (Boolean)entity.getProperty("advanced"+topic);
      System.out.println(advanced);
      System.out.println(topic + " iterator in search: " + iterator);

      
      //If there are no more urls for this topic, 
      //Move to an advanced search
      //If there are no more urls in the advanced search 
      //print that there is no more info for the topic 
      if (iteratorNum > urls.size()) {
        if (advanced) {
          topicsInfo.add(0, "No more info for this topic!");
          topicsInfo.add(0, "<h1>"+topic.toUpperCase()+"</h1>");
          continue;
        } else {
                System.out.println("advanced");
                String advancedTopic = "advanced"+topic;
          urls = getSearch(advancedTopic);
          if(urls.isEmpty()) {
                  topicsInfo.add(0, "No more info for this topic!");
            topicsInfo.add(0, "<h1>"+topic.toUpperCase()+":</h1>");
            continue;
                }
          iterator = "0";
          iteratorNum = 0;
          entity.setProperty(advancedTopic, true);
          entity.setProperty(topicName, urls);
        }
	      System.out.println(urls);
      }
      String url = urls.get(iteratorNum);
      //iterator = Integer.toString(Integer.parseInt(iterator)+1);
      //System.out.println("After iteration: " + iterator);
      entity.setProperty(iteratorName, iterator);      
      String info = "<iframe src=\"" + url + "\" style=\"height:600px;width:80%;\"></iframe>"; 
      topicsInfo.add(0, info);
      topicsInfo.add(0, "<h1>"+topic.toUpperCase()+"</h1>");
    }
    datastore.put(entity);
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(topicsInfo));

  }

 public void changeIterator() {
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
   String[] topicsArray = topics.split(",");
   for(String topic : topicsArray) {
     String iteratorName = topic+"iterator";
     String iterator = (String) entity.getProperty(iteratorName);
     System.out.println(topic + " iterator in change iterator: " + iterator);
     iterator = Integer.toString(Integer.parseInt(iterator)+1);
     System.out.println("After iteration: " + iterator);
     entity.setProperty(iteratorName, iterator);
   }
   datastore.put(entity);
 }
  
  //Gets the info off a page for a given url 
 // private String getInfo(ArrayList<String> urls, String currentUrl) throws IOException {
 //   String info = "";
 //   int currentUrlNum = Integer.parseInt(currentUrl);
 //   String url = urls.get(currentUrlNum);
 //   System.out.println(url);

 //   Document doc = Jsoup.connect(url).get();
 //   Elements results = doc.select("p, a");

 //   for(Element result : results) {
 //     info = info + result.toString();
 //   }
 //   return info;
 // }

  public ArrayList<String> getSearch(String topic) throws IOException {
    String google = "https://www.google.com/search";
    int num = 20;
    String searchURL = google + "?q=" + topic + "&num=" + num;
    ArrayList<String> urls = new ArrayList<>();
    System.out.println(searchURL);

    Document doc  = Jsoup.connect(searchURL).userAgent("Chrome").get();
    Elements results = doc.select("a[href]:has(span)").select("a[href]:not(:has(div))");

    for (Element result : results) {
        String linkHref = result.attr("href");
        String linkText = result.text();
        if (linkHref.contains("https")) {
          String url = linkHref.substring(7, linkHref.indexOf("&"));
	  System.out.println(url);
	  if("/www.google.com/search?num=20".equals(url)) {
            continue;
	  }
	  URL obj = new URL(url);
	  URLConnection conn = obj.openConnection();
	  Map<String, List<String>> map = conn.getHeaderFields();
	  boolean noIFrame = false;
	  for(Map.Entry<String, List<String>> entry : map.entrySet()) {
 	    String key = entry.getKey();
	    if("X-Frame-Options".equals(key)) { 
              noIFrame = true;
	    }
          }
	  if(!noIFrame) {
            urls.add(url);
	  }
      } 
    }
    return urls; 
  }
}
