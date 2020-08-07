package com.google.sps.servlets;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.*;
import static org.quartz.JobBuilder.*;
import com.google.appengine.api.datastore.*;
import org.quartz.JobDetail;

public class EmailJob  implements Job
{
    // This is the method that will be called by the scheduler when the trigger fires the job.
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
      JobKey key = context.getJobDetail().getKey(); 
      JobDataMap dataMap = context.getJobDetail().getJobDataMap();
      String userEmail = dataMap.getString("email");
      String userId = dataMap.getString("id");

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = 
      new Query("UserInfo")
      .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
      PreparedQuery results = datastore.prepare(query); 
      Entity entity = results.asSingleEntity();     
      EmailHandler handler = new EmailHandler();

      String nickname = (String) entity.getProperty("nickname");
      String topics = (String) entity.getProperty("topics");
      String message = "Welcome, " + nickname +"! Your chosen topics are " + topics; 
      handler.sendMessage("userEmail", message); 

    }
} 