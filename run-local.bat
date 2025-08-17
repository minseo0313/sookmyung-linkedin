@echo off
REM Windows 배치 파일용 로컬 실행 스크립트
REM Campus Match 애플리케이션을 local 프로필로 실행

echo 🚀 Campus Match 애플리케이션을 local 프로필로 시작합니다...

REM JAVA_HOME 확인
if defined JAVA_HOME (
    echo ✅ JAVA_HOME: %JAVA_HOME%
) else (
    echo ⚠️  JAVA_HOME이 설정되지 않았습니다. 시스템 PATH에서 Java를 찾습니다.
)

REM Java 버전 확인
java -version >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ Java가 PATH에서 발견되었습니다.
) else (
    echo ❌ Java를 찾을 수 없습니다. JAVA_HOME을 설정하거나 PATH에 Java를 추가하세요.
    pause
    exit /b 1
)

REM Gradle Wrapper 실행 권한 확인
if not exist "gradlew.bat" (
    echo ❌ gradlew.bat 파일을 찾을 수 없습니다.
    pause
    exit /b 1
)

echo 📝 실행 명령어: gradlew.bat bootRunLocal
echo ⏳ 애플리케이션을 시작합니다...

REM 로컬 프로필로 애플리케이션 실행
gradlew.bat bootRunLocal

pause

