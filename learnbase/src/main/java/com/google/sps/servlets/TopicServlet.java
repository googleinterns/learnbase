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

@WebServlet("/topics")
public class TopicServlet extends HttpServlet{

    @Override
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
        String topics = (String) entity.getProperty("topics"); 
        System.out.println(topics);
        if (topics.equals("")){
            response.getWriter().println("{}");
            return;
        }

        while (topics.length() > 0 && topics.substring(0,1).equals(",")){
            try{
                if (topics.length() > 1){
                    topics = topics.substring(1);
                }
                else{
                    topics = "";
                    entity.setProperty("topics", topics);
                    datastore.put(entity); 
                    response.getWriter().println("{}");
                    return;
                }
            } catch (Exception e){
                topics = "";
                entity.setProperty("topics", topics);
                datastore.put(entity); 

                response.getWriter().println("{}");
                return;
            }

        }
        String [] listedTopics = topics.split(",");
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
        Query query = 
        new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query); 
        Entity entity = results.asSingleEntity(); 
        if (entity == null){
            response.sendRedirect("/search.html");
        }

        String topic = request.getParameter("topic");
        String topics = (String) entity.getProperty("topics"); 
	ArrayList<String> urls = (ArrayList<String>) entity.getProperty("urls");

        if (topics.equals("")){
            entity.setProperty("topics", topic);
        }  else {
            topics += ",";
            topics += topic;
            entity.setProperty("topics", topics);
        }
	if(urls.isEmpty()) {
          urls = new ArrayList<>();
        }
	String[] values = topics.split(",");
        urls = getSearch(topic, urls);
	System.out.println(urls);
	entity.setProperty("urls", urls);
	datastore.put(entity);
        response.sendRedirect("/search.html");
    }

  private ArrayList<String> getSearch(String topic, ArrayList<String> urls) throws IOException {
    String google = "https://www.google.com/search";
    int num = 5;
    String searchURL = google + "?q=" + topic + "&num=" + num;

    Document doc  = Jsoup.connect(searchURL).userAgent("Chrome").get();
    Elements results = doc.select("a[href]:has(span)").select("a[href]:not(:has(div))");

    for (Element result : results) {
	String linkHref = result.attr("href");
	String linkText = result.text();
	if (linkHref.contains("https")) {
          urls.add(linkHref.substring(7, linkHref.indexOf("&"))); 
	} 
    }
    return urls;
  }

}
