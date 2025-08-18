-- TeamMember 테이블에 team_id와 user_id의 유니크 제약 추가
-- 한 사용자가 같은 팀에 중복 가입하는 것을 방지

ALTER TABLE team_members
  ADD CONSTRAINT uq_team_members_team_user UNIQUE (team_id, user_id);
