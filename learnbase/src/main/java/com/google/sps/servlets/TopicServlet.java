package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.*;
import java.io.PrintWriter;

@WebServlet("/topics")

public class TopicServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        UserService userService = UserServiceFactory.getUserService(); 
        System.out.println(request.getUserPrincipal());
        
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       
    }

}