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

public class MessageListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MessageListServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequeset(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequeset(request, response);
	}

	private void processRequeset(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter writer = response.getWriter();
		
		String sender = request.getParameter("Sender");
		String receiver = request.getParameter("Receiver");
		
		response.setContentType("text/xml");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM Message WHERE (SenderId = '" + sender + "' AND Receiver = '" + receiver + "') OR (SenderId = '" + receiver + "' AND Receiver = '" + sender + "')";
			ResultSet set = statement.executeQuery(query);
			
			writer.print("<Messages>");
			
			while (set.next()) {
				writer.print("<Message>");
				writer.print("<MessageId>" + set.getInt("MessageId") + "</MessageId>");
				writer.print("<Contents>" + set.getString("Contents") + "</Contents>");
				writer.print("<SenderId>" + set.getString("SenderId") + "</SenderId>");
				writer.print("<Receiver>" + set.getString("Receiver") + "</Receiver>");
				writer.print("</Message>");
			}
			
			writer.print("</Messages>");
			
			set.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
