package com.messenger;

import static com.messenger.utility.Constants.CONNECTION_STRING;
import static com.messenger.utility.Constants.PASSWORD;
import static com.messenger.utility.Constants.USER_NAME;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FriendsListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public FriendsListServlet() {
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
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM User";
			ResultSet set = statement.executeQuery(query);
			
			writer.print("<Users>");
			
			while (set.next()) {
				writer.print("<User>");
				writer.print("<UserId>" + set.getInt("UserId") + "</UserId>");
				writer.print("<FullName>" + set.getString("FullName") + "</FullName>");
				writer.print("<Mobile>" + set.getString("Mobile") + "</Mobile>");
				writer.print("</User>");
			}
			
			writer.print("</Users>");
			
			set.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
