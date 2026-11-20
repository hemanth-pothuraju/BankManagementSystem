<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register - Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <h2>Create your profile</h2>
        <p class="sub">Register as a new bank customer</p>

        <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <label for="fullName">Full name</label>
            <input type="text" id="fullName" name="fullName" required>
            <label for="email">E-mail</label>
            <input type="email" id="email" name="email" required>
            <label for="password">Password (min 6 characters)</label>
            <input type="password" id="password" name="password" minlength="6" required>
            <label for="phone">Phone (10 digits)</label>
            <input type="text" id="phone" name="phone" pattern="\d{10}" maxlength="10">
            <label for="address">Address</label>
            <input type="text" id="address" name="address">
            <button class="btn" type="submit">Register</button>
        </form>
        <div class="alt">Already registered? <a href="${pageContext.request.contextPath}/login.jsp">Login</a></div>
    </div>
</div>
</body>
</html>
