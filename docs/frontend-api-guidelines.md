# 프론트엔드 API 호출 가이드라인

## 🚨 중요: URL 인코딩 필수

### 검색 API 호출 시 주의사항

**❌ 잘못된 방법 (400 에러 발생)**
```javascript
// 한글 키워드를 직접 URL에 포함하면 Tomcat에서 400 에러 발생
const url = `/api/search/posts?keyword=${keyword}`; // keyword가 "개발"이면 실패
```

**✅ 올바른 방법 (200 성공)**
```javascript
// encodeURIComponent를 사용하여 URL 인코딩 필수
const url = `/api/search/posts?keyword=${encodeURIComponent(keyword)}`;
```

### 구체적인 예시

```javascript
// 검색 함수 예시
function searchPosts(keyword) {
    const encodedKeyword = encodeURIComponent(keyword);
    const url = `/api/search/posts?keyword=${encodedKeyword}`;
    
    return fetch(url)
        .then(response => response.json())
        .catch(error => console.error('검색 실패:', error));
}

// 사용 예시
searchPosts('개발'); // "개발" → "%EA%B0%9C%EB%B0%9C"로 자동 인코딩
searchPosts('테스터'); // "테스터" → "%ED%85%8C%EC%8A%A4%ED%84%B0"로 자동 인코딩
```

### 다른 HTTP 클라이언트에서의 사용법

#### Axios
```javascript
import axios from 'axios';

const searchPosts = async (keyword) => {
    const params = new URLSearchParams();
    params.append('keyword', keyword); // 자동으로 인코딩됨
    
    const response = await axios.get('/api/search/posts', { params });
    return response.data;
};
```

#### Fetch API
```javascript
const searchPosts = async (keyword) => {
    const params = new URLSearchParams();
    params.append('keyword', keyword);
    
    const response = await fetch(`/api/search/posts?${params}`);
    return response.json();
};
```

#### React Query
```javascript
import { useQuery } from '@tanstack/react-query';

const useSearchPosts = (keyword) => {
    return useQuery({
        queryKey: ['search', 'posts', keyword],
        queryFn: () => searchPosts(keyword),
        enabled: !!keyword,
    });
};
```

## 🔍 왜 URL 인코딩이 필요한가?

### Tomcat 레벨에서의 제한
- Spring Boot의 내장 Tomcat은 RFC 7230/3986 표준을 엄격하게 준수
- 한글과 같은 비ASCII 문자가 URL에 포함되면 `Invalid character found in the request target` 에러 발생
- 이는 애플리케이션 레벨이 아닌 **Tomcat 레벨**에서 발생하는 문제

### 서버 설정으로는 해결 불가
- `server.tomcat.relaxed-query-chars`: 특수문자만 허용 (한글 문제 해결 안됨)
- `server.tomcat.uri-encoding=UTF-8`: 디코딩 방식 설정 (미인코딩 입력 자체는 거부)

### 결론
**클라이언트에서 URL 인코딩을 보장하는 것이 유일한 해결책**

## 📝 테스트 시 주의사항

### cURL 테스트
```bash
# ❌ 실패 (400 에러)
curl "http://localhost:8080/api/search/posts?keyword=개발"

# ✅ 성공 (200 응답)
curl "http://localhost:8080/api/search/posts?keyword=%EA%B0%9C%EB%B0%9C"

# ✅ 더 안전한 방법
curl -G --data-urlencode "keyword=개발" "http://localhost:8080/api/search/posts"
```

### Postman/Insomnia
- Params 탭에서 키워드 입력 시 자동으로 인코딩됨
- 직접 URL에 한글 입력 시 400 에러 발생 주의

## 🛡️ 회귀 방지

### 코드 리뷰 체크리스트
- [ ] 검색 API 호출 시 `encodeURIComponent` 사용
- [ ] URL에 직접 한글 문자열 포함 금지
- [ ] 테스트 코드에서도 URL 인코딩 적용
- [ ] 특수문자(`+`, `&`, `/`, `?`, `%`, ` `) 포함 키워드 테스트 케이스 포함
- [ ] 공통 유틸리티 함수 사용 권장

### 🚨 절대 금지 패턴
```javascript
// ❌ 절대 하지 말 것
const url = `/api/search/posts?keyword=${keyword}`;
const url = `/api/search/posts?keyword=개발`;
const url = `/api/search/posts?keyword=C++ & Python`;

// ❌ 템플릿 리터럴에 직접 한글/특수문자
const searchUrl = `${BASE_URL}/search/posts?keyword=${userInput}`;
```

### ✅ 권장 공통 유틸리티
```javascript
// 공통 유틸리티 함수
export const buildSearchUrl = (baseUrl, params) => {
    const url = new URL(baseUrl);
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

### 자동화 도구
```javascript
// ESLint 규칙 예시 (필요시)
// "no-direct-url-encoding": "error"

// package.json scripts에 추가
{
  "scripts": {
    "lint:search": "grep -r '\\?keyword=' src/ && echo 'Direct keyword URL found!' && exit 1 || echo 'URL encoding check passed'"
  }
}
```

## 📚 참고 자료

- [MDN - encodeURIComponent()](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent)
- [RFC 3986 - URI Generic Syntax](https://tools.ietf.org/html/rfc3986)
- [RFC 7230 - HTTP/1.1 Message Syntax and Routing](https://tools.ietf.org/html/rfc7230)
