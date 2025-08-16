import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/AccountServlet")
public class AccountServlet extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String dbPassword = "Vinit@123";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        long accountNumber = Long.parseLong(req.getParameter("accountNumber").trim());
        String pin = req.getParameter("securityPin");
        String amountStr = req.getParameter("amount");
        String receiverAccount = req.getParameter("recieverAccount");
        //String receiverStr = req.getParameter("receiverAccount");
        try (Connection conn = DriverManager.getConnection(url, username, dbPassword)) {
            switch (action) {
                case "credit":
                    creditMoney(conn, accountNumber, pin, amountStr, res);
                    break;
                case "debit":
                    debitMoney(conn, accountNumber, pin, amountStr, res);
                    break;
                case "transfer":
                    transferMoney(conn, accountNumber, pin, receiverAccount, amountStr, res);
                    break;

                case "balance":
                    showBalance(conn, accountNumber, pin, res);
                    break;
                default:
                    res.getWriter().println("Unknown action: " + action);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().println("Database error: " + e.getMessage());
        }
    }

        public void creditMoney(Connection conn, long acc, String pin, String amountStr, HttpServletResponse res) throws SQLException, IOException {
            double amount = Double.parseDouble(amountStr);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, acc);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
                update.setDouble(1, amount);
                update.setLong(2, acc);
                int rows = update.executeUpdate();
                res.getWriter().println(rows > 0 ? "✅ Rs. " + amount + " credited successfully." : "❌ Credit failed.");
            } else {
                res.getWriter().println("❌ Invalid security pin.");
            }
        }

        public void debitMoney(Connection conn, long acc, String pin, String amountStr, HttpServletResponse res) throws SQLException, IOException {
            double amount = Double.parseDouble(amountStr);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, acc);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (amount <= balance) {
                    PreparedStatement update = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
                    update.setDouble(1, amount);
                    update.setLong(2, acc);
                    int rows = update.executeUpdate();
                    res.getWriter().println(rows > 0 ? "✅ Rs. " + amount + " debited successfully." : "❌ Debit failed.");
                } else {
                    res.getWriter().println("❌ Insufficient balance.");
                }
            } else {
                res.getWriter().println("❌ Invalid security pin.");
            }
        }

    private void transferMoney(Connection conn, long sender, String pin, String receiverStr, String amountStr, HttpServletResponse res) throws SQLException, IOException {
        long receiver = Long.parseLong(receiverStr);
        double amount = Double.parseDouble(amountStr);

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
        ps.setLong(1, sender);
        ps.setString(2, pin);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double balance = rs.getDouble("balance");
            if (amount <= balance) {
                conn.setAutoCommit(false);

                PreparedStatement debit = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
                debit.setDouble(1, amount);
                debit.setLong(2, sender);

                PreparedStatement credit = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
                credit.setDouble(1, amount);
                credit.setLong(2, receiver);

                int d = debit.executeUpdate();
                int c = credit.executeUpdate();

                if (d > 0 && c > 0) {
                    conn.commit();
                    res.getWriter().println("✅ Rs. " + amount + " transferred successfully.");
                } else {
                    conn.rollback();
                    res.getWriter().println("❌ Transfer failed.");
                }

                conn.setAutoCommit(true);
            } else {
                res.getWriter().println("❌ Insufficient balance.");
            }
        } else {
            res.getWriter().println("❌ Invalid security pin.");
        }
    }


    public void showBalance(Connection conn, long acc, String pin, HttpServletResponse res) throws SQLException, IOException {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, acc);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                res.getWriter().println("💰 Current Balance: Rs. " + rs.getDouble("balance"));
            } else {
                res.getWriter().println("❌ Invalid security pin.");
            }
        }
}
