# API Request/Response 스키마 요약

## 개요
이 문서는 Campus Match API의 Request/Response 스키마와 검증 규칙을 정리한 문서입니다.

## Swagger UI 접속
- **URL**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 인증 관련 API

### 1. 회원가입 - POST /api/users/register

#### Request Body (UserRegisterRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| studentId | String | ✅ | 8-10자리 숫자 (`^[0-9]{8,10}$`) | `"20231431"` |
| department | String | ✅ | 최대 255자 | `"컴퓨터학부"` |
| name | String | ✅ | 최대 255자 | `"남민서"` |
| birthDate | LocalDate | ✅ | 과거 날짜, yyyy-MM-dd 형식 | `"2000-08-01"` |
| phoneNumber | String | ✅ | 010-XXXX-XXXX 형식 (`^01[0-9]-[0-9]{3,4}-[0-9]{4}$`) | `"010-8511-4975"` |
| email | String | ✅ | 이메일 형식, 최대 255자 | `"test@example.com"` |
| password | String | ✅ | 8-100자, 영문+숫자+특수문자 포함 (`^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$`) | `"Aa123456!"` |

#### Response (UserResponse)
| 필드명 | 타입 | 설명 |
|--------|------|------|
| id | Long | 사용자 ID |
| studentId | String | 학번 |
| department | String | 학과 |
| name | String | 이름 |
| birthDate | LocalDate | 생년월일 |
| phoneNumber | String | 전화번호 |
| email | String | 이메일 |
| approvalStatus | ApprovalStatus | 승인 상태 |
| isDeleted | Boolean | 삭제 여부 |
| lastLoginAt | LocalDateTime | 마지막 로그인 시간 |
| createdAt | LocalDateTime | 생성 시간 |
| updatedAt | LocalDateTime | 수정 시간 |

### 2. 로그인 - POST /api/users/login

#### Request Body (UserLoginRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| studentId | String | ✅ | `"20231431"` |
| password | String | ✅ | `"Aa123456!"` |

#### Response (TokenResponse)
| 필드명 | 타입 | 설명 |
|--------|------|------|
| accessToken | String | JWT 액세스 토큰 |
| tokenType | String | 토큰 타입 (Bearer) |
| expiresIn | Long | 만료 시간 (초) |

### 3. 비밀번호 확인 - POST /api/users/verify-password

#### Request Body (VerifyPasswordRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| password | String | ✅ | `"Aa123456!"` |

## 게시글 관련 API

### 4. 게시글 생성 - POST /api/posts

#### Request Body (PostCreateRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| category | PostCategory | ✅ | ENUM 값 | `"PROJECT"` |
| title | String | ✅ | 최대 255자 | `"웹 개발 프로젝트 팀원 모집"` |
| content | String | ❌ | 최대 10000자 | `"React와 Spring Boot를 사용한 웹 애플리케이션 개발 프로젝트입니다."` |
| requiredRoles | String | ❌ | 최대 255자 | `"프론트엔드 개발자, 백엔드 개발자"` |
| recruitmentCount | Integer | ❌ | 1-100 | `3` |
| duration | String | ❌ | 최대 255자 | `"3개월"` |
| linkUrl | String | ❌ | 최대 255자 | `"https://github.com/example/project"` |
| imageUrl | String | ❌ | 최대 255자 | `"https://example.com/image.jpg"` |

### 5. 게시글 수정 - PUT /api/posts/{postId}

#### Request Body (PostUpdateRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| category | PostCategory | ❌ | ENUM 값 | `"PROJECT"` |
| title | String | ❌ | 최대 255자 | `"웹 개발 프로젝트 팀원 모집"` |
| content | String | ❌ | 최대 10000자 | `"React와 Spring Boot를 사용한 웹 애플리케이션 개발 프로젝트입니다."` |
| requiredRoles | String | ❌ | 최대 255자 | `"프론트엔드 개발자, 백엔드 개발자"` |
| recruitmentCount | Integer | ❌ | 1-100 | `3` |
| duration | String | ❌ | 최대 255자 | `"3개월"` |
| linkUrl | String | ❌ | 최대 255자 | `"https://github.com/example/project"` |
| imageUrl | String | ❌ | 최대 255자 | `"https://example.com/image.jpg"` |

### 6. 게시글 지원 - POST /api/posts/{postId}/apply

#### Request Body (PostApplicationRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| message | String | ✅ | 10-1000자 | `"프론트엔드 개발 경험이 있어서 도움이 될 것 같습니다."` |

