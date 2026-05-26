@echo off
if not exist "%~dp0Client\target\client.jar" (
    echo Chua co client.jar, dang build...
    cd /d "%~dp0Client"
    call mvn package -DskipTests -q
    if %errorlevel% neq 0 (
        echo.
        echo Build that bai. Bam phim bat ky de dong...
        pause >nul
        exit /b 1
    )
    echo Build thanh cong!
)
java -jar "%~dp0Client\target\client.jar"
if %errorlevel% neq 0 (
    echo.
    echo Loi khi chay client.jar. Bam phim bat ky de dong...
    pause >nul
)
