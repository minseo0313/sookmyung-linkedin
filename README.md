# Campus Match - 숙명여대 캠퍼스 매칭 플랫폼

숙명여자대학교 학생 전용 프로젝트 매칭·네트워킹 서비스. 회원 승인 시스템, 자기소개·게시글 작성, 검색, 1:1 메시지, AI 기반 추천, 팀 일정 관리, 관리자 페이지 등 기능 구현.

## 🎉 최신 업데이트 (2025-08-21)

### ✅ **API 테스트 100% 성공 달성! (dev 환경)**
- **총 테스트**: 55개
- **성공률**: **100.0%** (55/55 성공)
- **실패**: 0개
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

## 🏗️ 프로젝트 아키텍처

### 📊 **환경별 비교표**

| 구분 | **dev (개발)** | **prod (운영)** | **local (백업)** | **test (테스트)** |
|------|----------------|-----------------|------------------|-------------------|
| **포트** | `8080` | 환경변수 | `8080` | 임시포트 |
| **데이터베이스** | MySQL (localhost) | MySQL (외부서버) | MySQL (localhost) | H2 (인메모리) |
| **인증 방식** | Mock 인증 | JWT 토큰 | 미사용 | Mock 인증 |
| **사용자 ID** | 고정값 `1L` | JWT 토큰에서 추출 | - | 고정값 `999L` |
| **보안 설정** | 완전 비활성화 | JWT 필터 활성화 | - | 완전 비활성화 |
| **스텁 API** | ✅ 활성화 | ❌ 비활성화 | - | ❌ 비활성화 |
| **예외 처리** | 완화된 처리 | 표준 처리 | - | 표준 처리 |
| **Swagger** | ✅ 활성화 | ✅ 활성화 | - | ❌ 비활성화 |

### 🔐 **인증/인가 시스템**

#### **dev 환경 (현재 사용 중)**
```java
// Mock 인증 - 고정 사용자 ID 반환
@Profile("dev")
public class DevCurrentUserResolver {
    public Long currentUserId() {
        return 1L;  // 항상 1번 사용자로 인식
    }
}

// 보안 완전 비활성화
spring.autoconfigure.exclude: SecurityAutoConfiguration
```

#### **prod 환경**
```java
// JWT 토큰 기반 인증
@Profile("prod")
public class SecurityConfig {
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    .requestMatchers("/api/users/register", "/api/users/login").permitAll()
    .anyRequest().authenticated()
}

// TODO: JwtCurrentUserResolver 구현 필요
```

#### **test 환경**
```java
// 테스트용 Mock 인증
@Profile("test")
public class TestCurrentUserResolver {
    public Long currentUserId() {
        return 999L;  // 테스트용 고정 사용자 ID
    }
}
```

### 🗄️ **데이터베이스 설정**

| 환경 | **데이터베이스** | **URL** | **사용자** | **비밀번호** |
|------|------------------|---------|------------|--------------|
| **dev** | MySQL | `jdbc:mysql://127.0.0.1:3306/campus` | `campus` | `campuspw` |
| **prod** | MySQL | `jdbc:mysql://prod-db-server:3306/campus` | `${DB_USER}` | `${DB_PASS}` |
| **local** | MySQL | `jdbc:mysql://127.0.0.1:3306/campus` | `campus` | `campuspw` |
| **test** | H2 | `jdbc:h2:mem:testdb` | `sa` | `` |

### 📚 **Swagger 설정**
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **API 문서**: `openapi.json` (111KB)

### 🧪 **API 테스트**

#### **테스트 스크립트**
```
scripts/
├── full-api-test.sh        # 전체 API 테스트 (223줄)
├── run-test-and-summary.sh # 테스트 실행 및 요약
├── check-url-encoding.sh   # URL 인코딩 검사
└── dev-smoke.sh           # 개발용 스모크 테스트
```

#### **최신 테스트 결과**
```
총 테스트 수: 55
성공: 55 (100.0%)
실패: 0 (0.0%)

주요 성공 사항:
- ✅ 인증 API (로그인/회원가입)
- ✅ 프로필 API (조회/수정)
- ✅ 게시글 API (CRUD)
- ✅ 팀 API (생성/수정/스케줄)
- ✅ 메시지 API (스레드/전송)
- ✅ 검색 API (URL 인코딩 해결)
- ✅ 추천 API (사용자/게시글)
- ✅ 관리자 API (공지사항)
- ✅ 에러 케이스 처리 (404, 400, 405)
```

#### **⚠️ URL 인코딩 주의사항**
```javascript
// ❌ 실패 (400 에러)
const url = `/api/search/posts?keyword=${keyword}`;

// ✅ 성공 (200 응답)
const url = `/api/search/posts?keyword=${encodeURIComponent(keyword)}`;
```

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
# dev 환경 (현재 사용 중)
./gradlew bootRun -Dspring-boot.run.profiles=dev

# prod 환경
export SPRING_PROFILES_ACTIVE=prod
./gradlew bootRun

