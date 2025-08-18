-- TeamSchedule 테이블의 컬럼명을 startAt/endAt으로 통일
-- 엔티티 필드명과 일치하도록 변경

ALTER TABLE team_schedules 
RENAME COLUMN start_time TO start_at;

ALTER TABLE team_schedules 
RENAME COLUMN end_time TO end_at;
