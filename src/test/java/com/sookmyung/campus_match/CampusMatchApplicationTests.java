package com.sookmyung.campus_match;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.sookmyung.campus_match.config.TestConfig;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.repository.user.UserInterestRepository;
import com.sookmyung.campus_match.repository.user.ProfileRepository;
import com.sookmyung.campus_match.repository.user.InterestRepository;
import com.sookmyung.campus_match.repository.user.ExperienceRepository;
import com.sookmyung.campus_match.repository.team.TeamSecretaryRepository;
import com.sookmyung.campus_match.repository.team.TeamScheduleRepository;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.team.ScheduleAssignmentRepository;
import com.sookmyung.campus_match.repository.recommendation.UserRecommendationRepository;
import com.sookmyung.campus_match.repository.recommendation.UserEmbeddingRepository;
import com.sookmyung.campus_match.repository.post.PostApplicationRepository;
import com.sookmyung.campus_match.repository.admin.AdminRepository;
import com.sookmyung.campus_match.repository.message.MessageReportRepository;
import com.sookmyung.campus_match.repository.message.MessageRepository;
import com.sookmyung.campus_match.repository.post.PostLikeRepository;
import com.sookmyung.campus_match.repository.admin.SystemNoticeRepository;
import com.sookmyung.campus_match.repository.post.PostCommentRepository;
import com.sookmyung.campus_match.repository.message.MessageThreadRepository;
import com.sookmyung.campus_match.repository.post.PostRepository;
import org.flywaydb.core.Flyway;
import com.sookmyung.campus_match.config.DataInitializer;

@SpringBootTest(
    classes = CampusMatchApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
@Import(TestConfig.class)
class CampusMatchApplicationTests {

    // 모든 리포지토리를 MockBean으로 등록하여 NoSuchBeanDefinitionException 방지
    @MockBean UserRepository userRepository;
    @MockBean UserInterestRepository userInterestRepository;
    @MockBean ProfileRepository profileRepository;
    @MockBean InterestRepository interestRepository;
    @MockBean ExperienceRepository experienceRepository;
    @MockBean TeamSecretaryRepository teamSecretaryRepository;
    @MockBean TeamScheduleRepository teamScheduleRepository;
    @MockBean TeamRepository teamRepository;
    @MockBean TeamMemberRepository teamMemberRepository;
    @MockBean ScheduleAssignmentRepository scheduleAssignmentRepository;
    @MockBean UserRecommendationRepository userRecommendationRepository;
    @MockBean UserEmbeddingRepository userEmbeddingRepository;
    @MockBean PostApplicationRepository postApplicationRepository;
    @MockBean AdminRepository adminRepository;
    @MockBean MessageReportRepository messageReportRepository;
    @MockBean MessageRepository messageRepository;
    @MockBean PostLikeRepository postLikeRepository;
    @MockBean SystemNoticeRepository systemNoticeRepository;
    @MockBean PostCommentRepository postCommentRepository;
    @MockBean MessageThreadRepository messageThreadRepository;
    @MockBean PostRepository postRepository;
    @MockBean Flyway flyway;
    @MockBean DataInitializer dataInitializer;

	@Test
	void contextLoads() {
	}

}
