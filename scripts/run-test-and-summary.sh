#!/bin/bash

# === 전체 API 테스트 실행 & 요약 출력 스니펫 ===
# 목적: full-api-test.sh 실행 결과를 저장하고, 총/성공/실패 개수와 성공률, 상태코드 분포, 실패 상세를 한 번에 출력

set -u

OUT="/tmp/api-$(date +%s).out"

echo "=== 전체 테스트 실행 ==="
if [ -x ./scripts/full-api-test.sh ]; then
  ./scripts/full-api-test.sh | tee "$OUT"
else
  echo "[ERROR] ./scripts/full-api-test.sh 가 없습니다 또는 실행 권한이 없습니다." >&2
  exit 1
fi

echo
echo "=== 테스트 결과 요약 ==="
TOTAL=$(grep -c '^TEST ' "$OUT" || true)
PASS=$(grep -c '✅ PASS' "$OUT" || true)
FAIL=$(grep -c '❌ FAIL' "$OUT" || true)

# 성공률 계산(소수점 1자리)
if [ "${TOTAL:-0}" -gt 0 ]; then
  RATE=$(awk -v p="${PASS:-0}" -v t="${TOTAL:-0}" 'BEGIN{printf "%.1f", (p*100.0)/t}')
else
  RATE="0.0"
fi

echo "총 테스트: ${TOTAL}"
echo "성공: ${PASS}"
echo "실패: ${FAIL}"
echo "성공률: ${RATE}%"

echo
echo "=== 상태코드 분포(응답) ==="
for code in 200 201 204 400 401 403 404 405 409 422 429 500 502 503; do
  cnt=$(grep -c "Got: ${code}" "$OUT" || true)
  [ "$cnt" -gt 0 ] && printf "%3s: %d\n" "$code" "$cnt"
done

echo
echo "=== 실패 케이스 상위 20개(컨텍스트 포함) ==="
# '❌ FAIL' 라인을 중심으로 앞뒤 몇 줄을 보여줘서 원인 파악 용이하게
grep -B2 -A3 '❌ FAIL' "$OUT" | sed 's/^/[FAIL] /' | head -n 200 || echo "실패 케이스 없음"

echo
echo "=== 실패 엔드포인트 목록(요약) ==="
# 로그 포맷에 따라 다음 패턴 중 하나가 잡힙니다. 필요시 아래 awk/grep 패턴을 프로젝트 로그 포맷에 맞게 조정하세요.
# 1) 'REQUEST:' 라인이 있는 포맷
awk '/❌ FAIL/{show=1} show && /REQUEST:/{print; show=0}' "$OUT" 2>/dev/null \
| sed 's/^/[REQ] /' \
|| true

# 2) 'GET/POST ...' 가 같은 줄에 있는 포맷(보조)
grep -A0 -B0 -nE '❌ FAIL|^(GET|POST|PUT|DELETE|PATCH) ' "$OUT" 2>/dev/null \
| sed 's/^/[LINE] /' \
| head -n 200 || true

echo
echo "=== 4xx/5xx 원인별 샘플 ==="
echo "--- 400 ---"; grep -B2 -A2 "Got: 400" "$OUT" | head -n 60 || true
echo "--- 404 ---"; grep -B2 -A2 "Got: 404" "$OUT" | head -n 60 || true
echo "--- 405 ---"; grep -B2 -A2 "Got: 405" "$OUT" | head -n 60 || true
echo "--- 500 ---"; grep -B2 -A2 "Got: 500" "$OUT" | head -n 60 || true

echo
echo "=== 결과 파일 ==="
echo "$OUT"

# 실패가 0이면 종료코드 0, 아니면 1(원하면 CI에서 바로 페일 처리 가능)
[ "${FAIL:-0}" -eq 0 ]
