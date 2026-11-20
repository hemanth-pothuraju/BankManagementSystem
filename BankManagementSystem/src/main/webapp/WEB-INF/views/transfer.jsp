<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Fund Transfer - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="header.jspf" %>
<div class="container">
    <h1>Fund Transfer</h1>
    <p class="subtitle">Send money to another account in this bank.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <c:if test="${param.done == '1'}"><div class="alert success">Transfer completed successfully.</div></c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty accounts}">
                <p class="empty">Open an account first from the <a href="${pageContext.request.contextPath}/accounts">Accounts</a> page.</p>
            </c:when>
            <c:otherwise>
                <form action="${pageContext.request.contextPath}/transfer" method="post">
                    <label for="fromAccountId">From account</label>
                    <select id="fromAccountId" name="fromAccountId" required>
                        <c:forEach var="a" items="${accounts}">
                            <option value="${a.id}">${a.accountNumber} (${a.accountType}) - &#8377; <fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></option>
                        </c:forEach>
                    </select>
                    <label for="toAccount">To account number (12 digits)</label>
                    <input type="text" id="toAccount" name="toAccount" pattern="\d{12}" maxlength="12" required>
                    <label for="amount">Amount (&#8377;)</label>
                    <input type="number" id="amount" name="amount" min="0.01" step="0.01" required>
                    <button class="btn" type="submit">Transfer</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
