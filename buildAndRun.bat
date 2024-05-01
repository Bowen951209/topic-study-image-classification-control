@echo off

REM === Inputs ===
set PROJ_ROOT=.\
set APP_STARTER=Webcam
set APP_STARTER_PACKAGE=net.bowen
REM ===

REM === Set local env ===
set SRC_DIR=%PROJ_ROOT%\src
set BIN_DIR=%PROJ_ROOT%\bin
set LIBS_DIR=%PROJ_ROOT%\libs
set APP_STARTER_DIR=%APP_STARTER_PACKAGE:.=\%
REM ===

REM === Clean and make bin dir ===
rd /s /q "%BIN_DIR%"
md "%BIN_DIR%"
REM ===

REM === Compile ===
java -version
echo.

echo Compiling...
javac ^
    -cp %LIBS_DIR%\opencv-480.jar;%LIBS_DIR%\jSerialComm-2.10.4.jar ^
	-d "%BIN_DIR%" ^
	-sourcepath %SRC_DIR% ^
	"%SRC_DIR%\%APP_STARTER_DIR%\%APP_STARTER%.java" ^
	-encoding utf8

if errorlevel 1 (
	echo compile:error
	exit /B 1
)

echo compile:success
echo.
REM ===

REM === Run ===
java -cp %LIBS_DIR%\*;"%BIN_DIR%" -Djava.library.path="%LIBS_DIR%" %APP_STARTER_PACKAGE%.%APP_STARTER%

if errorlevel 1 (
	echo runtime:error
	exit /B 1
)

echo runtime:success
REM ===
