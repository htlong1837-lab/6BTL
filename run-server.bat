@echo off
java -jar "%~dp0sever\target\server.jar"
if %errorlevel% neq 0 (
    echo.
    echo Loi khi chay server.jar. Bam phim bat ky de dong...
    pause >nul
)
