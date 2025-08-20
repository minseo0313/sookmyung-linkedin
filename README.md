# sookmyung-linkedin
숙명여대 학생 전용 프로젝트 매칭·네트워킹 서비스. 회원 승인 시스템, 자기소개·게시글 작성, 검색, 1:1 메시지, AI 기반 추천, 팀 일정 관리, 관리자 페이지 등 기능 구현.

## 🚀 애플리케이션 실행 방법

### 사전 요구사항
- Java 21 (JDK 21)
- Docker Desktop (MySQL 8.0 컨테이너용)
- Gradle 8.0+

### JAVA_HOME 설정 (권장)
```bash
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Windows CMD
set JAVA_HOME=C:\Program Files\Java\jdk-21

# 시스템 환경변수에 영구 설정 (권장)
```

### 🐳 Docker MySQL 설정

#### 1. MySQL 컨테이너 실행
```bash
# 프로젝트 루트 디렉토리에서
docker-compose up -d mysql

# 컨테이너 상태 확인
docker ps

# MySQL 로그 확인 (ready for connections 메시지 확인)
docker logs -f campus-mysql
```

#### 2. 데이터베이스 연결 확인
```bash
# MySQL 직접 접속 테스트
mysql -h 127.0.0.1 -P 3306 -u campus -p
# 비밀번호: campuspw

# 데이터베이스 확인
SHOW DATABASES;
USE campus;
SHOW TABLES;
```

### 실행 방법

#### 1. PowerShell에서 실행 (권장)
```powershell
# 방법 1: PowerShell 스크립트 사용
.\run-local.ps1

# 방법 2: 직접 실행
.\gradlew.bat bootRunLocal

# 방법 3: 기본 bootRun (local 프로필 자동 적용)
.\gradlew.bat bootRun
```

#### 2. CMD에서 실행
```cmd
# 방법 1: 배치 파일 사용
run-local.bat

# 방법 2: 직접 실행
gradlew.bat bootRunLocal

# 방법 3: 기본 bootRun (local 프로필 자동 적용)
gradlew.bat bootRun
```

#### 3. 프로필별 실행
```bash
# 로컬 프로필 (포트: 8080)
.\gradlew.bat bootRunLocal

# 개발 프로필 (포트: 8081)
.\gradlew.bat bootRunDev

# 기본 실행 (local 프로필 자동 적용)
.\gradlew.bat bootRun
```

### 프로필 설정
- **local**: 로컬 개발 환경 (포트: 8080, MySQL 연결)
- **dev**: 개발 환경 (포트: 8081, MySQL 연결)
- **prod**: 운영 환경
- **test**: 테스트 환경

### 🔧 IntelliJ Run Configuration 설정

#### 1. Run/Debug Configurations 열기
- `Run` → `Edit Configurations...` 클릭

#### 2. Spring Boot 설정
- `Spring Boot` 템플릿 선택
- `Name`: `CampusMatch Local` (또는 원하는 이름)
- `Main class`: `com.sookmyung.campus_match.CampusMatchApplication`
- `VM options`: `-Dspring.profiles.active=local`
- `Active profiles`: `local`

#### 3. 환경변수 설정 (선택사항)
- `Environment variables` 섹션에서:
  - `SPRING_PROFILES_ACTIVE=local`

### 문제 해결

#### PowerShell에서 프로필 옵션 오류 발생 시
```powershell
# ❌ 잘못된 방법 (PowerShell에서 오류 발생)
.\gradlew.bat bootRun -Dspring-boot.run.profiles=local

# ✅ 올바른 방법
.\gradlew.bat bootRunLocal
```

#### JDK 버전 충돌 경고 해결
1. `JAVA_HOME` 환경변수 설정
2. 시스템 PATH에서 올바른 Java 버전 우선순위 설정
3. `build.gradle`의 `toolchain` 설정 확인

### 접속 정보
- **로컬 환경**: http://localhost:8080
- **개발 환경**: http://localhost:8081
- **API 문서**: http://localhost:8080/swagger-ui.html

## 📚 개발 가이드

### 프론트엔드 개발자를 위한 API 가이드
- **[프론트엔드 API 호출 가이드라인](docs/frontend-api-guidelines.md)**: JavaScript에서 API 호출 시 주의사항
- **[검색 API URL 인코딩 가이드](docs/search-api-encoding-guide.md)**: 한글/특수문자 키워드 처리 방법 ⚠️

### 중요: 검색 API 사용 시 주의사항
한글이나 특수문자가 포함된 검색 키워드는 **반드시 URL 인코딩**이 필요합니다:

```javascript
// ❌ 잘못된 방법 (400 에러 발생)
const url = `/api/search/posts?keyword=개발`;

// ✅ 올바른 방법
const url = `/api/search/posts?keyword=${encodeURIComponent('개발')}`;
```

자세한 내용은 [검색 API URL 인코딩 가이드](docs/search-api-encoding-guide.md)를 참조하세요.

## 🔍 연결 문제 해결 체크리스트

### 1. 포트 연결 확인
```powershell
# Windows PowerShell에서 포트 열림 확인
Test-NetConnection 127.0.0.1 -Port 3306

# 또는 CMD에서
telnet 127.0.0.1 3306
```

### 2. Docker 컨테이너 상태 확인
```bash
# 컨테이너 실행 상태 확인
docker ps

# MySQL 컨테이너 로그 확인
docker logs -f campus-mysql

# 로그에 "ready for connections" 메시지가 보여야 함
```

### 3. 데이터베이스 직접 접속 테스트
```bash
# MySQL 클라이언트로 직접 연결
mysql -h 127.0.0.1 -P 3306 -u campus -p
# 비밀번호: campuspw

# 연결 후 실행할 명령어
SHOW DATABASES;
USE campus;
SHOW TABLES;
```

### 4. 포트 충돌 문제 해결

#### Windows 로컬 MySQL 서비스와 충돌 시
```bash
# 포트 사용 중인 프로세스 확인
netstat -ano | findstr 3306

# 충돌이 발생하면 docker-compose.yml 수정
# ports: "13306:3306"으로 변경
```

#### 대체 포트 사용 시
1. `docker-compose.yml`에서 `ports: "13306:3306"`으로 변경
2. `application-local.yml`과 `application-dev.yml`에서 URL을 `127.0.0.1:13306`으로 수정
3. 컨테이너 재시작: `docker-compose down && docker-compose up -d`

### 5. 애플리케이션 로그 확인
```bash
# Spring Boot 애플리케이션 실행 후 로그에서 다음 확인:
# - "HikariPool-1 - Starting..." 메시지
# - "Started CampusMatchApplication" 메시지
# - 데이터베이스 연결 관련 오류 메시지 없음
```

### 6. 설정 파일 점검
- `application-local.yml`과 `application-dev.yml`에서:
  - URL이 `127.0.0.1:3306` (또는 대체 포트)인지 확인
  - `allowPublicKeyRetrieval=true`와 `useSSL=false` 옵션이 포함되어 있는지 확인
  - Hikari 설정이 올바른지 확인

### 7. Docker 볼륨 확인
```bash
# 마운트된 볼륨 확인
docker inspect campus-mysql

# Mounts 섹션에서 Source가 프로젝트 루트의 mysql-data로 설정되어 있는지 확인
```
