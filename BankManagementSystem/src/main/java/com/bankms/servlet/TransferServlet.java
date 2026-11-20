package com.bankms.servlet;

import com.bankms.dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

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
        String toAccount = request.getParameter("toAccount");

        try {
            int fromId = Integer.parseInt(request.getParameter("fromAccountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            if (toAccount == null || !toAccount.trim().matches("\\d{12}")) {
                request.setAttribute("error", "Enter a valid 12-digit destination account number.");
            } else if (amount.signum() <= 0) {
                request.setAttribute("error", "Amount must be greater than zero.");
            } else {
                String error = accountDAO.transfer(userId, fromId, toAccount.trim(),
                        amount.setScale(2, RoundingMode.HALF_UP));
                if (error == null) {
                    response.sendRedirect(request.getContextPath() + "/transfer?done=1");
                    return;
                }
                request.setAttribute("error", error);
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("error", "Select an account and enter a valid amount.");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Transfer failed. No money was moved.");
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
        request.getRequestDispatcher("/WEB-INF/views/transfer.jsp").forward(request, response);
    }
}
