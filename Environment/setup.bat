setx -m JAVA_HOME %CD%\environment\jdk
setx -m ANDROID_HOME %CD%\environment\sdk
setx PATH "%APPDATA%\npm;%CD%\environment\sdk\platform-tools;%CD%\environment\sdk\tools;%CD%\environment\nodejs\"
pause