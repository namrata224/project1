package com.messenger;

import java.io.IOException;
import java.sql.*;

import static com.messenger.utility.Constants.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SignupServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		String fullName = request.getParameter("FullName");
		String loginName = request.getParameter("LoginName");
		String password = request.getParameter("Password");
		String mobile = request.getParameter("Mobile");
		
		if ((fullName == null) || (fullName.length() == 0)) {
			return;
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
			String query = "INSERT INTO User (LoginName, FullName, Password, Mobile) VALUES ('" + loginName + "','" + fullName + "','" + password + "','" + mobile + "')";
			
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
