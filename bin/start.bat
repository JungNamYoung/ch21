@echo off
setlocal enabledelayedexpansion

set APP_HOME=%~dp0..
set APP_HOME=%APP_HOME:~0,-1%

set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED -Dapp.home="%APP_HOME%" -Dwebapp.dir="%APP_HOME%.\webapp" -Dfile.encoding=UTF-8

set PORT=%1
if "%PORT%"=="" set PORT=8580

set CP="%APP_HOME%.\classes;%APP_HOME%.\lib\*"

echo CP : %CP%

echo APP_HOME : %APP_HOME%
echo JAVA_OPTS : %JAVA_OPTS%
echo PORT : %PORT%

echo Starting REPORT on port %PORT% ...
"C:\start-webcontainer\SW\openjdk-23.0.2\bin\java.exe" %JAVA_OPTS% -Dhttp.port=%PORT% -cp %CP% haru.kitten.MiniServletContainer
endlocal