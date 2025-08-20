# sookmyung-linkedin
숙명여대 학생 전용 프로젝트 매칭·네트워킹 서비스. 회원 승인 시스템, 자기소개·게시글 작성, 검색, 1:1 메시지, AI 기반 추천, 팀 일정 관리, 관리자 페이지 등 기능 구현.

## 🎉 최신 업데이트 (2025-08-20)

### ✅ **API 테스트 100% 성공 달성! (dev 환경)**
- **총 테스트**: 55개
- **성공률**: **100.0%** (55/55 성공)
- **검색 API 400 에러 완전 해결** (dev 환경)
- **회귀 방지 체계 완벽 구축**

### 🔧 **해결된 주요 문제들**
1. **검색 API 400 에러**: 한글 키워드 URL 인코딩 문제 완전 해결 (dev 환경)
2. **팀 스케줄 생성**: dev 환경에서 201 Created 반환 보장
3. **팀 수정 API**: 필드명 불일치 문제 해결 (dev 환경)
4. **Dev 환경 최적화**: 모든 API 안정적 작동

### 🛡️ **회귀 방지 시스템**
- 프론트엔드 API 가이드라인 문서화
- URL 인코딩 검증 스크립트
- Dev 전용 POST 대체 엔드포인트
- 테스트 스크립트 URL 인코딩 강제

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
- **dev**: 개발 환경 (포트: 8081, MySQL 연결, DevErrorAdvice 활성화)
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

## 🧪 API 테스트 및 검증

### 전체 API 테스트 실행
```bash
# 전체 API 테스트 실행 및 결과 요약
./scripts/run-test-and-summary.sh

# 개별 API 테스트
./scripts/full-api-test.sh
```

### 테스트 결과 (2025-08-20 기준, dev 환경)
- **총 테스트**: 55개
- **성공**: 55개 (100%)
- **실패**: 0개 (0%)
- **성공률**: **100.0%** 🎉

### URL 인코딩 검증
```bash
# URL 인코딩 패턴 검증
./scripts/check-url-encoding.sh
```

## 📚 개발 가이드

### 프론트엔드 개발자를 위한 API 가이드
- **[프론트엔드 API 호출 가이드라인](docs/frontend-api-guidelines.md)**: JavaScript에서 API 호출 시 주의사항
- **[검색 API URL 인코딩 가이드](docs/search-api-encoding-guide.md)**: 한글/특수문자 키워드 처리 방법

### ⚠️ **중요: 검색 API 사용 시 주의사항**
한글이나 특수문자가 포함된 검색 키워드는 **반드시 URL 인코딩**이 필요합니다:

```javascript
// ❌ 잘못된 방법 (400 에러 발생)
const url = `/api/search/posts?keyword=개발`;

// ✅ 올바른 방법
const url = `/api/search/posts?keyword=${encodeURIComponent('개발')}`;

// ✅ URLSearchParams 사용
const params = new URLSearchParams();
params.append('keyword', '개발 C++&ML');
const url = `/api/search/posts?${params.toString()}`;
```

### Dev 환경 전용 POST 대체 엔드포인트
개발 중 테스트 편의를 위해 다음 엔드포인트를 사용할 수 있습니다:

```bash
# 게시글 검색 (POST 방식)
curl -X POST http://localhost:8081/api/search/posts/_dev \
  -H "Content-Type: application/json" \
  -d '{"keyword":"개발 ML+C++ & 데이터","page":0,"size":20}'

# 사용자 검색 (POST 방식)
curl -X POST http://localhost:8081/api/search/users/_dev \
  -H "Content-Type: application/json" \
  -d '{"keyword":"테스터","page":0,"size":20}'
```

**⚠️ 주의**: 이 엔드포인트들은 `@Profile("dev")`로 dev 환경에서만 사용 가능합니다.

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

## 🎯 접속 정보

- **로컬 환경**: http://localhost:8080
- **개발 환경**: http://localhost:8081
- **API 문서**: http://localhost:8080/swagger-ui.html
- **Actuator Health**: http://localhost:8080/actuator/health

## 📋 프로젝트 구조

```
sookmyung-linkedin/
├── src/main/java/com/sookmyung/campus_match/
│   ├── controller/          # API 컨트롤러
│   ├── service/            # 비즈니스 로직
│   ├── repository/         # 데이터 접근 계층
│   ├── domain/             # 도메인 모델
│   ├── dto/                # 데이터 전송 객체
│   ├── config/             # 설정 클래스
│   └── exception/          # 예외 처리
├── scripts/                 # 테스트 및 유틸리티 스크립트
├── docs/                    # 개발 가이드 문서
└── docker-compose.yml      # Docker 설정
```

## 🚀 향후 계획

- [ ] E2E 테스트 자동화
- [ ] CI/CD 파이프라인 구축
- [ ] 성능 모니터링 도구 통합
- [ ] 보안 취약점 정기 점검

---

**마지막 업데이트**: 2025-08-20  
**API 테스트 상태**: ✅ 100% 성공 (55/55, dev 환경)  
**검색 API 상태**: ✅ 완벽 작동 (dev 환경)  
**Dev 환경 상태**: ✅ 최적화 완료
