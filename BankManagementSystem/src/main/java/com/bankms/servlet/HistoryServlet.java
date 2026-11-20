package com.bankms.servlet;

import com.bankms.dao.TransactionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        try {
            request.setAttribute("transactions", transactionDAO.getByUser(userId, 100));
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to load transaction history.");
        }
        request.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(request, response);
    }
}
