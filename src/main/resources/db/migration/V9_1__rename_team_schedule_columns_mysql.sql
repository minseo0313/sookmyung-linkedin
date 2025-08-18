-- TeamSchedule 테이블의 컬럼명을 startAt/endAt으로 통일 (MySQL 전용)
-- 엔티티 필드명과 일치하도록 변경

ALTER TABLE team_schedules CHANGE COLUMN start_time start_at DATETIME NOT NULL;
ALTER TABLE team_schedules CHANGE COLUMN end_time   end_at   DATETIME NOT NULL;
