@echo off

REM === Inputs ===
set PROJ_ROOT=.\
set APP_STARTER=FaceClassifier
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
"%JAVA_HOME%\bin\java" -version
echo.

echo Compiling...
"%JAVA_HOME%\bin\javac" ^
    -cp %LIBS_DIR%\*.jar ^
	-d "%BIN_DIR%" ^
	-sourcepath %SRC_DIR% ^
	"%SRC_DIR%\%APP_STARTER_DIR%\%APP_STARTER%.java"

if errorlevel 1 (
	echo compile:error
	exit /B 1
)

echo compile:success
echo.
REM ===

REM === Run ===
"%JAVA_HOME%\bin\java" -cp %LIBS_DIR%\*;"%BIN_DIR%" -Djava.library.path="%LIBS_DIR%" %APP_STARTER_PACKAGE%.%APP_STARTER%

if errorlevel 1 (
	echo runtime:error
	exit /B 1
)

echo runtime:success
REM ===