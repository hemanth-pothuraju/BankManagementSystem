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

/** Handles deposit and withdrawal requests. */
@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

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
        String action = request.getParameter("action");

        try {
            int accountId = Integer.parseInt(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            if (amount.signum() <= 0) {
                request.setAttribute("error", "Amount must be greater than zero.");
                show(request, response);
                return;
            }
            amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);

            String error;
            if ("deposit".equals(action)) {
                error = accountDAO.deposit(userId, accountId, amount);
            } else if ("withdraw".equals(action)) {
                error = accountDAO.withdraw(userId, accountId, amount);
            } else {
                error = "Invalid action.";
            }

            if (error == null) {
                response.sendRedirect(request.getContextPath() + "/transaction?done=" + action);
                return;
            }
            request.setAttribute("error", error);
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("error", "Select an account and enter a valid amount.");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Transaction failed. Please try again.");
        }
        show(request, response);
    }

    private void show(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        try {
            request.setAttribute("accounts", accountDAO.getAccountsByUser(userId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/views/transaction.jsp").forward(request, response);
    }
}
