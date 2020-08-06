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
    if (topics.trim().equals("")) {
      Gson gson = new Gson();
      ArrayList<String> topicsInfo = new ArrayList<>();
      topicsInfo.add("<h2>No topics yet! Search something you want to know more about to get info</h2>");
      response.getWriter().println(gson.toJson(topicsInfo));
      return;
    }
    System.out.println("topics:" + topics);
    ArrayList<String> topicsInfo = new ArrayList<>();
    String[] topicsArray = topics.split(",");
    System.out.println("topics array:" + topicsArray);
    for (String topic : topicsArray) {
      String topicName = topic+"topic";
      String iteratorName = topic+"iterator";
      System.out.println(iteratorName);
      String iterator = (String) entity.getProperty(iteratorName);
      ArrayList<String> urls = (ArrayList<String>) entity.getProperty(topicName);
      int iteratorNum = Integer.parseInt(iterator);
      if (iteratorNum >= urls.size()) {
	topicsInfo.add(0,"No more info for this topic!");
	topicsInfo.add(0, "<h1>"+topic+":</h1>");
	continue;
      }
      String info = getInfo(urls, iterator);
      topicsInfo.add(0, info);
      topicsInfo.add(0, "<h1>"+topic+":</h1>");
      iterator = Integer.toString(Integer.parseInt(iterator)+1);
      entity.setProperty(iteratorName, iterator);
    }
    System.out.println(topicsInfo);
    datastore.put(entity);
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(topicsInfo));

  }
  
  private String getInfo(ArrayList<String> urls, String currentUrl) throws IOException {
    String info = "";
    int currentUrlNum = Integer.parseInt(currentUrl);
    String url = urls.get(currentUrlNum);

    System.out.println(url);

    Document doc = Jsoup.connect(url).get();
    Elements results = doc.select("p");

    for(Element result : results) {
      info = info + result.toString();
    }
    return info;
  }
}
