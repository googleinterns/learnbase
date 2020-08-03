package com.google.sps.servlets;

// import com.sendgrid.*;

//mailjet imports 

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class EmailHandler{
  private const String API_KEY = "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";
  private const String PUB_KEY = "2065063d2f679c68571c386cf8d13767";
  private const String PRIV_KEY =  "2921119afe3fec7e64e36d4677fc4a75";
  public EmailHandler(){

  }

  public void sendWelcomeMail(String userEmail) throws MailjetException, MailjetSocketTimeoutException{
    MailjetClient client;
    MailjetRequest request;
    MailjetResponse response;
    client = new MailjetClient(PUB_KEY, PRIV_KEY, new ClientOptions("v3.1"));
    request = new MailjetRequest(Emailv31.resource)
    .property(Emailv31.MESSAGES, new JSONArray()
    .put(new JSONObject()
    .put(Emailv31.Message.FROM, new JSONObject()
    .put("Email", "learnbase2020@gmail.com")
    .put("Name", "Learnbase"))
    .put(Emailv31.Message.TO, new JSONArray()
    .put(new JSONObject()
    .put("Email", userEmail)
    .put("Name", "Federick")))
    .put(Emailv31.Message.SUBJECT, "Greetings from Mailjet.")
    .put(Emailv31.Message.TEXTPART, "My first Mailjet email")
    .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger 1, welcome to <a href='https://www.mailjet.com/'>Mailjet</a>!</h3><br />May the delivery force be with you!")
    .put(Emailv31.Message.CUSTOMID, "AppGettingStartedTest")));
    response = client.post(request);
    System.out.println(response.getStatus());
    System.out.println(response.getData());
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  /* Sendgrid stuff
  public void sendWelcomeMail(String userEmail) throws IOException{
    Email from = new Email("learnbase2020@gmail.com");
    String subject = "Welcome to LearnBase!";
    Email to = new Email(userEmail);
    Content content = new Content("text/plain", "Welcome to LearnBase! We're very happy to have you." + 
      "\n If you haven't already done so, please choose a topic under the Search page and select a time " +
      "to recieve daily emails! ");
    Mail mail = new Mail(from, subject, to, content);
    sender(mail);
  }

  private void sendPersonalWelcome(String userEmail, String nickame) throws IOException{
    Email from = new Email("learnbase2020@gmail.com");
    String subject = "Welcome to LearnBase!";
    Email to = new Email(userEmail);
    Content content = new Content("text/plain", "Welcome to LearnBase" + nickname + "! We're very happy to have you." + 
      "\n If you haven't already done so, please choose a topic under the Search page and select a time " +
      "to recieve daily emails! ");
    Mail mail = new Mail(from, subject, to, content);
    sender(mail);
  }

  public void sendPreparedMail(String userEmail, String nickname, String message) throws IOException{
    Email from = new Email("learnbase2020@gmail.com");
    String subject = "Your daily dose of knowledge";
    Email to = new Email(userEmail); 
    Content content = new Content(message);
    Mail mail = new Mail(from, subject, to, content); 
    sender(mail);
  }

  public void sender(Mail mail) throws IOException{
    SendGrid sg = new SendGrid(API_KEY);

    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      System.out.println(response.getStatusCode());
      System.out.println(response.getBody());
      System.out.println(response.getHeaders());
    } catch (IOException ex) {
      throw ex;
    }
  }
  */
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
} 