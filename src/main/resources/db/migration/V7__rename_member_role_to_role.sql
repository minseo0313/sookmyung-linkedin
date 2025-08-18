-- TeamMember 테이블의 member_role 컬럼을 role로 변경
-- 엔티티 필드명과 일치하도록 통일

ALTER TABLE team_members
  RENAME COLUMN member_role TO role;
