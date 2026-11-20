package com.bankms.servlet;

import com.bankms.dao.UserDAO;
import com.bankms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));

        if (name.isEmpty() || email.isEmpty() || password == null || password.length() < 6) {
            fail(request, response, "Name, e-mail and a password of at least 6 characters are required.");
            return;
        }
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            fail(request, response, "Phone number must contain 10 digits.");
            return;
        }

        User user = new User();
        user.setFullName(name);
        user.setEmail(email.toLowerCase());
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setPhone(phone);
        user.setAddress(address);

        try {
            if (userDAO.register(user)) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");
            } else {
                fail(request, response, "This e-mail is already registered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail(request, response, "Registration failed. Please try again later.");
        }
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
