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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

@RunWith(JUnit4.class) 
public final class RecommendationsTest {

  RecommendationsServlet rServlet;

  HashMap<String, ArrayList<Double>> word2vecTestDict1;
  HashMap<String, ArrayList<Double>> word2vecTestDict2;

  @Before 
  public void setUp() {
    rServlet = new RecommendationsServlet();
    word2vecTestDict1 = new HashMap<String, ArrayList<Double>>();
    word2vecTestDict1.put("a", new ArrayList<Double>(Arrays.asList(1.0, 0.0)));
    word2vecTestDict1.put("b", new ArrayList<Double>(Arrays.asList(2.0, 1.0)));
    word2vecTestDict1.put("c", new ArrayList<Double>(Arrays.asList(-2.0, 3.0)));  
    
    word2vecTestDict2 = new HashMap<String, ArrayList<Double>>();
    word2vecTestDict2.put("a", new ArrayList<Double>(Arrays.asList(0.0)));
    word2vecTestDict2.put("b", new ArrayList<Double>(Arrays.asList(1.0)));
    word2vecTestDict2.put("c", new ArrayList<Double>(Arrays.asList(4.0)));
    word2vecTestDict2.put("d", new ArrayList<Double>(Arrays.asList(9.0)));
    word2vecTestDict2.put("e", new ArrayList<Double>(Arrays.asList(16.0)));
    word2vecTestDict2.put("f", new ArrayList<Double>(Arrays.asList(25.0)));
    word2vecTestDict2.put("g", new ArrayList<Double>(Arrays.asList(36.0)));
    word2vecTestDict2.put("h", new ArrayList<Double>(Arrays.asList(49.0)));
    word2vecTestDict2.put("i", new ArrayList<Double>(Arrays.asList(64.0)));
    word2vecTestDict2.put("j", new ArrayList<Double>(Arrays.asList(81.0)));
    word2vecTestDict2.put("k", new ArrayList<Double>(Arrays.asList(100.0)));
  }

  @Test 
  public void cosineDistanceTest1() {
    double expected = 2.0;
    double actual = rServlet.getCosineDistance(word2vecTestDict1, "a", "b");

    Assert.assertEquals(expected, actual, 0.0001);
  } 

  @Test
  public void cosineDistanceTest2() {
    double expected = -2.0;
    double actual = rServlet.getCosineDistance(word2vecTestDict1, "a", "c");

    Assert.assertEquals(expected, actual, 0.0001);
  }

  @Test
  public void cosineDistanceTest3() {
    double expected = -1.0;
    double actual = rServlet.getCosineDistance(word2vecTestDict1, "b", "c");

    Assert.assertEquals(expected, actual, 0.0001);
  }

  @Test 
  public void closestWordsTest1() {
    ArrayList<String> expected = new ArrayList<String>(
        Arrays.asList("b","c","d","e","f","g","h","i","j","k"));
    ArrayList<String> actual = rServlet.getClosestWords(word2vecTestDict2, "a");

    Assert.assertEquals(expected, actual);
  }

  @Test 
  public void closestWordsTest2() {
    ArrayList<String> expected = new ArrayList<String>(
        Arrays.asList("j", "i","h", "g","f", "e","d","c","b","a"));
    ArrayList<String> actual = rServlet.getClosestWords(word2vecTestDict2, "k");

    Assert.assertEquals(expected, actual);
  }

  @Test 
  public void closestWordsTest3() {
    // Note this is cosine distance, not normal distance,
    // so order isn't necessarily intuitive.

    ArrayList<String> expected = new ArrayList<String>(
        Arrays.asList("k", "j", "i", "h", "g", "f", "d", "c", "b", "a"));
    ArrayList<String> actual = rServlet.getClosestWords(word2vecTestDict2, "e");

    Assert.assertEquals(expected, actual);
  }
}
