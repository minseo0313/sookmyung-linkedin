-- Flyway 마이그레이션 스크립트
-- V3__harden_user_report_count.sql
-- users 테이블의 report_count 컬럼 제약 조건 강화

-- NULL 값은 0으로 교정
UPDATE users SET report_count = 0 WHERE report_count IS NULL;

-- 없으면 추가 (여러 환경에서 안전하게)
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS report_count INT NOT NULL DEFAULT 0;

-- 제약/기본값을 최종적으로 보장
ALTER TABLE users
  MODIFY COLUMN report_count INT NOT NULL DEFAULT 0;

-- 인덱스 추가 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_users_report_count ON users(report_count);

-- 컬럼 설명 추가
ALTER TABLE users 
  MODIFY COLUMN report_count INT NOT NULL DEFAULT 0 COMMENT '사용자 신고 횟수 (서버 관리 필드)';

