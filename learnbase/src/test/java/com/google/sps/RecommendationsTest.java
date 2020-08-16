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

  HashMap<String, ArrayList<Double>> word2vecTest;
  RecommendationsServlet rServlet;

  @Before 
  public void setUp() {
    rServlet = new RecommendationsServlet();
    word2vecTest = new HashMap<String, ArrayList<Double>>();
    word2vecTest.put("a", new ArrayList<Double>(Arrays.asList(1.0, 0.0)));
    word2vecTest.put("b", new ArrayList<Double>(Arrays.asList(2.0, 1.0)));
    word2vecTest.put("c", new ArrayList<Double>(Arrays.asList(-2.0, 3.0)));  
    word2vecTest.put("d", new ArrayList<Double>(Arrays.asList(1.0, -1.0)));
    word2vecTest.put("e", new ArrayList<Double>(Arrays.asList(-1.0, -1.0)));
    word2vecTest.put("f", new ArrayList<Double>(Arrays.asList(-1.0, 1.0)));
    word2vecTest.put("g", new ArrayList<Double>(Arrays.asList(-1.0, 0.0)));
    word2vecTest.put("h", new ArrayList<Double>(Arrays.asList(0.0, -1.0)));
    word2vecTest.put("i", new ArrayList<Double>(Arrays.asList(0.0, 1.0)));
    word2vecTest.put("j", new ArrayList<Double>(Arrays.asList(1.0, 1.0)));
  }

  @Test 
  public void cosineDistanceTest1() {
    double expected = 2.0;
    double actual = rServlet.getCosineDistance(word2vecTest, "a", "b");

    Assert.assertEquals(expected, actual, 0.0001);
  } 

  @Test
  public void cosineDistanceTest2() {
    double expected = -2.0;
    double actual = rServlet.getCosineDistance(word2vecTest, "a", "c");

    Assert.assertEquals(expected, actual, 0.0001);
  }

  @Test
  public void cosineDistanceTest3() {
    double expected = -1.0;
    double actual = rServlet.getCosineDistance(word2vecTest, "b", "c");

    Assert.assertEquals(expected, actual, 0.0001);
  }


}