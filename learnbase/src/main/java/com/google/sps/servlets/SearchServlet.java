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
    Query query = new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    String topics = (String) entity.getProperty("topics");
    Gson gson = new Gson();
    //If the user has not searched any topics yet, print that they have no topics
    if (topics.trim().equals("")) {
      ArrayList<String> topicsInfo = new ArrayList<>();
      topicsInfo
          .add("<h2>No topics yet! Search something you want to know more about to get info.</h2>");
      response.getWriter().println(gson.toJson(topicsInfo));
      return;
    }
    String[] topicsArray = topics.split(",");
    ArrayList<String> topicsInfo = getInfo(topicsArray, entity);
    datastore.put(entity);
    response.getWriter().println(gson.toJson(topicsInfo));
  }

  // Go through each topic the user has and get the daily urls info 
  public ArrayList<String> getInfo (String[] topicsArray, Entity entity) throws IOException {
    ArrayList<String> topicsInfo = new ArrayList<String>();
    for (String topic : topicsArray) {
      String topicName = topic+"topic";
      String iteratorName = topic+"iterator";
      String iterator = (String) entity.getProperty(iteratorName);
      ArrayList<String> urls = (ArrayList<String>) entity.getProperty(topicName);
      int iteratorNum = Integer.parseInt(iterator);
      boolean advanced = (Boolean)entity.getProperty("advanced"+topic);
      if (iteratorNum >= urls.size()) {
        topicsInfo = iteratorEnd(advanced, entity, topicsInfo, urls, topic);
	continue;
      }
      String url = urls.get(iteratorNum);
      String info = "<iframe src=\"" + url + "\" style=\"height:600px;width:80%;\"></iframe>";
      topicsInfo.add(0, info);
      topicsInfo.add(0, "<h1>"+topic.toUpperCase()+"</h1>");
    }
    return topicsInfo; 
  }

  // If the iterator has reached the end of the url list, either go to advanced or tell the user 
  // they are at the end of the topic 
  public ArrayList<String> iteratorEnd (boolean advanced, Entity entity, 
       ArrayList<String> topicsInfo, ArrayList<String> urls, String topic) 
       throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    if (advanced) {
      topicsInfo.add(0, "No more info for this topic!");
      topicsInfo.add(0, "<h1>"+topic.toUpperCase()+":</h1>");
    } else {
      String advancedTopic = "advanced"+topic;
      urls = getSearch(advancedTopic);
      if (urls.isEmpty()) {
        topicsInfo.add(0, "No more info for this topic!");
        topicsInfo.add(0, "<h1>"+topic.toUpperCase()+":</h1>");	
      } else { 
        entity.setProperty(topic+"iterator", "1");
        entity.setProperty(topic+"topic", urls);
        entity.setProperty(advancedTopic, true);
        datastore.put(entity);
	String url = urls.get(0);
	String info = "<iframe src=\"" + url + "\" style=\"height:600px;width:80%;\"></iframe>"; 
        topicsInfo.add(0, info);
        topicsInfo.add(0, "<h1>"+topic.toUpperCase()+"</h1>");
      }
    }
    return topicsInfo;
  }

  // Get the lsit of urls for a topic
  public ArrayList<String> getSearch (String topic) throws IOException {
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
	if ("/www.google.com/search?num=20".equals(url)) {
          continue;
	}
        boolean noIframe = checkIframe(url);
	if (!noIframe) {
          urls.add(url);
	}
      } 
    }
    return urls; 
  }

  // Make sure each url allows iframe 
  public boolean checkIframe (String url) throws IOException {
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
