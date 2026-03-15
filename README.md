# Bank of Bihar

A web-based banking application for managing user accounts, transactions, and authentication.

## Tech Stack

- **Java (Servlets):** Backend logic using Jakarta Servlet API.
- **JSP:** For dynamic web content (index.jsp).
- **HTML/CSS:** User interface for login, registration, account operations, and dashboard.
- **JDBC (MySQL):** Database connectivity for user and account management.
- **Maven:** Project build and dependency management.
- **JUnit:** Testing framework.
- **Jakarta Servlet API:** For HTTP request handling.

## Features

- **User Registration:** New users can register with full name, email, and password.
- **User Login:** Secure login with email and password authentication.
- **Account Opening:** Users can open new bank accounts with initial deposit and security pin.
- **Account Operations:**
  - **Credit Money:** Add funds to account.
  - **Debit Money:** Withdraw funds from account.
  - **Transfer Money:** Transfer funds between accounts.
  - **Balance Inquiry:** Check current account balance.
- **Dashboard:** User-friendly dashboard for account operations.
- **Session Management:** Secure session handling for logged-in users.
- **Responsive UI:** Modern, styled HTML pages for all user actions.

## Project Structure

- `src/main/java/` — Java servlets for backend logic.
- `src/main/webapp/` — HTML, JSP, and CSS files for frontend.
- `pom.xml` — Maven configuration and dependencies.
- `WEB-INF/web.xml` — Servlet and welcome file configuration.

## Getting Started

1. **Clone the repository**
2. **Configure MySQL database** (update credentials in servlets if needed)
3. **Build with Maven**
4. **Deploy WAR file to a servlet container (e.g., Tomcat)**
5. **Access via browser**

## License

MIT License
