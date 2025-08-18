-- PostApplication 테이블에서 submitted_at 컬럼 제거
-- BaseEntity의 createdAt 필드만 사용하도록 통일

ALTER TABLE post_applications DROP COLUMN submitted_at;
