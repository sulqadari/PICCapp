echo off
set JAVA_HOME=C:\openJDK\jdk-14
set PATH=%JAVA_HOME%\bin;%PATH%
echo Display Java Version
java -version

chcp 1251
rem javac -sourcepath ./src -d ./bin src/AudioConfigMain.java
java --module-path %PATH_TO_FX% --add-modules javafx.controls -cp ./bin/gui TroikaAppMain
pause