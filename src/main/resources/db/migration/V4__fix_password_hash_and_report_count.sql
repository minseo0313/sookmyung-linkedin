-- Flyway 마이그레이션 스크립트
-- V4__fix_password_hash_and_report_count.sql
-- users 테이블의 password 컬럼을 password_hash로 변경하고 report_count 기본값 설정

-- 1. password 컬럼을 password_hash로 변경
-- 기존 password 컬럼이 있다면 이름 변경
ALTER TABLE users CHANGE COLUMN password password_hash VARCHAR(255) NOT NULL;

-- 2. report_count 컬럼이 없다면 추가
ALTER TABLE users ADD COLUMN IF NOT EXISTS report_count INT NOT NULL DEFAULT 0;

-- 3. 기존 NULL 값들을 0으로 교정
UPDATE users SET report_count = 0 WHERE report_count IS NULL;

-- 4. 컬럼 제약 조건 및 기본값 최종 설정
ALTER TABLE users 
  MODIFY COLUMN report_count INT NOT NULL DEFAULT 0;

-- 5. 인덱스 추가 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_users_report_count ON users(report_count);

-- 6. 컬럼 설명 추가
ALTER TABLE users 
  MODIFY COLUMN password_hash VARCHAR(255) NOT NULL COMMENT '비밀번호 해시값 (BCrypt)';
ALTER TABLE users 
  MODIFY COLUMN report_count INT NOT NULL DEFAULT 0 COMMENT '사용자 신고 횟수 (서버 관리 필드, 기본값: 0)';

