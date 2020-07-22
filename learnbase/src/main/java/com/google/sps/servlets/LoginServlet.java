package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/userlogin")
public class LoginServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    response.setContentType("text/html");

    if(userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlRedirectLogout = "/index.html";
      String logoutUrl = userService.createLogoutURL(urlRedirectLogout);

      response.getWriter().println("<h1>Welcome " + userEmail + "!</h1>");
      response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
      response.getWriter().println("<p>Your current interests are: </p>");
    } else {
      String urlRedirectLogin = "/index.html";
      String loginUrl = userService.createLoginURL(urlRedirectLogin);

      response.getWriter().println("<h1>Welcome!</h1>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }
}