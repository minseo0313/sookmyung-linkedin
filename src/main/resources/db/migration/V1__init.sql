-- Flyway 초기 DDL 스크립트
-- V1__init.sql

-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL UNIQUE,
    department VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
    operator BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 프로필 테이블
CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    department VARCHAR(255),
    student_code VARCHAR(2),
    bio VARCHAR(100),
    profile_image_url VARCHAR(255),
    location VARCHAR(255),
    website_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    view_count INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 관심사 테이블
CREATE TABLE interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interest_name VARCHAR(255) NOT NULL,
    interest_type ENUM('PREDEFINED', 'CUSTOM') NOT NULL,
    interest_name_en VARCHAR(255),
    category VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 사용자 관심사 테이블
CREATE TABLE user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interest_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (interest_id) REFERENCES interests(id),
    UNIQUE KEY uk_user_interests_user_interest (user_id, interest_id)
);

-- 경력/활동 테이블
CREATE TABLE experiences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    experience_type ENUM('CAREER', 'ACTIVITY') NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    tags VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 게시글 카테고리 테이블
CREATE TABLE post_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 게시글 테이블
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT,
    post_title VARCHAR(255) NOT NULL,
    post_content TEXT,
    required_roles VARCHAR(255),
    recruitment_count INT,
    duration VARCHAR(255),
    link_url VARCHAR(255),
    image_url VARCHAR(255),
    is_closed BOOLEAN,
    view_count INT,
    like_count INT,
    comment_count INT,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (category_id) REFERENCES post_categories(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 게시글 지원 테이블
CREATE TABLE post_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_text TEXT,
    application_status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL,
    submitted_at DATETIME NOT NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    team_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 게시글 좋아요 테이블
CREATE TABLE post_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_post_likes_post_user (post_id, user_id)
);

-- 게시글 댓글 테이블
CREATE TABLE post_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment_content TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 메시지 스레드 테이블
CREATE TABLE message_threads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    started_from_type ENUM('PROFILE', 'POST') NOT NULL,
    started_from_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id)
);

-- 메시지 테이블
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    message_content TEXT,
    thread_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (thread_id) REFERENCES message_threads(id)
);

-- 메시지 신고 테이블
CREATE TABLE message_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reported_message_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    report_reason TEXT,
    report_status ENUM('PENDING', 'ACCEPTED') NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (reported_message_id) REFERENCES messages(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (reported_user_id) REFERENCES users(id)
);

-- 사용자 추천 테이블
CREATE TABLE user_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recommended_user_id BIGINT NOT NULL,
    similarity_score DECIMAL(5,4),
    recommendation_reason TEXT,
    generated_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (recommended_user_id) REFERENCES users(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 사용자 임베딩 테이블
CREATE TABLE user_embeddings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    embedding_vector TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 팀 테이블
CREATE TABLE teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by BIGINT NOT NULL,
    created_from ENUM('POST', 'PROFILE') NOT NULL,
    confirmed_at DATETIME,
    post_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- 팀 멤버 테이블
CREATE TABLE team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_role ENUM('LEADER', 'MEMBER') NOT NULL,
    joined_at DATETIME NOT NULL,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 팀 비서 테이블
CREATE TABLE team_secretaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

-- 팀 일정 테이블
CREATE TABLE team_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by BIGINT NOT NULL,
    schedule_title VARCHAR(255) NOT NULL,
    schedule_description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    secretary_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (secretary_id) REFERENCES team_secretaries(id)
);

-- 일정 할당 테이블
CREATE TABLE schedule_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assigned_to BIGINT NOT NULL,
    assignment_status ENUM('TODO', 'IN_PROGRESS', 'DONE') NOT NULL,
    schedule_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (schedule_id) REFERENCES team_schedules(id)
);

-- 관리자 테이블
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 시스템 공지 테이블
CREATE TABLE system_notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by BIGINT NOT NULL,
    notice_title VARCHAR(255) NOT NULL,
    notice_content TEXT,
    visible_from DATETIME,
    visible_to DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (created_by) REFERENCES admins(id)
);

-- 인덱스 생성
CREATE INDEX idx_users_approval_status ON users(approval_status);
CREATE INDEX idx_users_department ON users(department);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_category_id ON posts(category_id);
CREATE INDEX idx_posts_is_closed ON posts(is_closed);
CREATE INDEX idx_post_applications_post_id ON post_applications(post_id);
CREATE INDEX idx_post_applications_user_id ON post_applications(user_id);
CREATE INDEX idx_post_likes_post_id ON post_likes(post_id);
CREATE INDEX idx_post_likes_user_id ON post_likes(user_id);
CREATE INDEX idx_post_comments_post_id ON post_comments(post_id);
CREATE INDEX idx_post_comments_user_id ON post_comments(user_id);
CREATE INDEX idx_message_threads_user1_id ON message_threads(user1_id);
CREATE INDEX idx_message_threads_user2_id ON message_threads(user2_id);
CREATE INDEX idx_messages_thread_id ON messages(thread_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_teams_created_by ON teams(created_by);
CREATE INDEX idx_teams_post_id ON teams(post_id);
CREATE INDEX idx_team_members_team_id ON team_members(team_id);
CREATE INDEX idx_team_members_user_id ON team_members(user_id);
CREATE INDEX idx_team_schedules_secretary_id ON team_schedules(secretary_id);
CREATE INDEX idx_schedule_assignments_schedule_id ON schedule_assignments(schedule_id);
CREATE INDEX idx_schedule_assignments_assigned_to ON schedule_assignments(assigned_to);
CREATE INDEX idx_user_recommendations_user_id ON user_recommendations(user_id);
CREATE INDEX idx_user_recommendations_recommended_user_id ON user_recommendations(recommended_user_id);
CREATE INDEX idx_system_notices_visible_from ON system_notices(visible_from);
CREATE INDEX idx_system_notices_visible_to ON system_notices(visible_to);




