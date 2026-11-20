package com.bankms.servlet;

import com.bankms.dao.AccountDAO;
import com.bankms.dao.TransactionDAO;
import com.bankms.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        try {
            List<Account> accounts = accountDAO.getAccountsByUser(userId);
            BigDecimal total = BigDecimal.ZERO;
            for (Account a : accounts) {
                total = total.add(a.getBalance());
            }
            request.setAttribute("accounts", accounts);
            request.setAttribute("totalBalance", total);
            request.setAttribute("recent", transactionDAO.getByUser(userId, 5));
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load dashboard.");
        }
    }
}
