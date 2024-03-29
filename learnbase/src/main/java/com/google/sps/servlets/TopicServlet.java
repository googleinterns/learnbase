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
import java.net.URL;
import java.net.URLConnection;


@WebServlet("/topics")
public class TopicServlet extends HttpServlet{

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService(); 
    User user = userService.getCurrentUser();
    String userId = user.getUserId(); 
    Query query = new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query); 
    Entity entity = results.asSingleEntity(); 
    String topics = (String) entity.getProperty("topics"); 
    if (topics.equals("")) {
      response.getWriter().println("{}");
      return;
    }
    while (topics.length() > 0 && topics.substring(0,1).equals(",")) {
    //Clears commas from the beginning of the topics string to avoid blank topics
      try {
        if (topics.length() > 1) {
          topics = topics.substring(1);
        } else {
           topics = "";
           entity.setProperty("topics", topics);
           datastore.put(entity); 
           response.getWriter().println("{}");
           return;
        }
      } catch (Exception e) { 
        topics = "";
        entity.setProperty("topics", topics);
        datastore.put(entity); 
        response.getWriter().println("{}");
        return;
      }
    }  
    String [] listedTopics = topics.split(",");
    for (int i = 0; i < listedTopics.length; i++) {
      listedTopics[i] = listedTopics[i].substring(0,1).toUpperCase() + listedTopics[i].substring(1);
    }
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
    Query query = new Query("UserInfo")
	.setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query); 
    Entity entity = results.asSingleEntity(); 
    if (entity == null) {
      response.sendRedirect("/search.html");
    }
    String topic = request.getParameter("topic").trim().replaceAll(" +", " ").toLowerCase();
    String topics = (String) entity.getProperty("topics"); 
    if (topics.contains(topic)) {
      topics = containsTopic(topics, topic);
      entity.setProperty("topics", topics);
      datastore.put(entity);
      response.sendRedirect("/search.html");
      return;
    }
    ArrayList<String> urls = getSearch(topic);
    topics = notContainsTopic(topics, topic, entity, datastore, urls);
    response.sendRedirect("/search.html");
  }
 
  //Add a new topic to the datastore 
  private String notContainsTopic(String topics, String topic, Entity entity, DatastoreService datastore, ArrayList<String> urls) {
    if (topics.equals("")) {
      entity.setProperty("topics", topic);
      topics = topic;
    } else {
      topics += ",";
      topics += topic;
      entity.setProperty("topics", topics);
    }
    entity.setProperty(topic+"topic", urls);
    entity.setProperty(topic+"iterator", "0");
    entity.setProperty("advanced"+topic, false);
    datastore.put(entity);
    return topics;
  }
  
  //If the topic already exists 
  private String containsTopic(String topics, String topic) {
    List<String> listOfTopics = new ArrayList<String>();
    String str[] = topics.split(",");
    for (int i = 0; i < str.length; i++) {
      listOfTopics.add(str[i]);
    }
    if(!listOfTopics.contains(topic)) {
      topics += ",";
      topics += topic;
    } else {
      listOfTopics.remove(topic);
      topics = "";
      for (int i = 0; i < listOfTopics.size(); i++) {
        topics += listOfTopics.get(i);
	topics += ",";
      }
      topics += topic;
    }
    return topics;
  } 

  //Given a topic, gets urls from google 
  private ArrayList<String> getSearch(String topic) throws IOException {
    String google = "https://www.google.com/search";
    int num = 20;
    String searchURL = google + "?q=" + topic + "&num=" + num;
    ArrayList<String> urls = new ArrayList<>();
    Document doc  = Jsoup.connect(searchURL).userAgent("Chrome").get();
    Elements results = doc.select("a[href]:has(span)").select("a[href]:not(:has(div))");
    for (Element result : results) {
      String linkHref = result.attr("href");
      String linkText = result.text();
      if (linkHref.contains("https")) {
	String url = linkHref.substring(7, linkHref.indexOf("&"));
	if ("/www.google.com/search?num=20".equals(url)) {
          continue;
	}
	boolean noIFrame = checkIframe(url);
	if (!noIFrame) {
          urls.add(url);
        }
      } 
    }
    return urls;  
  }
  
  //Check if the url allows for iframe 
  private boolean checkIframe (String url) throws IOException {
    URL obj = new URL(url); 
    URLConnection conn = obj.openConnection();
    Map<String, List<String>> map = conn.getHeaderFields();
    boolean noIframe = false;
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      String key = entry.getKey();
      if ("X-Frame-Options".equals(key)) {
        noIframe = true;
      }
    }
    return noIframe;
  }
}

