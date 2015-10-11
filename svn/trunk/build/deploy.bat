rem defining global variables
@echo off
echo "Deployment: defining variables..."
rem if not %1 == ""
rem set MENTA_HOME=%1%

rem if %1 == ""
set /v MENTA_HOME=c:\menta_opc

set /v CATALINA_HOME=%MENTA_HOME%\Tomcat
set /v JAVA_HOME=%MENTA_HOME%\jdk1.6.0_16
set /v MYSQL_HOME=%MENTA_HOME%\MySql

echo "Deployment: defining variables completed"

rem creating structure of folders
echo "Deployment: creating structure of folders..."
md %MENTA_HOME%
md %CATALINA_HOME%
md %JAVA_HOME%
md %MYSQL_HOME%
echo "Deployment: creating structure of folders completed"

rem copying files
echo "Deployment: copying new files..."
copy Tomcat %CATALINA_HOME%
copy jdk1.6.0_16 %JAVA_HOME%
copy MySQL %MYSQL_HOME%

copy start.bat %MENTA_HOME%
copy stop.bat %MENTA_HOME%
echo "Deployment: copying new files completed"

rem installing database
echo "Deployment: installing db..."

call %MYSQL_HOME%\mysql_start.bat

rem %MYSQL_HOME%\bin\mysqld.exe --bind-address=localhost -P 9999 -b %MYSQL_HOME% -h %MYSQL_HOME%\data --log-error=[logs] -u root --standalone --default-storage-engine=InnoDB

sleep 500

%MYSQL_HOME%\bin\mysql -u root -h localhost -P [port]  < menta_ddl.sql
%MYSQL_HOME%\bin\mysql -u root -h localhost -P [port] menta < menta_demo_data.sql

call %MYSQL_HOME%\mysql_stop.bat
echo "Deployment: installing db completed"

pause