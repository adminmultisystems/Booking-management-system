# Script to set JAVA_HOME for current session
# Run this before running Maven commands

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
Write-Host "JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "You can now run: mvn spring-boot:run" -ForegroundColor Green

