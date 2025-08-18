-- PostApplication 테이블에서 application_text 컬럼 제거
-- message 필드로 단일화

ALTER TABLE post_applications
DROP COLUMN IF EXISTS application_text;
