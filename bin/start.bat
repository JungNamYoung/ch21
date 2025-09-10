@echo off
setlocal enabledelayedexpansion

set APP_HOME=%~dp0..
set APP_HOME=%APP_HOME:~0,-1%

set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED -Dapp.home="%APP_HOME%" -Dwebapp.dir="%APP_HOME%.\webapp" -Dfile.encoding=UTF-8

rem set PORT=%1
rem if "%PORT%"=="" set PORT=8580

set CP="%APP_HOME%.\classes;%APP_HOME%.\lib\*"

echo CP : %CP%

echo APP_HOME : %APP_HOME%
echo JAVA_OPTS : %JAVA_OPTS%
rem echo PORT : %PORT%

set JAVA_EXE="%APP_HOME%.\sw\openjdk-23.0.2\bin\java.exe"
echo JAVA_EXE : %JAVA_EXE%

rem echo Starting REPORT on port %PORT% ...
rem %JAVA_EXE% %JAVA_OPTS% -Dhttp.port=%PORT% -cp %CP% haru.kitten.MiniServletContainer
%JAVA_EXE% %JAVA_OPTS% -cp %CP% haru.kitten.MiniServletContainer
endlocal