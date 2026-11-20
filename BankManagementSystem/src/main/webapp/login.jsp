<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <h2>Welcome back</h2>
        <p class="sub">Sign in to your bank account</p>

        <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
        <c:if test="${param.registered == '1'}"><div class="alert success">Registration successful. Please log in.</div></c:if>
        <c:if test="${param.loggedout == '1'}"><div class="alert success">You have been logged out.</div></c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="email">E-mail</label>
            <input type="email" id="email" name="email" required>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
            <button class="btn" type="submit">Login</button>
        </form>
        <div class="alt">New customer? <a href="${pageContext.request.contextPath}/register.jsp">Create an account</a></div>
    </div>
</div>
</body>
</html>
