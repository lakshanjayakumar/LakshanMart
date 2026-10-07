@echo off
title LakshanMart Public Tunnel
echo ==========================================================
echo    LakshanMart - Public Online URL Tunnel Launcher
echo ==========================================================
echo.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tunnel.ps1" %*
pause