### 7. 댓글 생성 - POST /api/posts/{postId}/comments

#### Request Body (PostCommentCreateRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| content | String | ✅ | 최대 1000자 | `"프로젝트에 관심이 있습니다! 연락드려도 될까요?"` |

## 프로필 관련 API

### 8. 프로필 생성 - POST /api/profiles

#### Request Body (ProfileCreateRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| headline | String | ❌ | 최대 255자 | `"웹 개발자"` |
| bio | String | ❌ | 최대 100자 | `"React와 Spring Boot를 사용한 웹 개발을 하고 있습니다."` |
| profileImageUrl | String | ❌ | 최대 255자 | `"https://example.com/profile.jpg"` |
| location | String | ❌ | 최대 255자 | `"서울시 강남구"` |
| websiteUrl | String | ❌ | 최대 255자 | `"https://github.com/username"` |
| linkedinUrl | String | ❌ | 최대 255자 | `"https://linkedin.com/in/username"` |
| greetingEnabled | Boolean | ❌ | - | `true` |

### 9. 프로필 수정 - PUT /api/profiles/{profileId}

#### Request Body (ProfileUpdateRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| headline | String | ❌ | 최대 255자 | `"웹 개발자"` |
| bio | String | ❌ | 최대 100자 | `"React와 Spring Boot를 사용한 웹 개발을 하고 있습니다."` |
| profileImageUrl | String | ❌ | 최대 255자 | `"https://example.com/profile.jpg"` |
| location | String | ❌ | 최대 255자 | `"서울시 강남구"` |
| websiteUrl | String | ❌ | 최대 255자 | `"https://github.com/username"` |
| linkedinUrl | String | ❌ | 최대 255자 | `"https://linkedin.com/in/username"` |
| greetingEnabled | Boolean | ❌ | - | `true` |

## 메시지 관련 API

### 10. 메시지 전송 - POST /api/messages

#### Request Body (MessageSendRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| receiverId | Long | ✅ | - | `1` |
| title | String | ✅ | 1-100자 | `"프로젝트 협업 제안"` |
| content | String | ✅ | 1-1000자 | `"안녕하세요! 프로젝트에 함께 참여하실 분을 찾고 있습니다."` |

### 11. 메시지 답장 - POST /api/messages/{messageId}/reply

#### Request Body (MessageReplyRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| content | String | ✅ | 1-1000자 | `"네, 좋습니다! 언제 만나서 이야기해볼까요?"` |

### 12. 메시지 신고 - POST /api/messages/{messageId}/report

#### Request Body (MessageReportRequest)
| 필드명 | 타입 | 필수 | 검증 규칙 | 예시 |
|--------|------|------|-----------|------|
| reason | MessageReportReason | ✅ | ENUM 값 | `"SPAM"` |
| description | String | ❌ | 최대 500자 | `"스팸 메시지입니다."` |

## 관리자 관련 API

### 13. 회원 승인/반려 - POST /api/admin/users/{userId}/approve

#### Request Body (ApproveUserRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| approved | Boolean | ✅ | `true` |

### 14. 시스템 공지 등록 - POST /api/admin/notices

#### Request Body (SystemNoticeRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| title | String | ❌ | `"시스템 점검 안내"` |
| content | String | ❌ | `"8월 15일 00:00~06:00 서버 점검이 예정되어 있습니다."` |
| active | boolean | ❌ | `true` |

## 팀 관련 API

### 15. 팀 매칭 수락 - POST /api/teams/{teamId}/accept

#### Request Body (TeamAcceptRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| applicantId | Long | ✅ | `100` |

## 일정 관련 API

### 16. 팀 일정 생성 - POST /api/teams/{teamId}/schedules

#### Request Body (TeamScheduleRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| title | String | ✅ | `"팀 회의"` |
| description | String | ❌ | `"프로젝트 진행 상황 공유"` |
| startAt | LocalDateTime | ✅ | `"2025-08-15T10:00:00"` |
| endAt | LocalDateTime | ✅ | `"2025-08-15T12:00:00"` |
| startTime | LocalDateTime | ❌ | `"2025-08-15T10:00:00"` |
| endTime | LocalDateTime | ❌ | `"2025-08-15T12:00:00"` |
| allDay | boolean | ❌ | `false` |
| location | String | ❌ | `"중앙도서관 회의실 A"` |

### 17. 일정 업무 할당 - POST /api/schedules/{scheduleId}/assignments

