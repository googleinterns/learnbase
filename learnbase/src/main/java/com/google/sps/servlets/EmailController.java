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


@WebServlet("/emails")
public class EmailController extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Date d = new Date(); 
    int hour = d.getHours();
    int minute = d.getMinutes();
    Query q;
    if (minute < 15){
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
        int entityMinute = (Integer) entity.getProperty("minute");
        if (entityMinute >= 45){
          String encryptedMail = (String) entity.getProperty("mail"); 
          String email = decryptEmail(encryptedMail); 
          EmailHandler handler = new EmailHandler(); 
          handler.sendMessage(encryptedMail, "Scheduler works");
        }
      }
    }
    else{
      q = new Query("UserInfo")
        .setFilter(new FilterPredicate("hour", FilterOperator.EQUAL, hour));
      PreparedQuery pq = datastore.prepare(q);
      for (Entity entity: pq.asIterable()){
        int entityMinute = (Integer)(entity.getProperty("minute"));
        if (minute-15 <= entityMinute && entityMinute < minute){
          String encryptedMail = (String) entity.getProperty("mail"); 
          String email = decryptEmail(encryptedMail); 
          EmailHandler handler = new EmailHandler(); 
          handler.sendMessage(encryptedMail, "Scheduler works");
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

}
