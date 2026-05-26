@echo off
if not exist "%~dp0Client\target\client.jar" (
    echo Chua co client.jar, dang build...

    where mvn >nul 2>&1
    if %errorlevel% neq 0 (
        echo.
        echo Maven chua duoc cai dat hoac chua them vao PATH.
        echo Cai Maven tai: https://maven.apache.org/download.cgi
        echo Sau do them thu muc bin vao PATH roi chay lai file nay.
        echo.
        pause >nul
        exit /b 1
    )

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
