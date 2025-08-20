# 검색 API URL 인코딩 가이드

## 🚨 문제 상황

### Tomcat 레벨에서의 제한사항
Spring Boot의 내장 Tomcat은 RFC 7230/3986 표준을 엄격하게 준수합니다. 한글과 같은 비ASCII 문자나 특수문자가 URL에 인코딩되지 않은 채로 포함되면 **서버 애플리케이션에 도달하기 전**에 Tomcat에서 다음 에러를 발생시킵니다:

```
Invalid character found in the request target [/api/search/posts?keyword=개발]. 
The valid characters are defined in RFC 7230 and RFC 3986
```

### 왜 서버에서 해결할 수 없는가?

1. **Tomcat 레벨 차단**: 요청이 Spring 애플리케이션에 도달하기 전에 Tomcat에서 거부
2. **서버 설정의 한계**:
   - `server.tomcat.relaxed-query-chars`: 특수문자만 허용 (한글 문제 해결 안됨)
   - `server.tomcat.uri-encoding=UTF-8`: 디코딩 방식 설정 (미인코딩 입력 자체는 거부)
3. **애플리케이션 레벨 처리 불가**: `@ControllerAdvice`, Filter, Interceptor 등으로 해결 불가

### 결론
**클라이언트에서 URL 인코딩을 보장하는 것이 유일한 해결책**

## ✅ 해결 방법

### 1. 프론트엔드 (JavaScript)

#### 기본 원칙
```javascript
// ❌ 절대 금지
const url = `/api/search/posts?keyword=${keyword}`;

// ✅ 반드시 인코딩
const url = `/api/search/posts?keyword=${encodeURIComponent(keyword)}`;
```

#### 권장 유틸리티 함수
```javascript
export const buildSearchUrl = (baseUrl, params) => {
    const url = new URL(baseUrl, window.location.origin);
    Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
            url.searchParams.append(key, value);
        }
    });
    return url.toString();
};

// 사용 예시
const searchUrl = buildSearchUrl('/api/search/posts', {
    keyword: '개발 C++ & ML',
    page: 0,
    size: 20
});
```

### 2. cURL 테스트

#### 안전한 방법
```bash
# ✅ 권장: --data-urlencode 사용
curl -G --data-urlencode "keyword=개발 C++/Python & ML" \
     --data-urlencode "page=0" \
     --data-urlencode "size=20" \
     "http://localhost:8080/api/search/posts"

# ✅ 수동 인코딩
curl "http://localhost:8080/api/search/posts?keyword=%EA%B0%9C%EB%B0%9C%20C%2B%2B%26ML"
```

#### 위험한 방법
```bash
# ❌ 절대 금지 - 400 에러 발생
curl "http://localhost:8080/api/search/posts?keyword=개발"
curl "http://localhost:8080/api/search/posts?keyword=C++ & Python"
```

### 3. Python

```python
import urllib.parse
import requests

keyword = "개발 C++ & ML"
encoded_keyword = urllib.parse.quote(keyword)
url = f"http://localhost:8080/api/search/posts?keyword={encoded_keyword}"

# 또는 requests가 자동 처리
params = {"keyword": keyword, "page": 0, "size": 20}
response = requests.get("http://localhost:8080/api/search/posts", params=params)
```

### 4. Dev 환경 전용 POST 대체 (테스트 편의)

운영에 영향을 주지 않고 테스트할 수 있는 대체 엔드포인트:

```bash
# dev 환경에서만 사용 가능
curl -X POST -H "Content-Type: application/json" \
     -d '{"keyword":"개발 C++ & ML","page":0,"size":20}' \
     "http://localhost:8080/api/search/posts/_dev"

curl -X POST -H "Content-Type: application/json" \
     -d '{"keyword":"테스터 & QA","page":0,"size":20}' \
     "http://localhost:8080/api/search/users/_dev"
```

**주의**: 이 엔드포인트는 `@Profile("dev")`로 개발 환경에서만 노출됩니다.

## 🧪 테스트 케이스

### 성공해야 하는 케이스
```bash
# 한글
curl -G --data-urlencode "keyword=개발" "http://localhost:8080/api/search/posts"

# 영문 + 특수문자
curl -G --data-urlencode "keyword=C++ & Python" "http://localhost:8080/api/search/posts"

# 공백 포함
curl -G --data-urlencode "keyword=머신 러닝" "http://localhost:8080/api/search/posts"

# 복합 특수문자
curl -G --data-urlencode "keyword=개발 C++/Python & ML + 데이터" "http://localhost:8080/api/search/posts"
```

### 실패하는 케이스 (금지)
```bash
# ❌ 모두 400 에러 발생
curl "http://localhost:8080/api/search/posts?keyword=개발"
curl "http://localhost:8080/api/search/posts?keyword=C++ & Python"
curl "http://localhost:8080/api/search/posts?keyword=머신 러닝"
```

## 🛡️ 회귀 방지

### CI/CD 체크
```bash
# 스크립트에서 미인코딩 패턴 검사
grep -R '\?keyword=[^%].*[ㄱ-ㅎ가-힣]' scripts/ && \
    echo "❌ Unencoded Korean keyword found" && exit 1 || \
    echo "✅ URL encoding check passed"
```

### 코드 리뷰 체크리스트
- [ ] 검색 API 호출 시 `encodeURIComponent` 또는 `URLSearchParams` 사용
- [ ] URL에 직접 한글 문자열 포함 금지
- [ ] 특수문자 포함 키워드 테스트 케이스 포함
- [ ] 공통 유틸리티 함수 사용
- [ ] cURL 테스트 시 `--data-urlencode` 사용

### 정적 분석 도구
```json
// package.json
{
  "scripts": {
    "lint:search": "grep -r '\\?keyword=' src/ && echo 'Direct keyword URL found!' && exit 1 || echo 'URL encoding check passed'"
  }
}
```

## 📊 인코딩 참조표

| 문자 | URL 인코딩 | 설명 |
|------|------------|------|
| 개발 | %EA%B0%9C%EB%B0%9C | 한글 |
| C++ | C%2B%2B | + 기호 |
| & | %26 | 앰퍼샌드 |
| 공백 | %20 | 스페이스 |
| / | %2F | 슬래시 |
| ? | %3F | 물음표 |
| = | %3D | 등호 |

## 🔗 참고 자료

- [RFC 3986 - URI Generic Syntax](https://tools.ietf.org/html/rfc3986)
- [RFC 7230 - HTTP/1.1 Message Syntax](https://tools.ietf.org/html/rfc7230)
- [MDN - encodeURIComponent()](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent)
- [Tomcat URI Encoding](https://tomcat.apache.org/tomcat-10.1-doc/config/http.html)
