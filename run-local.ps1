# PowerShell용 로컬 실행 스크립트
# Campus Match 애플리케이션을 local 프로필로 실행

Write-Host "🚀 Campus Match 애플리케이션을 local 프로필로 시작합니다..." -ForegroundColor Green

# JAVA_HOME 확인
if ($env:JAVA_HOME) {
    Write-Host "✅ JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  JAVA_HOME이 설정되지 않았습니다. 시스템 PATH에서 Java를 찾습니다." -ForegroundColor Yellow
}

# Java 버전 확인
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java 버전: $javaVersion" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Java를 찾을 수 없습니다. JAVA_HOME을 설정하거나 PATH에 Java를 추가하세요." -ForegroundColor Red
    exit 1
}

# Gradle Wrapper 실행 권한 확인
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "❌ gradlew.bat 파일을 찾을 수 없습니다." -ForegroundColor Red
    exit 1
}

Write-Host "📝 실행 명령어: .\gradlew.bat bootRunLocal" -ForegroundColor Yellow
Write-Host "⏳ 애플리케이션을 시작합니다..." -ForegroundColor Yellow

# 로컬 프로필로 애플리케이션 실행
.\gradlew.bat bootRunLocal

