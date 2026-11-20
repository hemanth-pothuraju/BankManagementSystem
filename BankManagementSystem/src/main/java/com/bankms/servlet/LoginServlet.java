package com.bankms.servlet;

import com.bankms.dao.UserDAO;
import com.bankms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            fail(request, response, "E-mail and password are required.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase());
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                HttpSession old = request.getSession(false);
                if (old != null) old.invalidate();          // prevents session fixation
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getFullName());
                session.setMaxInactiveInterval(15 * 60);    // 15 minute timeout
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                fail(request, response, "Invalid e-mail or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail(request, response, "Unable to log in right now. Please try again later.");
        }
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
