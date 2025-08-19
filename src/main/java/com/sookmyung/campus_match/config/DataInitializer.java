package com.sookmyung.campus_match.config;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.repository.user.ProfileRepository;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import com.sookmyung.campus_match.domain.common.enums.CreatedFrom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final Flyway flyway;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("테스트 데이터 초기화 시작...");
        
        // Flyway 마이그레이션이 완료될 때까지 대기
        try {
            Thread.sleep(2000); // 2초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("데이터 초기화 대기가 중단되었습니다.");
            return;
        }
        
        // 기존 데이터가 있으면 초기화하지 않음
        if (userRepository.count() > 0) {
            log.info("기존 데이터가 존재하여 초기화를 건너뜁니다.");
            return;
        }

        log.info("Flyway 마이그레이션 상태 확인 중...");
        try {
            // Flyway 정보 로깅
            log.info("Flyway 마이그레이션 완료됨. 데이터 초기화를 시작합니다.");
        } catch (Exception e) {
            log.error("Flyway 상태 확인 중 오류 발생: {}", e.getMessage());
            return;
        }

        // 1. 사용자 생성
        User user1 = createUser("20250001", "테스터1", "컴퓨터학부", "test1@sookmyung.ac.kr", "010-1234-5678");
        User user2 = createUser("20250002", "테스터2", "경영학부", "test2@sookmyung.ac.kr", "010-1234-5679");
        User user3 = createUser("20250003", "테스터3", "디자인학부", "test3@sookmyung.ac.kr", "010-1234-5680");
        User user4 = createUser("20250004", "테스터4", "컴퓨터학부", "test4@sookmyung.ac.kr", "010-1234-5681");
        User user5 = createUser("20250005", "테스터5", "경영학부", "test5@sookmyung.ac.kr", "010-1234-5682");

        // 2. 프로필 생성
        createProfile(user1, "웹 개발자", "React와 Spring Boot를 사용한 웹 개발을 하고 있습니다.", true);
        createProfile(user2, "UX 디자이너", "사용자 경험을 중시하는 디자인을 하고 있습니다.", true);
        createProfile(user3, "프로젝트 매니저", "팀 프로젝트 관리와 기획을 담당하고 있습니다.", false);
        createProfile(user4, "백엔드 개발자", "Java와 Spring을 사용한 서버 개발을 하고 있습니다.", true);
        createProfile(user5, "프론트엔드 개발자", "JavaScript와 React를 사용한 클라이언트 개발을 하고 있습니다.", true);

        // 3. 팀 생성
        Team team1 = createTeam("웹 개발팀", "React와 Spring Boot를 사용한 웹 서비스 개발", 5, user1);
        Team team2 = createTeam("모바일 앱팀", "Flutter를 사용한 크로스 플랫폼 앱 개발", 3, user2);
        Team team3 = createTeam("AI 연구팀", "머신러닝과 딥러닝을 활용한 AI 서비스 개발", 4, user3);

        // 4. 팀 멤버 추가
        addTeamMember(team1, user1, MemberRole.LEADER);
        addTeamMember(team1, user4, MemberRole.MEMBER);
        addTeamMember(team1, user5, MemberRole.MEMBER);
        
        addTeamMember(team2, user2, MemberRole.LEADER);
        addTeamMember(team2, user3, MemberRole.MEMBER);
        
        addTeamMember(team3, user3, MemberRole.LEADER);
        addTeamMember(team3, user1, MemberRole.MEMBER);
        addTeamMember(team3, user4, MemberRole.MEMBER);

        // 5. 게시글 생성
        createPost(user1, "웹 개발자 모집", "React와 Spring Boot를 사용한 웹 서비스 개발팀을 모집합니다.", PostCategory.PROJECT, 2, "개발자", "3개월", "https://github.com/example");
        createPost(user2, "UX 디자이너 모집", "사용자 경험을 중시하는 디자이너를 모집합니다.", PostCategory.STUDY, 1, "디자이너", "2개월", null);
        createPost(user3, "프로젝트 매니저 모집", "팀 프로젝트를 이끌어갈 매니저를 모집합니다.", PostCategory.PROJECT, 1, "매니저", "6개월", "https://example.com");

        log.info("테스트 데이터 초기화 완료!");
    }

    private User createUser(String studentId, String name, String department, String email, String phoneNumber) {
        User user = User.builder()
                .studentId(studentId)
                .name(name)
                .department(department)
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber(phoneNumber)
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .operator(false)
                .reportCount(0)
                .build();
        return userRepository.save(user);
    }

    private Profile createProfile(User user, String headline, String bio, boolean greetingEnabled) {
        Profile profile = Profile.builder()
                .user(user)
                .department(user.getDepartment())
                .studentCode(user.getStudentId())
                .headline(headline)
                .bio(bio)
                .greetingEnabled(greetingEnabled)
                .viewCount(0)
                .build();
        return profileRepository.save(profile);
    }

    private Team createTeam(String teamName, String description, int maxMembers, User leader) {
        Team team = Team.builder()
                .teamName(teamName)
                .description(description)
                .maxMembers(maxMembers)
                .isActive(true)
                .createdBy(leader)
                .createdFrom(CreatedFrom.PROFILE)
                .build();
        return teamRepository.save(team);
    }

    private void addTeamMember(Team team, User user, MemberRole role) {
        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .role(role)
                .build();
        teamMemberRepository.save(member);
    }

    private Post createPost(User author, String title, String content, PostCategory category, 
                          int recruitmentCount, String requiredRoles, String duration, String linkUrl) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .category(category)
                .recruitmentCount(recruitmentCount)
                .requiredRoles(requiredRoles)
                .duration(duration)
                .linkUrl(linkUrl)
                .isClosed(false)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .build();
        return postRepository.save(post);
    }
}
