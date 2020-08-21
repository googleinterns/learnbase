package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import javax.crypto.Cipher;
import java.time.format.DateTimeFormatter;  
import java.security.*;
import java.time.LocalDateTime;    
import java.util.Date;
import java.util.*;

@WebServlet("/emails")
public class EmailController extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //This doGet is called once every 10 minutes 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Date d = new Date(); 
    int hour = d.getHours();
    int minute = d.getMinutes();
    //Current time frame 

    Query q;

    if (minute < 5){ //If current time is less than 5, check the previous hour 
      if (hour == 0){
        q = new Query("UserInfo")
          .setFilter(new FilterPredicate("hour", FilterOperator.EQUAL, 23));
      }
      else{

        q = new Query("UserInfo")
          .setFilter(new FilterPredicate("hour", FilterOperator.EQUAL, hour-1));
      }
      PreparedQuery pq = datastore.prepare(q);
      for (Entity entity: pq.asIterable()){
        Long em = (Long) entity.getProperty("minute");
        int entityMinute = em.intValue();
        if (entityMinute >= 55){
          sendEmail((String) entity.getProperty("mail"), entity);
	        SearchServlet searchServlet = new SearchServlet();
	        searchServlet.changeIterator();
        }
      }
    }
    else{ 
      q = new Query("UserInfo")
        .setFilter(new FilterPredicate("hour", FilterOperator.EQUAL, hour));
      PreparedQuery pq = datastore.prepare(q);
      for (Entity entity: pq.asIterable()){
        Long em = (Long) entity.getProperty("minute");
        int entityMinute = em.intValue();
        if (minute-5 <= entityMinute && entityMinute < minute){
          sendEmail((String) entity.getProperty("mail"), entity); 
          SearchServlet searchServlet = new SearchServlet();
          searchServlet.changeIterator();
        }
      }
    }
    response.getWriter().println(200);
  }

  private String decryptEmail (String email){
    try{
      Signature sign = Signature.getInstance("SHA256withRSA");
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
      keyPairGen.initialize(2048);
      KeyPair pair = keyPairGen.generateKeyPair();   
      PublicKey publicKey = pair.getPublic();  
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      byte[] cipherText = email.getBytes();

      cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
      byte[] decipheredText = cipher.doFinal(cipherText);
      return new String (decipheredText); 
    } catch (Exception e){
      e.printStackTrace();
      return "failed";
    }
  
  }

  private void sendEmail(String email, Entity entity){
    EmailHandler handler = new EmailHandler();
    String plaintext = "Welcome to your daily Learnbase email! Look below to find info on topics you've selected! ";
    String html = buildHTML(entity);
    handler.sendPlainAndHTML(email, plaintext, html);
  }

  private String buildHTML (Entity entity){   
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //Replaced arraylist of topic urls with a string to be served at html
    String html = "You can either visit the sites listed below or you can visit the " +
      " <a href=\"learnbase-step-2020.appspot.com/info.html\"> Learnbase Info Page</a>";
    String topics = (String) entity.getProperty("topics");
    

    if (topics.trim().equals("")) {
      ArrayList<String> topicsInfo = new ArrayList<>();
      html += "<h2>No topics yet! Search something you want to know more about to get info.</h2>";
      return html;
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
      
      if (iteratorNum >= urls.size()) {
        if (advanced != null && advanced) {
          html += "<br>" + "<h2>"+topic.toUpperCase()+"</h2>" + "<br> No more info for this topic!";
          continue;
        } else {
          System.out.println("advanced");
          String advancedTopic = "advanced"+topic;
          SearchServlet searcher= new SearchServlet();
          try{
            urls = searcher.getSearch(advancedTopic);
            if(urls.isEmpty()) {
              html += "<br>" + "<h2>"+topic.toUpperCase()+"</h2>" + "<br> No more info for this topic!";
              continue;
            }
          } catch (Exception e){
            e.printStackTrace();
          }
          iterator = "0";
          iteratorNum = 0;
          entity.setProperty(advancedTopic, true);
          entity.setProperty(topicName, urls);
        }
	      System.out.println(urls);
      }
      String url = urls.get(iteratorNum);
      String info = "<iframe src=\"" + url + "\" style=\"height:600px;width:80%;\"></iframe>"; 
      html += "<br>" + "<h2>"+topic.toUpperCase()+"</h2>" + "<br> <a href=\"" + url + "\">Daily info</a>";

      //Increment iterator so that the next day they get new info 
      iterator = Integer.toString(Integer.parseInt(iterator)+1);
      entity.setProperty(iteratorName, iterator);
    }
    datastore.put(entity);
    return html;
  }

}
