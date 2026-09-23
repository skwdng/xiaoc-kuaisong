@echo off
REM ============================================================
REM   小c快送 开机自动启动脚本（Redis 缓存 + 后端服务）
REM   把本文件的快捷方式放到"开机自启动"文件夹即可开机自动运行
REM ============================================================

REM ---- 1. 启动 Redis 缓存服务（最小化窗口）----
start "Redis" /min cmd /c "cd /d E:\software\Redis-x64-3.2.100 && redis-server.exe redis.windows.conf"

REM ---- 2. 等 5 秒让 Redis 就绪 ----
ping -n 6 127.0.0.1 >nul

REM ---- 3. 启动后端服务（最小化窗口，日志写入 backend-live.log）----
cd /d E:\CExpress\sky-take-out
start "SkyBackend" /min cmd /c ""D:\env\jdk\bin\java.exe" -jar "E:\CExpress\sky-take-out\sky-server\target\sky-server-1.0-SNAPSHOT.jar" > E:\CExpress\sky-take-out\backend-live.log 2>&1"

exit
