package com.google.sps.servlets;

import com.sendgrid.*;
import java.io.IOException;

public class EmailHandler{

  public EmailHandler(){

  }

  public void sendMail() throws IOException{
    Email from = new Email("learnbase2020@gmail.com");
    String subject = "Sending with SendGrid is Fun";
    Email to = new Email("thefed@google.com");
    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
    //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 <-- API key

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