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
  
  public void doGet(HttpServletResponse request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userId = user.getUserId(); 
    Query query = new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    String topics = (String) entity.getProperty("topics");
    ArrayList<String> urls = (ArrayList<String>) entity.getProperty("urls");
    String currentUrl = (String) entity.getProperty("currentUrl");
    if (currentUrl == null) {
      currentUrl = "0";
    }

    ArrayList<String> info = getInfo(urls, currentUrl);
    
    response.setContentType("text/html");
    response.getWriter().println(info);
  }
  
  private ArrayList<String> getInfo(ArrayList<String> urls, String currentUrl) throws IOException {
    ArrayList<String> info = new ArrayList<>();
    int currentUrlNum = Integer.parseInt(currentUrl);
    String url = urls.get(currentUrlNum);

    Document doc = Jsoup.connect(url).get();
    Elements results = doc.select("p");

    for(Element result : results) {
      info.add(result.toString());
    }
    return info;
  }
}
