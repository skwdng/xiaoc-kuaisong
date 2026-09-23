@echo off
chcp 65001 >nul
title XiaoC-Express Backend (DO NOT CLOSE THIS WINDOW)
cd /d E:\CExpress\sky-take-out
echo ============================================
echo   Backend starting, please wait...
echo   "Started SkyApplication" = success
echo   Keep this window OPEN, or mini-program
echo   and admin site will stop working.
echo ============================================
"D:\env\jdk\bin\java.exe" -jar "E:\CExpress\sky-take-out\sky-server\target\sky-server-1.0-SNAPSHOT.jar"
pause
