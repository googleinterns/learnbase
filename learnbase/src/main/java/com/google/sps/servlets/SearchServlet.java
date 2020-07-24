package com.google.sps.servlets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList; 

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
     String topic = request.getParameter("topic");
  }
}
