import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
@WebServlet("/LoginServlet")
public class LoginServlet  extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String dbPassword = "Vinit@123";
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            res.sendRedirect("register.html?status=fail");
            return;
        }

        // Step 1: Authenticate user
        String loginQuery = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url, username, dbPassword);
             PreparedStatement loginStmt = conn.prepareStatement(loginQuery)) {

            loginStmt.setString(1, email);
            loginStmt.setString(2, password);
            ResultSet rs = loginStmt.executeQuery();

            if (rs.next()) {
                // Step 2: Check if user has a bank account
                if (account_exist(email)) {
                    res.sendRedirect("loginOperation.html"); // User logged in and has account
                } else {
                    res.sendRedirect("LoginAccountOpen.html?status=success&email=" + URLEncoder.encode(email, "UTF-8"));
                }
            } else {
                res.sendRedirect("Login.html?status=fail"); // Login failed
            }

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendRedirect("Login.html?status=fail");
        }
    }

    public boolean account_exist(String email){
        String query = "select * from accounts where email = ?";
        try (Connection conn = DriverManager.getConnection(url, username, dbPassword);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}

