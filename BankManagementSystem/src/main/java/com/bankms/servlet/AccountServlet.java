package com.bankms.servlet;

import com.bankms.dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/accounts")
public class AccountServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        String type = request.getParameter("accountType");
        String openingValue = request.getParameter("openingBalance");

        if (!"Savings".equals(type) && !"Current".equals(type)) {
            request.setAttribute("error", "Select a valid account type.");
            show(request, response);
            return;
        }
        try {
            BigDecimal opening = new BigDecimal(openingValue == null || openingValue.isBlank() ? "0" : openingValue);
            BigDecimal minimum = "Savings".equals(type) ? new BigDecimal("500") : new BigDecimal("1000");
            if (opening.compareTo(minimum) < 0) {
                request.setAttribute("error", type + " accounts need a minimum opening deposit of " + minimum + ".");
                show(request, response);
                return;
            }
            accountDAO.openAccount(userId, type, opening.setScale(2));
            response.sendRedirect(request.getContextPath() + "/accounts?opened=1");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Enter a valid opening amount.");
            show(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to open the account.");
            show(request, response);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        try {
            request.setAttribute("accounts", accountDAO.getAccountsByUser(userId));
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to load accounts.");
        }
        request.getRequestDispatcher("/WEB-INF/views/accounts.jsp").forward(request, response);
    }
}
