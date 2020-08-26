package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.*;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import java.security.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import javax.crypto.Cipher;


@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<h1>Set Nicknames</h1>");
    UserService userService = UserServiceFactory.getUserService();

    // If the user is logged in, give them a nickname form
    // If the user is logged out, give them a login button 
    if (userService.isUserLoggedIn()) {
      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      out.println("<p>Set your nickname here:</p>");
      out.println("<form method=\"POST\" action=\"/nickname\">");
      out.println("<input name=\"nickname\" value=\"" + nickname + "\" required />");
      out.println("<br/>");
      out.println("<button>Submit</button>");
      out.println("</form>");
    } else { 
      String loginUrl = userService.createLoginURL("/nickname");
      out.println("<h1>Welcome!</h1>");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }

  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

    TimeZone timeZone = TimeZone.getDefault();
    int offset = (int) ((timeZone.getOffset( System.currentTimeMillis())/(1000*60*60)));
    UserService userService = UserServiceFactory.getUserService();
    if(!userService.isUserLoggedIn()) {
      response.sendRedirect("/nickname");
      return;
    }

    String nickname = request.getParameter("nickname");
    String id = userService.getCurrentUser().getUserId();
    EmailHandler handler = new EmailHandler();
    try{
      handler.sendWelcomeMail(userService.getCurrentUser().getEmail());
    } catch (Exception e){
      System.out.println(e);
    }
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("nickname", nickname);
    entity.setProperty("topics", "");
    entity.setProperty("hour", 12 );
    entity.setProperty("minute", 00);
    entity.setProperty("offset", offset);
    entity.setProperty("optIn", y);
    entity.setProperty("mail", (String) userService.getCurrentUser().getEmail());
    datastore.put(entity);

    response.sendRedirect("/index.html");
  }

  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = 
      new Query("UserInfo")
       .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }

  private String encryptEmail(String email){
    try{
      Signature sign = Signature.getInstance("SHA256withRSA");
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
      keyPairGen.initialize(2048);
      KeyPair pair = keyPairGen.generateKeyPair();   
      PublicKey publicKey = pair.getPublic(); 
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] input = email.getBytes();	  
      cipher.update(input);
      byte[] cipherText = cipher.doFinal();
      return new String(cipherText, "UTF8");
    } 
    catch(Exception e){
      e.printStackTrace(); 
      return "email failed";
    }
  }

}
