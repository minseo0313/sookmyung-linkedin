#!/bin/bash

# URL 인코딩 체크 스크립트
# WHY: 검색 API에서 한글 키워드 미인코딩으로 인한 400 에러 방지

echo "🔍 URL 인코딩 체크 시작..."
echo "===================================="

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 에러 카운터
ERROR_COUNT=0

echo ""
echo "1. 스크립트 파일에서 미인코딩 한글 키워드 검사..."

# 한글이 포함된 미인코딩 패턴 검사 (자기 자신 제외)
UNENCODED_KOREAN=$(grep -r '\?keyword=[^%]*[ㄱ-ㅎ가-힣]' scripts/ 2>/dev/null | grep -v check-url-encoding.sh)
if [ ! -z "$UNENCODED_KOREAN" ]; then
    echo -e "${RED}❌ 스크립트에서 미인코딩 한글 키워드 발견:${NC}"
    echo "$UNENCODED_KOREAN"
    ERROR_COUNT=$((ERROR_COUNT + 1))
else
    echo -e "${GREEN}✅ 스크립트 한글 키워드 인코딩 정상${NC}"
fi

echo ""
echo "2. 스크립트 파일에서 미인코딩 특수문자 검사..."

# 특수문자가 포함된 미인코딩 패턴 검사 (자기 자신 제외)
UNENCODED_SPECIAL=$(grep -r '\?keyword=[^%]*[+ &]' scripts/ 2>/dev/null | grep -v check-url-encoding.sh)
if [ ! -z "$UNENCODED_SPECIAL" ]; then
    echo -e "${RED}❌ 스크립트에서 미인코딩 특수문자 발견:${NC}"
    echo "$UNENCODED_SPECIAL"
    ERROR_COUNT=$((ERROR_COUNT + 1))
else
    echo -e "${GREEN}✅ 스크립트 특수문자 인코딩 정상${NC}"
fi

echo ""
echo "3. 문서에서 잘못된 예시 검사..."

# 문서에서 잘못된 예시가 ❌ 표시 없이 사용되는지 검사
DOC_BAD_EXAMPLES=$(grep -r '\?keyword=.*[ㄱ-ㅎ가-힣]' docs/ 2>/dev/null | grep -v '❌' | grep -v '절대 금지' | grep -v '실패')
if [ ! -z "$DOC_BAD_EXAMPLES" ]; then
    echo -e "${YELLOW}⚠️  문서에서 잘못된 예시가 경고 없이 사용됨:${NC}"
    echo "$DOC_BAD_EXAMPLES"
    echo -e "${YELLOW}   (❌ 표시나 '절대 금지', '실패' 설명이 없음)${NC}"
else
    echo -e "${GREEN}✅ 문서 예시 적절함${NC}"
fi

echo ""
echo "4. curl 명령어에서 --data-urlencode 사용 검사..."

# curl에서 한글 키워드 사용 시 --data-urlencode 사용 여부 검사 (자기 자신 제외)
CURL_WITHOUT_ENCODE=$(grep -r 'curl.*\?keyword=.*[ㄱ-ㅎ가-힣]' scripts/ 2>/dev/null | grep -v 'data-urlencode' | grep -v '%' | grep -v check-url-encoding.sh)
if [ ! -z "$CURL_WITHOUT_ENCODE" ]; then
    echo -e "${RED}❌ curl에서 한글 키워드를 --data-urlencode 없이 사용:${NC}"
    echo "$CURL_WITHOUT_ENCODE"
    ERROR_COUNT=$((ERROR_COUNT + 1))
else
    echo -e "${GREEN}✅ curl 명령어 인코딩 정상${NC}"
fi

echo ""
echo "===================================="

if [ $ERROR_COUNT -eq 0 ]; then
    echo -e "${GREEN}🎉 모든 URL 인코딩 검사 통과!${NC}"
    echo "검색 API 400 에러 재발 위험 없음"
    exit 0
else
    echo -e "${RED}💥 $ERROR_COUNT개의 URL 인코딩 문제 발견${NC}"
    echo ""
    echo "🔧 해결 방법:"
    echo "  - 한글/특수문자 키워드는 URL 인코딩 필수"
    echo "  - curl 사용 시: curl -G --data-urlencode \"keyword=한글\" ..."
    echo "  - JavaScript: encodeURIComponent(keyword)"
    echo "  - 문서: docs/search-api-encoding-guide.md 참조"
    exit 1
fi
