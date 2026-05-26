@echo off
java -jar "%~dp0Client\target\client.jar"
if %errorlevel% neq 0 (
    echo.
    echo Loi khi chay client.jar. Bam phim bat ky de dong...
    pause >nul
)
