package com.sookmyung.campus_match.service.post;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.domain.post.PostLike;
import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import com.sookmyung.campus_match.dto.post.PostCreateRequest;
import com.sookmyung.campus_match.dto.post.PostUpdateRequest;
import com.sookmyung.campus_match.dto.like.PostLikeCountResponse;
import com.sookmyung.campus_match.dto.application.PostApplicationRequest;
import com.sookmyung.campus_match.dto.application.PostApplicationResponse;
import com.sookmyung.campus_match.repository.post.PostRepository;

import com.sookmyung.campus_match.repository.post.PostLikeRepository;
import com.sookmyung.campus_match.repository.post.PostApplicationRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostApplicationRepository postApplicationRepository;
    private final UserRepository userRepository;

    /**
     * AI 기반 게시글 작성 도움 기능
     * - 이전 작성 사례와 입력값을 기반으로 문장 구조 추천
     */
    public String generatePostContentSuggestion(String category, List<String> requiredRoles, 
                                              String duration, int recruitCount) {
        
        StringBuilder suggestion = new StringBuilder();
        
        // 카테고리별 기본 템플릿
        String template = getTemplateByCategory(category);
        suggestion.append(template).append("\n\n");
        
        // 역할별 상세 설명 추가
        if (requiredRoles != null && !requiredRoles.isEmpty()) {
            suggestion.append("【필요 역할】\n");
            for (String role : requiredRoles) {
                suggestion.append("• ").append(role).append(": ");
                suggestion.append(getRoleDescription(role)).append("\n");
            }
            suggestion.append("\n");
        }
        
        // 기간 및 인원 정보
        if (duration != null && !duration.isEmpty()) {
            suggestion.append("【프로젝트 기간】\n");
            suggestion.append("• ").append(duration).append("\n\n");
        }
        
        suggestion.append("【모집 인원】\n");
        suggestion.append("• ").append(recruitCount).append("명\n\n");
        
        // 추가 제안사항
        suggestion.append("【추가 제안】\n");
        suggestion.append("• 구체적인 기술 스택이나 도구를 명시하면 더 좋은 매칭이 가능합니다.\n");
        suggestion.append("• 프로젝트의 목표나 기대 효과를 간단히 설명해보세요.\n");
        suggestion.append("• 연락처나 미팅 일정에 대한 언급도 도움이 됩니다.\n");
        
        return suggestion.toString();
    }

    /**
     * 카테고리별 기본 템플릿 반환
     */
    private String getTemplateByCategory(String category) {
        if (category == null) {
            return getDefaultTemplate();
        }
        
        return switch (category.toLowerCase()) {
            case "개발", "프로그래밍" -> getDevelopmentTemplate();
            case "디자인", "ui/ux" -> getDesignTemplate();
            case "데이터", "분석" -> getDataAnalysisTemplate();
            case "공모전", "대회" -> getCompetitionTemplate();
            case "연구", "논문" -> getResearchTemplate();
            default -> getDefaultTemplate();
        };
    }

    private String getDefaultTemplate() {
        return """
            【프로젝트 소개】
            안녕하세요! 함께 프로젝트를 진행할 팀원을 찾고 있습니다.
            
            【프로젝트 개요】
            • 프로젝트명: [프로젝트명을 입력해주세요]
            • 프로젝트 목적: [목적을 간단히 설명해주세요]
            • 주요 기능: [주요 기능들을 나열해주세요]
            """;
    }

    private String getDevelopmentTemplate() {
        return """
            【개발 프로젝트 소개】
            안녕하세요! 개발 프로젝트에 함께할 팀원을 모집합니다.
            
            【기술 스택】
            • 백엔드: [사용할 기술을 입력해주세요]
            • 프론트엔드: [사용할 기술을 입력해주세요]
            • 데이터베이스: [사용할 DB를 입력해주세요]
            
            【개발 환경】
            • 개발 도구: [IDE, 버전 관리 도구 등]
            • 협업 도구: [Slack, Notion, Figma 등]
            """;
    }

    private String getDesignTemplate() {
        return """
            【디자인 프로젝트 소개】
            안녕하세요! 디자인 프로젝트에 함께할 팀원을 모집합니다.
            
            【디자인 영역】
            • UI/UX 디자인
            • 그래픽 디자인
            • 브랜딩 디자인
            
            【필요한 도구】
            • Figma, Adobe Creative Suite 등
            • 협업 및 프로토타이핑 도구
            """;
    }

    private String getDataAnalysisTemplate() {
        return """
            【데이터 분석 프로젝트 소개】
            안녕하세요! 데이터 분석 프로젝트에 함께할 팀원을 모집합니다.
            
            【분석 영역】
            • 데이터 수집 및 전처리
            • 통계 분석 및 시각화
            • 머신러닝 모델 개발
            
            【사용 기술】
            • Python, R, SQL 등
            • 데이터 시각화 도구
            • 머신러닝 라이브러리
            """;
    }

    private String getCompetitionTemplate() {
        return """
            【공모전/대회 참가팀 모집】
            안녕하세요! 공모전/대회에 함께 참가할 팀원을 모집합니다.
            
            【대회 정보】
            • 대회명: [대회명을 입력해주세요]
            • 주최: [주최기관을 입력해주세요]
            • 마감일: [마감일을 입력해주세요]
            
            【참가 조건】
            • 팀 구성: [팀원 수 및 역할]
            • 제출물: [제출해야 할 결과물]
            """;
    }

    private String getResearchTemplate() {
        return """
            【연구 프로젝트 소개】
            안녕하세요! 연구 프로젝트에 함께할 팀원을 모집합니다.
            
            【연구 분야】
            • 연구 주제: [주제를 입력해주세요]
            • 연구 방법: [방법론을 입력해주세요]
            • 기대 결과: [기대하는 결과를 입력해주세요]
            
            【필요 역량】
            • 관련 분야 지식
            • 논문 작성 및 분석 능력
            • 연구 도구 활용 능력
            """;
    }

    /**
     * 역할별 상세 설명 반환
     */
    private String getRoleDescription(String role) {
        return switch (role.toLowerCase()) {
            case "백엔드", "백엔드 개발자" -> "서버 개발, API 설계, 데이터베이스 관리";
            case "프론트엔드", "프론트엔드 개발자" -> "웹/앱 UI 개발, 사용자 인터페이스 구현";
            case "디자이너", "ui/ux 디자이너" -> "사용자 인터페이스 및 경험 디자인";
            case "데이터", "데이터 분석가" -> "데이터 수집, 분석, 시각화";
            case "기획자", "pm" -> "프로젝트 기획, 요구사항 분석, 일정 관리";
            case "마케터", "마케팅" -> "마케팅 전략 수립, 홍보 및 브랜딩";
            default -> "해당 역할에 대한 구체적인 업무 내용을 입력해주세요";
        };
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public com.sookmyung.campus_match.dto.post.PostDetailResponse createPost(PostCreateRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));
        
        Post post = Post.builder()
                .author(author)
                .category(request.getCategory())
                .postTitle(request.getTitle())
                .postContent(request.getContent())
                .title(request.getTitle())
                .content(request.getContent())
                .requiredRoles(request.getRequiredRoles())
                .recruitmentCount(request.getRecruitmentCount())
                .duration(request.getDuration())
                .linkUrl(request.getLinkUrl())
                .isClosed(false)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .isDeleted(false)
                .build();
        
        Post savedPost = postRepository.save(post);
        return com.sookmyung.campus_match.dto.post.PostDetailResponse.from(savedPost);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public com.sookmyung.campus_match.dto.post.PostDetailResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        // 권한 확인
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }
        
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }
        if (request.getRequiredRoles() != null) {
            post.setRequiredRoles(request.getRequiredRoles());
        }
        if (request.getRecruitmentCount() != null) {
            post.setRecruitmentCount(request.getRecruitmentCount());
        }
        if (request.getDuration() != null) {
            post.setDuration(request.getDuration());
        }
        if (request.getLinkUrl() != null) {
            post.setLinkUrl(request.getLinkUrl());
        }
        
        Post updatedPost = postRepository.save(post);
        return com.sookmyung.campus_match.dto.post.PostDetailResponse.from(updatedPost);
    }

    /**
     * 게시글 조회
     */
    public com.sookmyung.campus_match.dto.post.PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        return com.sookmyung.campus_match.dto.post.PostDetailResponse.from(post);
    }

    /**
     * 게시글 목록 조회
     */
    public com.sookmyung.campus_match.dto.common.PageResponse<com.sookmyung.campus_match.dto.post.PostSummaryResponse> getPosts(Integer page, Integer size, String sort) {
        // TODO: PageUtils를 사용한 페이징 처리
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page != null ? page : 0, size != null ? size : 20);
        Page<Post> posts = postRepository.findAllActiveAndOpen(pageable);
        return com.sookmyung.campus_match.dto.common.PageResponse.from(posts.map(com.sookmyung.campus_match.dto.post.PostSummaryResponse::from));
    }

    /**
     * 조회수 증가
     */
    @Transactional
    public void incrementViews(Long postId) {
        postRepository.incrementViews(postId);
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 이미 좋아요를 눌렀는지 확인
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            // 이미 좋아요를 눌렀으면 무시 (멱등성)
            return;
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);
        post.increaseLikeCount(1);
        postRepository.save(post);
    }

    public com.sookmyung.campus_match.dto.post.PostLikeCountResponse getPostLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        return com.sookmyung.campus_match.dto.post.PostLikeCountResponse.of(postId, post.getLikeCount() != null ? post.getLikeCount().longValue() : 0L);
    }

    @Transactional
    public PostApplicationResponse applyToPost(Long postId, PostApplicationRequest request, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 이미 지원했는지 확인
        if (postApplicationRepository.existsByPostAndApplicant_Id(post, user.getId())) {
            throw new IllegalArgumentException("이미 지원한 게시글입니다.");
        }

        PostApplication application = PostApplication.builder()
                .post(post)
                .applicant(user)
                .message(request.getMessage())
                .status(ApplicationStatus.PENDING)
                .build();

        PostApplication savedApplication = postApplicationRepository.save(application);
        return PostApplicationResponse.from(savedApplication);
    }

    public List<PostApplicationResponse> getApplications(Long postId, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 게시글 작성자만 지원자 목록을 볼 수 있음
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 지원자 목록을 볼 수 있습니다.");
        }

        List<PostApplication> applications = postApplicationRepository.findByPost(post);
        return applications.stream()
                .map(PostApplicationResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void acceptApplication(Long postId, Long applicantId, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 게시글 작성자만 지원을 수락할 수 있음
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 지원을 수락할 수 있습니다.");
        }

        PostApplication application = postApplicationRepository.findByPostAndApplicant_Id(post, applicantId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        application.accept();
        postApplicationRepository.save(application);
    }

    @Transactional
    public void rejectApplication(Long postId, Long applicantId, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 게시글 작성자만 지원을 거절할 수 있음
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 지원을 거절할 수 있습니다.");
        }

        PostApplication application = postApplicationRepository.findByPostAndApplicant_Id(post, applicantId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        application.reject();
        postApplicationRepository.save(application);
    }

    @Transactional
    public void closePost(Long postId, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 게시글 작성자만 모집을 마감할 수 있음
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 모집을 마감할 수 있습니다.");
        }

        post.closeRecruitment();
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 게시글 작성자만 삭제할 수 있음
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        // Soft delete
        post.setIsDeleted(true);
        postRepository.save(post);
    }
}
