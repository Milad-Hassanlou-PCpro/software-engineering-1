@echo off
setlocal enabledelayedexpansion
set "DIR=%~dp0"
set "OUT=%DIR%out"
set "LIBS=%DIR%lib"

set "JAVAC=javac"
set "JAVA=java"
where javac >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-1.8\bin\javac.exe" (
        set "JAVAC=C:\Program Files\Java\jdk-1.8\bin\javac.exe"
        set "JAVA=C:\Program Files\Java\jdk-1.8\bin\java.exe"
    )
)

if not exist "%OUT%" mkdir "%OUT%"

set "SOURCES="
for %%f in ("%DIR%*.java") do set "SOURCES=!SOURCES! "%%f""

echo Compiling...
"%JAVAC%" -encoding UTF-8 -d "%OUT%"!SOURCES!
if errorlevel 1 (
    echo Compile failed.
    pause
    exit /b 1
)

"%JAVA%" -cp "%OUT%;%LIBS%\sqlite-jdbc-3.45.1.0.jar;%LIBS%\slf4j-api-1.7.36.jar;%LIBS%\slf4j-nop-1.7.36.jar" ap.exercises.finalproject_ap.LibrarySystem
pause
