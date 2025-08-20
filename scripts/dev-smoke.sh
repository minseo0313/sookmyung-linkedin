#!/bin/bash

# Dev 환경 스모크 테스트 스크립트
# WHY: dev 환경에서 모든 API가 정상 작동하는지 확인

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo "🚀 Dev 환경 스모크 테스트 시작"
echo "=================================="

# 1. Actuator Health Check - 200 예상
echo "1. Actuator Health Check"
response=$(curl -s -w "%{http_code}" "$BASE_URL/actuator/health")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 2. 내 프로필 조회 (기본 사용자) - 200 예상
echo "2. 내 프로필 조회 (기본 사용자)"
response=$(curl -s -w "%{http_code}" "$API_BASE/profiles/me")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 3. 내 프로필 조회 (커스텀 사용자) - 200 예상
echo "3. 내 프로필 조회 (커스텀 사용자)"
response=$(curl -s -w "%{http_code}" -H "X-Mock-User: alice" "$API_BASE/profiles/me")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 4. 팀 목록 조회 - 200 예상
echo "4. 팀 목록 조회"
response=$(curl -s -w "%{http_code}" "$API_BASE/teams")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 5. 게시글 목록 조회 - 200 예상
echo "5. 게시글 목록 조회"
response=$(curl -s -w "%{http_code}" "$API_BASE/posts")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 6. 게시글 댓글 조회 - 200 예상
echo "6. 게시글 댓글 조회"
response=$(curl -s -w "%{http_code}" "$API_BASE/posts/1/comments")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 7. 검색 API (키워드 미포함) - 200 예상
echo "7. 검색 API (키워드 미포함)"
response=$(curl -s -w "%{http_code}" "$API_BASE/search/posts")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 8. 카테고리별 게시글 조회 (올바른 카테고리) - 200 예상
echo "8. 카테고리별 게시글 조회 (올바른 카테고리)"
response=$(curl -s -w "%{http_code}" "$API_BASE/posts/categories/PROJECT")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 9. 카테고리별 게시글 조회 (잘못된 카테고리) - 400 예상
echo "9. 카테고리별 게시글 조회 (잘못된 카테고리)"
response=$(curl -s -w "%{http_code}" "$API_BASE/posts/categories/UNKNOWN")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 10. 게시글 좋아요 - 200 예상
echo "10. 게시글 좋아요"
response=$(curl -s -w "%{http_code}" -X POST "$API_BASE/posts/1/like")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

# 11. 사용자 목록 조회 (관리자) - 200 예상
echo "11. 사용자 목록 조회 (관리자)"
response=$(curl -s -w "%{http_code}" "$API_BASE/admin/users")
http_code="${response: -3}"
body="${response%???}"
echo "   HTTP Code: $http_code"
echo "   Response: $body"
echo ""

echo "=================================="
echo "✅ Dev 환경 스모크 테스트 완료"
echo ""
echo "📊 결과 요약:"
echo "- 200 응답: 정상 작동"
echo "- 400 응답: 예상된 오류 (잘못된 파라미터 등)"
echo "- 500 응답: 서버 오류 (개발 필요)"
echo ""
echo "🔧 문제가 있는 API는 로그를 확인하여 수정하세요."

