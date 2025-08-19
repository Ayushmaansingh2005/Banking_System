import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {


    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String password = "Vinit@123";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
         //Step 1: Load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            res.sendRedirect("register.html?status=fail");
            return;
        }

        // Step 2: Fetch form data
        String fullName = req.getParameter("fullname");
        String email = req.getParameter("email");
        String userPassword = req.getParameter("password");

        // Step 3: Check if user already exists
        if (userExists(email)) {
            res.sendRedirect("register.html?status=fail");
            return;
        }

        // Step 4: Insert new user into database
        String registerQuery = "INSERT INTO user(full_name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(registerQuery)) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, userPassword);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                res.sendRedirect("register.html?status=success");
            } else {
                res.sendRedirect("register.html?status=fail");
            }

            conn.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendRedirect("register.html?status=fail");
        }
    }

    // Helper method to check for existing user
    private boolean userExists(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
