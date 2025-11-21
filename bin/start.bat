@echo off

set APP_HOME=%~dp0..
set APP_HOME=%APP_HOME:~0,-1%

set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED -Dapp.home="%APP_HOME%" -Dwebapp.dir="%APP_HOME%.\webapp" -Dfile.encoding=UTF-8

set CP="%APP_HOME%.\classes;%APP_HOME%.\lib\*"

echo CP : %CP%

echo APP_HOME : %APP_HOME%
echo JAVA_OPTS : %JAVA_OPTS%

set JAVA_EXE="%APP_HOME%.\sw\openjdk-23.0.2\bin\java.exe"
echo JAVA_EXE : %JAVA_EXE%

start "haru" %JAVA_EXE% %JAVA_OPTS% -cp %CP% haru.core.bootstrap.MiniServletContainer
echo.  
