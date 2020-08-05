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
    if (topics.equals("")) { 
      return;
    }
    ArrayList<String> topicsInfo = new ArrayList<>();
    String[] topicsArray = topics.split(",");
    for (String topic : topicsArray) {
      String topicName = topic+"topic";
      String iteratorName = topic+"iterator";
      System.out.println(iteratorName);
      String iterator = (String) entity.getProperty(iteratorName);
      ArrayList<String> urls = (ArrayList<String>) entity.getProperty(topicName);
      int iteratorNum = Integer.parseInt(iterator);
      if (iteratorNum >= urls.size()) {
        topicsInfo.add("<h1>"+topic+":</h1>");
	topicsInfo.add("No more info for this topic!");
	continue;
      }
      String info = getInfo(urls, iterator);
      topicsInfo.add("<h1>"+topic+":</h1>");
      topicsInfo.add(info);
      iterator = Integer.toString(Integer.parseInt(iterator)+1);
      entity.setProperty(iteratorName, iterator);
    }
    datastore.put(entity);
    //System.out.println(info);
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(topicsInfo));

  }
  
  private String getInfo(ArrayList<String> urls, String currentUrl) throws IOException {
    String info = "";
    int currentUrlNum = Integer.parseInt(currentUrl);
    String url = urls.get(currentUrlNum);

    Document doc = Jsoup.connect(url).get();
    Elements results = doc.select("p");

    for(Element result : results) {
      info = info + result.toString();
    }
    return info;
  }
}
