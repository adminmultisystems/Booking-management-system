# Script to set JAVA_HOME to JDK 21 for current session
# Run this before running Maven commands

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "✅ JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "`n📋 Java Version:" -ForegroundColor Cyan
java -version

Write-Host "`n📋 Maven Version:" -ForegroundColor Cyan
mvn -v

Write-Host "`n✅ You can now run: mvn spring-boot:run" -ForegroundColor Green
