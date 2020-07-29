// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

/** Servlet that returns HTML that contains the page view count. */
@WebServlet("/recommend-topics")
public class PythonServlet extends HttpServlet {

  Map wordInfo;
  Process mProcess;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    wordInfo = gson.fromJson(new FileReader("py/json/word_cache.json"), 
        new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType());
    String topic = request.getParameter("topic").replace(" ", "_");
    
    if (!wordInfo.containsKey(topic)) {
      querySimilarWords(topic);
      wordInfo = gson.fromJson(new FileReader("py/json/word_cache.json"),
        new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType());
    }

    String json = gson.toJson(wordInfo.get(topic));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  private void querySimilarWords(String topic) {
    Process process;
    try {
      process = Runtime.getRuntime().exec(new String[]{"python", "py/main.py", topic});
      mProcess = process;
    } catch(Exception e) {
      System.out.println("Exception Raised" + e.toString());
    }
    InputStream stdout = mProcess.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
    String line;
    try{
      while((line = reader.readLine()) != null){
        System.out.println("stdout: "+ line);
      }
    } catch(IOException e){
      System.out.println("Exception in reading output"+ e.toString());
    }

  }
}