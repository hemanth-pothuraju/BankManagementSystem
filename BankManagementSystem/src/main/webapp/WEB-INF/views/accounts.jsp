<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Accounts - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="header.jspf" %>
<div class="container">
    <h1>My Accounts</h1>
    <p class="subtitle">Open a new account or view existing ones.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <c:if test="${param.opened == '1'}"><div class="alert success">Account opened successfully.</div></c:if>

    <div class="card">
        <h3>Open a new account</h3>
        <form action="${pageContext.request.contextPath}/accounts" method="post">
            <label for="accountType">Account type</label>
            <select id="accountType" name="accountType" required>
                <option value="Savings">Savings (min &#8377;500)</option>
                <option value="Current">Current (min &#8377;1000)</option>
            </select>
            <label for="openingBalance">Opening deposit (&#8377;)</label>
            <input type="number" id="openingBalance" name="openingBalance" min="0" step="0.01" required>
            <button class="btn" type="submit">Open account</button>
        </form>
    </div>

    <div class="card">
        <h3>Account list</h3>
        <table>
            <tr><th>Account number</th><th>Type</th><th>Balance</th><th>Opened on</th></tr>
            <c:forEach var="a" items="${accounts}">
                <tr>
                    <td>${a.accountNumber}</td>
                    <td>${a.accountType}</td>
                    <td>&#8377; <fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatDate value="${a.createdAt}" pattern="dd MMM yyyy"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty accounts}"><tr><td colspan="4" class="empty">You have no accounts yet.</td></tr></c:if>
        </table>
    </div>
</div>
</body>
</html>
