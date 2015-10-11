
echo "Deployment: defining variables..."

rem set /v MENTA_HOME=

set CATALINA_HOME=C:\Menta\Tomcat
set JAVA_HOME=jdk1.6.0_16
set MYSQL_HOME=MySQL

echo "Start MySQL"
%MYSQL_HOME%\bin\mysqld.exe --bind-address=localhost -P 10061 -b %MYSQL_HOME% -h data\ --log-error=MySQL.log -u root --standalone --default-storage-engine=InnoDB

echo "Start Tomcat"
%CATALINA_HOME%\bin\startup.bat
