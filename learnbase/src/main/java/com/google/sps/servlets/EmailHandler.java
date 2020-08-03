package com.google.sps.servlets;

import com.sendgrid.*;
import java.io.IOException;

public class EmailHandler{

  public EmailHandler(){

  }

  public void sendWelcomeMail(String userEmail) throws IOException{
    Email from = new Email("learnbase2020@gmail.com");
    String subject = "Welcome to LearnBase!";
    Email to = new Email(userEmail);
    Content content = new Content("text/plain", "Welcome to LearnBase! We're very happy to have you." + 
      "\n If you haven't already done so, please choose a topic under the Search page and select a time " +
      "to recieve daily emails! ");
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid("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");
    // <-- API key

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
} 