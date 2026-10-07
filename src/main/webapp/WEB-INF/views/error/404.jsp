<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - Page Not Found | LakshanMart</title>
    <style>
        body { font-family: sans-serif; text-align: center; padding: 50px; background-color: #f8fafc; color: #334155; }
        h1 { font-size: 48px; margin-bottom: 10px; color: #0284c7; }
        p { font-size: 18px; color: #64748b; }
        a { color: #0284c7; text-decoration: none; font-weight: bold; }
    </style>
</head>
<body>
    <h1>404</h1>
    <h2>Resource Not Found</h2>
    <p>The requested page or endpoint does not exist on LakshanMart.</p>
    <p><a href="${pageContext.request.contextPath}/">Return to Home</a></p>
</body>
</html>