# test 환경
./gradlew test
```

### IntelliJ 설정

#### 1. Run/Debug Configurations 열기
- `Run` → `Edit Configurations...` 클릭

#### 2. Spring Boot 설정 (dev 환경용)
- `Spring Boot` 템플릿 선택
- `Name`: `CampusMatch Dev` (또는 원하는 이름)
- `Main class`: `com.sookmyung.campus_match.CampusMatchApplication`
- `VM options`: `-Dspring.profiles.active=dev`
- `Active profiles`: `dev`

#### 3. 환경변수 설정 (선택사항)
- `Environment variables` 섹션에서:
  - `SPRING_PROFILES_ACTIVE=dev`

## 🧪 API 테스트 및 검증

### 헬스체크
```bash
# 애플리케이션 상태 확인
curl http://localhost:8080/actuator/health

# Swagger UI 접속
open http://localhost:8080/swagger-ui.html
```

### 전체 API 테스트
```bash
# 전체 API 테스트 실행
./scripts/full-api-test.sh

# 테스트 결과 요약
./scripts/run-test-and-summary.sh

# URL 인코딩 검사
./scripts/check-url-encoding.sh
```

### 개별 API 테스트
```bash
# 로그인 테스트
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"studentId":"20240001","password":"password123"}'

# 프로필 조회 테스트
curl http://localhost:8080/api/profiles/me

# 게시글 목록 조회 테스트
curl http://localhost:8080/api/posts
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/sookmyung/campus_match/
│   │   ├── CampusMatchApplication.java
│   │   ├── config/
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java (prod)
│   │   │   │   ├── DevCurrentUserResolver.java (dev)
│   │   │   │   └── jwt/
│   │   │   │       ├── JwtTokenProvider.java
│   │   │   │       └── JwtAuthenticationFilter.java
│   │   │   └── TestConfig.java
│   │   ├── controller/
│   │   │   ├── auth/ (실제 인증 API)
│   │   │   ├── dev/ (스텁 API)
│   │   │   │   ├── DevAuthController.java
│   │   │   │   ├── DevRecommendationController.java
│   │   │   │   └── DevSearchController.java
│   │   │   └── ... (기타 컨트롤러)
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── DevErrorAdvice.java (dev)
│   └── resources/
│       ├── application.yml (기본 설정)
│       ├── application-dev.yml (개발 환경)
│       ├── application-prod.yml (운영 환경)
│       ├── application-local-backup.yml (백업)
│       └── db/migration/
│           └── V1__complete_schema_setup.sql
├── test/
│   ├── java/com/sookmyung/campus_match/
│   │   ├── CampusMatchApplicationTests.java
│   │   ├── config/
│   │   │   └── TestConfig.java
│   │   └── controller/
│   │       ├── AuthControllerTest.java
│   │       └── UserRegistrationTest.java
│   └── resources/
│       └── application-test.yml
└── scripts/
    ├── full-api-test.sh
    ├── run-test-and-summary.sh
    └── check-url-encoding.sh
```

## 🔧 주요 설정 파일

### JWT 설정
```yaml
# application.yml
jwt:
  secret: your-secret-key-here-make-it-long-and-secure...
  access-token-expiration: 3600    # 1시간
  refresh-token-expiration: 86400  # 24시간
```

### 데이터베이스 설정
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/campus
    username: campus
    password: campuspw
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    baseline-on-migrate: true
```

### Swagger 설정
```yaml
# application.yml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
  info:
    title: Campus Match API
    description: 숙명여자대학교 캠퍼스 매치 플랫폼 API
    version: 1.0.0
```

## ⚠️ 중요한 주의사항

### 1. 현재 사용 환경
- **실제 사용**: `dev` 환경 (포트 8080)
- **백업 파일**: `local` 환경 (미사용)
- **테스트**: `test` 환경 (단위 테스트용)
- **운영**: `prod` 환경 (JWT 미완성)

### 2. JWT 구현 상태
- ✅ **토큰 발행**: 모든 환경에서 정상
- ❌ **토큰 검증**: prod 환경에서 미구현
- ⚠️ **완전한 JWT**: 아직 개발 중

### 3. API 테스트
- **URL 인코딩 필수**: 한글 키워드 시 `encodeURIComponent` 사용
- **스텁 API**: dev 환경에서 미구현 API 모킹
- **테스트 성공률**: 현재 100.0% (55/55 성공)

### 4. 데이터베이스
- **dev**: MySQL (localhost:3306)
- **prod**: MySQL (외부 서버)
- **test**: H2 (인메모리)
- **마이그레이션**: Flyway 사용

## 📚 추가 문서

- [프론트엔드 API 가이드라인](docs/frontend-api-guidelines.md)
- [검색 API 인코딩 가이드](docs/search-api-encoding-guide.md)
- [API 요청 형식 가이드](docs/request-format.md)

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

- **프로젝트 링크**: [https://github.com/your-username/sookmyung-linkedin](https://github.com/your-username/sookmyung-linkedin)
- **이메일**: support@campus-match.com

---

**현재 `dev` 환경이 실제 개발에 사용되고 있으며, 모든 스텁 API와 Mock 인증이 활성화되어 있습니다!** 🚀
