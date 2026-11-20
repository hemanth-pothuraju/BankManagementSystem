<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="header.jspf" %>
<div class="container">
    <h1>Welcome, <c:out value="${sessionScope.userName}"/></h1>
    <p class="subtitle">Here is a summary of your accounts.</p>

    <div class="grid">
        <div class="card stat">
            <div class="label">Total balance</div>
            <div class="value">&#8377; <fmt:formatNumber value="${totalBalance}" minFractionDigits="2" maxFractionDigits="2"/></div>
        </div>
        <div class="card stat">
            <div class="label">Accounts</div>
            <div class="value">${accounts.size()}</div>
        </div>
    </div>

    <div class="card">
        <h3>Your accounts</h3>
        <table>
            <tr><th>Account number</th><th>Type</th><th>Balance</th></tr>
            <c:forEach var="a" items="${accounts}">
                <tr>
                    <td>${a.accountNumber}</td>
                    <td>${a.accountType}</td>
                    <td>&#8377; <fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty accounts}">
                <tr><td colspan="3" class="empty">No accounts yet. <a href="${pageContext.request.contextPath}/accounts">Open one</a>.</td></tr>
            </c:if>
        </table>
    </div>

    <div class="card">
        <h3>Recent transactions</h3>
        <table>
            <tr><th>Date</th><th>Account</th><th>Type</th><th>Amount</th></tr>
            <c:forEach var="t" items="${recent}">
                <tr>
                    <td><fmt:formatDate value="${t.txnTime}" pattern="dd MMM yyyy, hh:mm a"/></td>
                    <td>${t.accountNumber}</td>
                    <td>${t.txnType}</td>
                    <td class="${t.txnType == 'DEPOSIT' || t.txnType == 'TRANSFER_IN' ? 'credit' : 'debit'}">
                        &#8377; <fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty recent}"><tr><td colspan="4" class="empty">No transactions yet.</td></tr></c:if>
        </table>
    </div>
</div>
</body>
</html>
