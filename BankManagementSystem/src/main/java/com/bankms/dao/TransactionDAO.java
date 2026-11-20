package com.bankms.dao;

import com.bankms.model.Transaction;
import com.bankms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /** Latest transactions across all accounts of a user (newest first). */
    public List<Transaction> getByUser(int userId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.account_id, a.account_number, t.txn_type, t.amount, t.balance_after, "
                   + "t.related_account, t.description, t.txn_time "
                   + "FROM transactions t JOIN accounts a ON a.id = t.account_id "
                   + "WHERE a.user_id = ? ORDER BY t.txn_time DESC, t.id DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setAccountId(rs.getInt("account_id"));
                    t.setAccountNumber(rs.getString("account_number"));
                    t.setTxnType(rs.getString("txn_type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    t.setRelatedAccount(rs.getString("related_account"));
                    t.setDescription(rs.getString("description"));
                    t.setTxnTime(rs.getTimestamp("txn_time"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}
