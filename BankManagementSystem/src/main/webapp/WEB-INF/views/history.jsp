<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Transaction History - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="header.jspf" %>
<div class="container">
    <h1>Transaction History</h1>
    <p class="subtitle">Your latest 100 transactions across all accounts.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

    <div class="card">
        <table>
            <tr><th>Date</th><th>Account</th><th>Type</th><th>Details</th><th>Amount</th><th>Balance after</th></tr>
            <c:forEach var="t" items="${transactions}">
                <tr>
                    <td><fmt:formatDate value="${t.txnTime}" pattern="dd MMM yyyy, hh:mm a"/></td>
                    <td>${t.accountNumber}</td>
                    <td>${t.txnType}</td>
                    <td><c:out value="${t.description}"/></td>
                    <td class="${t.txnType == 'DEPOSIT' || t.txnType == 'TRANSFER_IN' ? 'credit' : 'debit'}">
                        &#8377; <fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                    </td>
                    <td>&#8377; <fmt:formatNumber value="${t.balanceAfter}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty transactions}"><tr><td colspan="6" class="empty">No transactions found.</td></tr></c:if>
        </table>
    </div>
</div>
</body>
</html>
