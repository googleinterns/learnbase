package com.google.sps.servlets;


import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmailHandler{

  public EmailHandler(){

  }

  public void sendMail() {
    MailjetClient client;
    MailjetRequest request;
    MailjetResponse response = null;
    //client = new MailjetClient(System.getenv("2065063d2f679c68571c386cf8d13767"), System.getenv("2921119afe3fec7e64e36d4677fc4a75"), new ClientOptions("v3.1"));
    client = new MailjetClient(System.getenv("2065063d2f679c68571c386cf8d13767"), System.getenv("2921119afe3fec7e64e36d4677fc4a75"), new ClientOptions("v3.1"));
    request = new MailjetRequest(Emailv31.resource)
    .property(Emailv31.MESSAGES, new JSONArray()
    .put(new JSONObject()
    .put(Emailv31.Message.FROM, new JSONObject()
    .put("Email", "thefed@google.com")
    .put("Name", "Federick"))
    .put(Emailv31.Message.TO, new JSONArray()
    .put(new JSONObject()
    .put("Email", "thefed@google.com")
    .put("Name", "Federick")))
    .put(Emailv31.Message.SUBJECT, "Greetings from Mailjet.")
    .put(Emailv31.Message.TEXTPART, "My first Mailjet email")
    .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger 1, welcome to <a href='https://www.mailjet.com/'>Mailjet</a>!</h3><br />May the delivery force be with you!")
    .put(Emailv31.Message.CUSTOMID, "AppGettingStartedTest")));

    try{
      response = client.post(request);
    } catch (Exception e){
      System.out.println("catch");
      System.out.println(e);
    }
      
    System.out.println(response.getStatus());
    System.out.println(response.getData());
  }
} 