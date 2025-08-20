import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/AccountOpenServlet")
public class AccountOpenServlet extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String dbPassword = "Vinit@123";


    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String full_name = req.getParameter("fullName");
        double balance = Double.parseDouble(req.getParameter("initialAmount"));
        String security_pin = req.getParameter("pin");
        long account_number = generateAccountNumber();
        String email = req.getParameter("email");

        String open_account = "insert into accounts(account_number, full_name, email, balance, security_pin) values(?,?,?,?,?)";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            res.sendRedirect("AccountOpenServlet.html?status=fail");
            return;
        }
        try (Connection conn = DriverManager.getConnection(url, username, dbPassword);
             PreparedStatement preparedStatement = conn.prepareStatement(open_account)){
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, full_name);
            preparedStatement.setString(3, email);
            preparedStatement.setDouble(4, balance);
            preparedStatement.setString(5, security_pin);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                res.sendRedirect("LoginAccountOpen.html?status=success&account=" + account_number);
            } else {
                throw new RuntimeException("Account Creation failed!!");

            }


        } catch (SQLException e) {
            e.printStackTrace();
            res.sendRedirect("AccountOpenServlet.html?status=fail");
            return;
        }
    }
    private long generateAccountNumber(){
        try(Connection conn = DriverManager.getConnection(url, username, dbPassword);
            Statement statement = conn.createStatement()){

            ResultSet resultSet = statement.executeQuery("select account_number from accounts order by account_number desc limit 1");
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number+1;
            }else{
                return  10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }
}
