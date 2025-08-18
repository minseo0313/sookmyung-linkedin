-- TeamSchedule 테이블에 캘린더 조회 성능 향상을 위한 인덱스 생성 (MySQL 전용)
-- 중복 방지 후 재생성

DROP INDEX IF EXISTS idx_team_schedules_team_start_at ON team_schedules;
CREATE INDEX idx_team_schedules_team_start_at ON team_schedules (team_id, start_at DESC);
