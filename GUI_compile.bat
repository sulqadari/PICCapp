echo off
set JAVA_HOME=C:\openJDK\jdk-14
set PATH=%JAVA_HOME%\bin;%PATH%
echo Display Java Version
java -version

chcp 1251
rem javac -sourcepath ./src -d ./bin src/AudioConfigMain.java
javac --module-path %PATH_TO_FX% --add-modules javafx.controls ./src/gui/TroikaAppMain.java -sourcepath ./src/gui;./src -d ./bin/gui -Xlint:unchecked
pause