#### Request Body (ScheduleAssignmentRequest)
| 필드명 | 타입 | 필수 | 예시 |
|--------|------|------|------|
| assigneeId | Long | ❌ | `5` |
| title | String | ❌ | `"디자인 시안 제작"` |
| description | String | ❌ | `"메인 페이지 UI/UX 시안 제작"` |
| status | AssignmentStatus | ❌ | `"TODO"` |
| dueAt | LocalDateTime | ❌ | `"2025-08-20T18:00:00"` |
| progressPct | Integer | ❌ | `0` |

## ENUM 값 목록

### PostCategory
- `PROJECT` - 프로젝트
- `STUDY` - 스터디
- `COMPETITION` - 공모전
- `HACKATHON` - 해커톤
- `INTERNSHIP` - 인턴십
- `VOLUNTEER` - 봉사활동
- `OTHER` - 기타

### MessageReportReason
- `SPAM` - 스팸
- `HARASSMENT` - 괴롭힘
- `INAPPROPRIATE_CONTENT` - 부적절한 내용
- `COMMERCIAL` - 상업적 목적
- `OTHER` - 기타

### AssignmentStatus
- `TODO` - 할 일
- `IN_PROGRESS` - 진행 중
- `DONE` - 완료
- `CANCELLED` - 취소됨

## 공통 규칙

### 날짜/시간 형식
- **LocalDate**: `yyyy-MM-dd` (예: `"2000-08-01"`)
- **LocalDateTime**: `yyyy-MM-ddTHH:mm:ss` (예: `"2025-01-27T10:30:00"`)

### 전화번호 형식
- **패턴**: `^01[0-9]-[0-9]{3,4}-[0-9]{4}$`
- **예시**: `"010-8511-4975"`

### 비밀번호 정책
- **길이**: 8-100자
- **패턴**: `^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$`
- **요구사항**: 영문, 숫자, 특수문자(!@#$%^&*) 포함
- **예시**: `"Aa123456!"`

### 페이지네이션 파라미터
- **page**: 페이지 번호 (0부터 시작, 기본값: 0)
- **size**: 페이지 크기 (기본값: 20)
- **sort**: 정렬 기준 (예: `"createdAt,desc"`)

### 응답 형식
모든 API 응답은 `ApiEnvelope<T>` 형태로 래핑됩니다:

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": { ... },
  "timestamp": "2025-01-27T10:30:00Z"
}
```

### HTTP 상태 코드
- **200**: 성공
- **201**: 생성 성공
- **204**: 성공 (응답 본문 없음)
- **400**: 잘못된 요청 (검증 실패)
- **401**: 인증 실패
- **403**: 권한 없음
- **404**: 리소스 없음
- **409**: 충돌 (중복 등)
- **500**: 서버 오류

## 유효/무효 예시

### 회원가입 - 유효한 예시
```json
{
  "studentId": "20231431",
  "department": "컴퓨터학부",
  "name": "남민서",
  "birthDate": "2000-08-01",
  "phoneNumber": "010-8511-4975",
  "email": "test@example.com",
  "password": "Aa123456!"
}
```

### 회원가입 - 무효한 예시
```json
{
  "studentId": "123",           // 8자리 미만
  "department": "컴퓨터학부",
  "name": "남민서",
  "birthDate": "2025-08-01",    // 미래 날짜
  "phoneNumber": "010-123-456", // 잘못된 형식
  "email": "invalid-email",     // 잘못된 이메일
  "password": "123"             // 너무 짧고 규칙 불일치
}
```

### 게시글 생성 - 유효한 예시
```json
{
  "category": "PROJECT",
  "title": "웹 개발 프로젝트 팀원 모집",
  "content": "React와 Spring Boot를 사용한 웹 애플리케이션 개발 프로젝트입니다.",
  "requiredRoles": "프론트엔드 개발자, 백엔드 개발자",
  "recruitmentCount": 3,
  "duration": "3개월",
  "linkUrl": "https://github.com/example/project"
}
```

## Swagger UI 사용 팁

1. **Try it out**: 각 API의 "Try it out" 버튼을 클릭하여 실제 요청을 보낼 수 있습니다.
2. **예시 값 활용**: Request Body의 예시 값들을 그대로 사용하여 빠르게 테스트할 수 있습니다.
3. **응답 확인**: 응답 코드와 응답 본문을 확인하여 API가 올바르게 작동하는지 검증할 수 있습니다.
