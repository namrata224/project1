package com.messenger;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import static com.messenger.utility.Constants.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public LoginServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		
		response.setContentType("text/xml");
		
		String loginName = request.getParameter("LoginName");
		String password = request.getParameter("Password");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM User WHERE LoginName = '" + loginName + "' AND Password = '" + password + "'";
			//writer.println(query);
			ResultSet set = statement.executeQuery(query);
			
			writer.print("<result>");
			if (set.next()) {
				writer.print("<status>0</status>");
				writer.print("<userId>" + set.getInt("UserId") + "</userId>");
				writer.print("<fullName>" + set.getString("FullName") + "</fullName>");
			} else {
				writer.print("<status>-1</status>");
			}
			writer.print("</result>");
			
			set.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
