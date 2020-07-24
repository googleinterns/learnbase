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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.python.util.PythonInterpreter;
import org.python.core.*;

/** Servlet that returns HTML that contains the page view count. */
@WebServlet("/python")
public class PythonServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PyObject x;
    PythonInterpreter pyInterp = new PythonInterpreter();
    pyInterp.exec("x = 10 + 10");
    x = pyInterp.get("x");
    response.setContentType("text/html;");
    response.getWriter().println(x); 

    // PythonInterpreter pyInterp = new PythonInterpreter();
    // pyInterp.execfile("test.py");
    // PyObject x = pyInterp.get("x");
    // System.out.println(x);

    // response.setContentType("text/html;");

  }
}