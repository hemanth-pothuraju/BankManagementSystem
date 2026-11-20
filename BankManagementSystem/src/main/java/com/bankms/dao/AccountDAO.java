package com.bankms.dao;

import com.bankms.model.Account;
import com.bankms.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AccountDAO {

    /** Opens a new account with an opening deposit and a unique 12-digit number. */
    public boolean openAccount(int userId, String type, BigDecimal openingBalance) throws SQLException {
        String accNo = generateAccountNumber();
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int accountId;
                String sql = "INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES (?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, accNo);
                    ps.setString(3, type);
                    ps.setBigDecimal(4, openingBalance);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        accountId = keys.getInt(1);
                    }
                }
                if (openingBalance.signum() > 0) {
                    insertTxn(con, accountId, "DEPOSIT", openingBalance, openingBalance, null, "Opening deposit");
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Account> getAccountsByUser(int userId) throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT id, user_id, account_number, account_type, balance, created_at "
                   + "FROM accounts WHERE user_id = ? ORDER BY id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** Deposit into an account owned by userId. */
    public String deposit(int userId, int accountId, BigDecimal amount) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal balance = lockBalance(con, accountId, userId);
                if (balance == null) {
                    con.rollback();
                    return "Account not found.";
                }
                BigDecimal newBalance = balance.add(amount);
                updateBalance(con, accountId, newBalance);
                insertTxn(con, accountId, "DEPOSIT", amount, newBalance, null, "Cash deposit");
                con.commit();
                return null;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Withdraw from an account owned by userId. Returns an error message or null on success. */
    public String withdraw(int userId, int accountId, BigDecimal amount) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal balance = lockBalance(con, accountId, userId);
                if (balance == null) {
                    con.rollback();
                    return "Account not found.";
                }
                if (balance.compareTo(amount) < 0) {
                    con.rollback();
                    return "Insufficient balance.";
                }
                BigDecimal newBalance = balance.subtract(amount);
                updateBalance(con, accountId, newBalance);
                insertTxn(con, accountId, "WITHDRAW", amount, newBalance, null, "Cash withdrawal");
                con.commit();
                return null;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Transfer between accounts as one atomic database transaction. */
    public String transfer(int userId, int fromAccountId, String toAccountNumber, BigDecimal amount)
            throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String fromNumber = accountNumberOf(con, fromAccountId, userId);
                if (fromNumber == null) {
                    con.rollback();
                    return "Source account not found.";
                }
                if (fromNumber.equals(toAccountNumber)) {
                    con.rollback();
                    return "Cannot transfer to the same account.";
                }
                int toId = -1;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id FROM accounts WHERE account_number = ?")) {
                    ps.setString(1, toAccountNumber);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) toId = rs.getInt(1);
                    }
                }
                if (toId == -1) {
                    con.rollback();
                    return "Destination account does not exist.";
                }

                // Lock rows in id order to avoid deadlocks
                BigDecimal fromBal;
                BigDecimal toBal;
                if (fromAccountId < toId) {
                    fromBal = lockBalance(con, fromAccountId, -1);
                    toBal = lockBalance(con, toId, -1);
                } else {
                    toBal = lockBalance(con, toId, -1);
                    fromBal = lockBalance(con, fromAccountId, -1);
                }
                if (fromBal.compareTo(amount) < 0) {
                    con.rollback();
                    return "Insufficient balance.";
                }
                BigDecimal newFrom = fromBal.subtract(amount);
                BigDecimal newTo = toBal.add(amount);
                updateBalance(con, fromAccountId, newFrom);
                updateBalance(con, toId, newTo);
                insertTxn(con, fromAccountId, "TRANSFER_OUT", amount, newFrom, toAccountNumber,
                        "Transfer to " + toAccountNumber);
                insertTxn(con, toId, "TRANSFER_IN", amount, newTo, fromNumber,
                        "Transfer from " + fromNumber);
                con.commit();
                return null;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // ---------- helpers ----------

    /** Locks the account row (FOR UPDATE). If userId > 0 the owner is also checked. */
    private BigDecimal lockBalance(Connection con, int accountId, int userId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE id = ?" + (userId > 0 ? " AND user_id = ?" : "")
                   + " FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            if (userId > 0) ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : null;
            }
        }
    }

    private String accountNumberOf(Connection con, int accountId, int userId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT account_number FROM accounts WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, accountId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private void updateBalance(Connection con, int accountId, BigDecimal newBalance) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?")) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private void insertTxn(Connection con, int accountId, String type, BigDecimal amount,
                           BigDecimal balanceAfter, String related, String description) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, txn_type, amount, balance_after, related_account, description) "
                   + "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setBigDecimal(3, amount);
            ps.setBigDecimal(4, balanceAfter);
            ps.setString(5, related);
            ps.setString(6, description);
            ps.executeUpdate();
        }
    }

    private String generateAccountNumber() {
        long n = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        return String.valueOf(n);
    }

    private Account map(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
