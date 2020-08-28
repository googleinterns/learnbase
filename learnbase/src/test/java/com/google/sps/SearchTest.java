package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.*;
import java.io.*;

@RunWith(JUnit4.class)
public final class SearchTest {

  private SearchServlet search;

  @Before
  public void setUp() {
    search = new SearchServlet();
  }
  
  @Test
  public void getSearchURLs() throws IOException {
    ArrayList<String> actual = search.getSearch("javascript");
    ArrayList<String> expected = new ArrayList<String>();

    expected.add("https://en.wikipedia.org/wiki/JavaScript");

    Assert.assertEquals(expected, actual);

  }
  
  @Test 
  public void nonAcademicSearch() throws IOException {
    ArrayList<String> actual = search.getSearch("tennis");
    ArrayList<String> expected = new ArrayList<String>();

    expected.add("https://en.wikipedia.org/wiki/Tennis");

    Assert.assertEquals(expected, actual);
  }
}
