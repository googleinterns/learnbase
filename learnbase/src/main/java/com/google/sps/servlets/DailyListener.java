package com.google.sps.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.apache.log4j.Logger;
import java.io.IOException;
import com.google.appengine.api.datastore.*;
import java.io.*; 
import java.util.*; 

public class DailyListener{
  
  public void execute(JobExcecutionContext cntxt) throws JobExcecutionContext{


  }

  public static void scheduleTask(String time, String userEmail, String userId){

    String[] times = time.split(":");
    String cronExpresssion = "0 " + times[1] + " " + times[0] + " * * ?";  
    try {
      SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
      Scheduler sched  schedFact.getScheduler();
      sched.start();
      JobDetail jobDetail 
        new JobDetail(userId, "Scheduled User Email", EmailJob.class);
      jobDetail.getJobDataMap().put("type", "FULL");
      jobDetail.getJobDataMap().put("email", userEmail);
      jobDetail.getJobDataMap().put("id", userId);

      CronTrigger trigger  new CronTrigger("Daily Report", "Report Generation");
      trigger.setCronExpression(cronExpresssion);
      sched.scheduleJob(jobDetail, trigger);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}