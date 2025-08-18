-- TeamSchedule 테이블에 캘린더 조회 성능 향상을 위한 인덱스 생성
-- 팀별 스케줄 조회 시 startAt 기준 내림차순 정렬 성능 최적화

CREATE INDEX idx_team_schedules_team_start_at 
ON team_schedules (team_id, start_at DESC);
