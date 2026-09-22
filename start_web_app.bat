@echo off
title SmartRemote Pro - Web App & PC Remote
color 0b

echo ================================================================
echo           SmartRemote Pro - Universal Remote Web App
echo ================================================================
echo.

:: Check Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python is not installed or not in PATH!
    echo Please install Python 3.8+ from python.org and add to PATH.
    pause
    exit /b 1
)

echo [INFO] Starting SmartRemote Pro Web Server & PC Remote Bridge...
echo [INFO] Opening Web App in your browser...
echo.

start http://localhost:8080
python "%~dp0server\web_remote_server.py"

pause
