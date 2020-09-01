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
import java.io.*; 
import java.util.*; 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/deleteTopic")
public class DeleteTopicServlet extends HttpServlet{

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity entity = retrieveEntity();
    String topics = (String) entity.getProperty("topics"); 
    String [] listedTopics = topics.split(",");
    String removedTopic = request.getParameter("topic");
    String editedTopics = "";
    for (int i = 0; i < listedTopics.length; i++){
      if (!listedTopics[i].equals(removedTopic)){
        editedTopics += listedTopics[i]; 
        if (i+1 < listedTopics.length){
          editedTopics+=",";
        }
      } else {
        removedTopic="";
      }
    }
    while (editedTopics.length() > 1 && editedTopics.substring(editedTopics.length()-1).equals(",")){
      editedTopics = editedTopics.substring(0, editedTopics.length()-1);
    }
    if (editedTopics.length() == 1 && editedTopics.equals(",")){
      editedTopics = "";
    }
    entity.setProperty("topics", editedTopics);
    datastore.put(entity);
    response.sendRedirect("/search.html");
  }

  private Entity retrieveEntity(){
    UserService userService = UserServiceFactory.getUserService(); 
    User user = userService.getCurrentUser();
    String userId = user.getUserId(); 

    Query query = 
      new Query("UserInfo")
      .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query); 
    Entity entity = results.asSingleEntity(); 
    return entity;
  }

  private ArrayList<String> deleteUrls(String topics) throws IOException {
    ArrayList<String> newUrls = new ArrayList<>();
    String[] topicArray = topics.split(",");
    for (String topic : topicArray) {
      String google = "https://www.google.com/search";
      int num = 5;
      String searchURL = google + "?q=" + topic + "&num=" + num;
      Document doc  = Jsoup.connect(searchURL).userAgent("Chrome").get();
      Elements results = doc.select("a[href]:has(span)").select("a[href]:not(:has(div))");
      for (Element result : results) {
        String linkHref = result.attr("href");
        String linkText = result.text();
        if (linkHref.contains("https")) {
          newUrls.add(linkHref.substring(7, linkHref.indexOf("&")));
        } 
      }
    }
    System.out.println(newUrls);
    return newUrls;
  }
}

