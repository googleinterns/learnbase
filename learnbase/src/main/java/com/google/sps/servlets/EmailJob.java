package com.google.sps.servlets;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.*;
 
public class EmailJob  implements Job
{
    // This is the method that will be called by the scheduler when the trigger fires the job.
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException
    {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = 
      new Query("UserInfo")
      .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
      PreparedQuery results = datastore.prepare(query); 
      Entity entity = results.asSingleEntity();     
      EmailHandler handler = new EmailHandler();
      JobKey key = context.getJobDetail().getKey(); 
      JobDataMap dataMap = context.getJobDetail().getJobDataMap();
      String userEmail = dataMap.getString("email");
      String userID = dataMap.getString("id");
      String nickname = (String) entity.getProperty("nickname");
      String topics = (String) entity.getProperty("topics");
      String message = "Welcome, " + nickname +"! Your chosen topics are " + topics; 
      handler.sendMessage("userEmail", message); 

    }
} 