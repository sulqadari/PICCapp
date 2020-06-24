echo off
chcp 1251
javac -sourcepath ./src/gui;./src -d ./bin/gui ./src/gui/TroikaAppMain.java
pause