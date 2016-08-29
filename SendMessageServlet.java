package com.messenger;

import static com.messenger.utility.Constants.CONNECTION_STRING;
import static com.messenger.utility.Constants.PASSWORD;
import static com.messenger.utility.Constants.USER_NAME;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendMessageServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		String contents = request.getParameter("Contents");
		String sender = request.getParameter("Sender");
		String receiver = request.getParameter("Receiver");
		
		if ((contents == null) || (contents.length() == 0)) {
			return;
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
			String query = "INSERT INTO Message (Contents, SenderId, Receiver) VALUES ('" + contents + "','" + sender + "','" + receiver+ "')";
			
			Statement st = con.createStatement();
			st.execute(query);
			
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
