<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Deposit / Withdraw - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="header.jspf" %>
<div class="container">
    <h1>Deposit / Withdraw</h1>
    <p class="subtitle">Add money to or take money out of your account.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <c:if test="${param.done == 'deposit'}"><div class="alert success">Deposit completed successfully.</div></c:if>
    <c:if test="${param.done == 'withdraw'}"><div class="alert success">Withdrawal completed successfully.</div></c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty accounts}">
                <p class="empty">Open an account first from the <a href="${pageContext.request.contextPath}/accounts">Accounts</a> page.</p>
            </c:when>
            <c:otherwise>
                <form action="${pageContext.request.contextPath}/transaction" method="post">
                    <label for="accountId">Account</label>
                    <select id="accountId" name="accountId" required>
                        <c:forEach var="a" items="${accounts}">
                            <option value="${a.id}">${a.accountNumber} (${a.accountType}) - &#8377; <fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></option>
                        </c:forEach>
                    </select>
                    <label for="amount">Amount (&#8377;)</label>
                    <input type="number" id="amount" name="amount" min="0.01" step="0.01" required>
                    <button class="btn green" type="submit" name="action" value="deposit">Deposit</button>
                    <button class="btn red" type="submit" name="action" value="withdraw">Withdraw</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
