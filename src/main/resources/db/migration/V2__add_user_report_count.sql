-- Flyway 마이그레이션 스크립트
-- V2__add_user_report_count.sql
-- users 테이블에 report_count 컬럼 추가

-- report_count 컬럼 추가
ALTER TABLE users ADD COLUMN report_count INT DEFAULT 0;
