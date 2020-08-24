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

import java.util.*;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

/** Servlet that returns HTML that contains the page view count. */
@WebServlet("/recommend-topics")
public class RecommendationsServlet extends HttpServlet {

  HashMap<String, ArrayList<Double>> words2vecs = new HashMap<String, ArrayList<Double>>();
  HashMap<String, ArrayList<String>> wordCache = new HashMap<String, ArrayList<String>>();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.setContentType("application/json;");

    String topic = request.getParameter("topic").trim().replaceAll(" +", " ").replace(" ", "_").toLowerCase();
    
    if (!wordCache.containsKey(topic)) {
      words2vecs = (HashMap<String, ArrayList<Double>>) gson.fromJson(new FileReader("model/word_embedding.json"), words2vecs.getClass());
      HashSet<String> omittedWords = new HashSet<String>(
          Arrays.asList("lecture_notes", "tutorial", "introductory", "textbook", "mathematical", 
          "scientific", "study", "algorithmic", "interdisciplinary", "massachusetts_institute", "analytical", "theoretical", "logical"));
      
      for (String word : omittedWords) {
        words2vecs.remove(word);
      }
      
      if (!words2vecs.containsKey(topic)) {
        response.getWriter().println("[]");
        wordCache.put(topic, new ArrayList<String>());

        return;
      }
      ArrayList<String> closestWords = getClosestWords(words2vecs, topic);
      
      wordCache.put(topic, closestWords);
    }
    
    String similarTopics = gson.toJson(wordCache.get(topic)); 
    response.getWriter().println(similarTopics);
  }

  /**
   * Takes in a hashmap mapping words to their corresponding word embeddings,
   * represented as ArrayLists of doubles, as well as the topic we want to find
   * other similar topics for. 
   * Outputs top 10 similar words based off of word2vec model. 
   */
  public ArrayList<String> getClosestWords(HashMap<String, ArrayList<Double>> words2vecs, String topic) {
    String[] currentTopTenWords = new String[10];
    Double[] currentTopTenDistances = new Double[10];
    Arrays.fill(currentTopTenDistances, Double.NEGATIVE_INFINITY);

    ArrayList<String> closestWords = new ArrayList<String>();

    words2vecs.forEach((word, vec) -> {
      if (!topic.equals(word)) {
        double distance = getCosineDistance(words2vecs, topic, word);
        for (int i = 0; i < 10; i++) {
          if (distance > currentTopTenDistances[i]) {
            for (int j = 9; j > i; j--) {
              currentTopTenDistances[j] = currentTopTenDistances[j - 1];
              currentTopTenWords[j] = currentTopTenWords[j-1];
            }
            currentTopTenDistances[i] = distance;
            currentTopTenWords[i] = word;
            break;
          }
        }
      }
    });

    for (String word : currentTopTenWords) {
      closestWords.add(word);
    }

    return closestWords;
  } 

  /**
   * Takes two word vectors and multiplies them together 
   * and returns the result.  
   */
  public double getCosineDistance(HashMap<String, ArrayList<Double>> words2vecs, String word1, String word2) {
    double distance = 0.0;
    for (int i = 0; i < words2vecs.get(word1).size(); i++) {
      distance += words2vecs.get(word1).get(i) * words2vecs.get(word2).get(i);
    }
    return distance;
  }

}