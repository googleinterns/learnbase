package com.google.sps.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.*;
import org.quartz.JobDetail;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.JobBuilder.*;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import java.io.IOException;
import com.google.appengine.api.datastore.*;
import java.io.*; 
import java.util.*; 

public class DailyListener{
  

  public static void scheduleTask(String time, String userEmail, String userId){

    String[] times = time.split(":");
    String cronExpresssion = "0 " + times[1] + " " + times[0] + " * * ?";  
    try {
      SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
      Scheduler sched  = schedFact.getScheduler();
      sched.start();
      JobDetail jobDetail = newJob(EmailJob.class)
        .withIdentity(userId, "Scheduled User Email")
        .usingJobData("type", "FULL")
        .usingJobData("email", userEmail)
        .usingJobData("id", userId)
        .build();

      CronTrigger trigger = newTrigger()
        .withIdentity("Daily Report", "Report Generation")
        .withSchedule(cronSchedule(cronExpresssion))
        .forJob(userId, "Scheduled User Email")
        .build();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}