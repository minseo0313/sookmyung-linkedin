package com.sookmyung.campus_match.service.post;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import com.sookmyung.campus_match.dto.post.PostApplicationRequest;
import com.sookmyung.campus_match.dto.application.PostApplicationResponse;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.repository.post.PostRepository;
import com.sookmyung.campus_match.repository.post.PostApplicationRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.util.page.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostApplicationService {

    private final PostRepository postRepository;
    private final PostApplicationRepository postApplicationRepository;
    private final UserRepository userRepository;
    private final PageUtils pageUtils;

    /**
     * 게시글에 지원
     */
    @Transactional
    public PostApplicationResponse applyToPost(Long postId, PostApplicationRequest request, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));

        // 게시글이 마감되었는지 확인
        if (post.getIsClosed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 마감된 게시글입니다");
        }

        // 이미 지원했는지 확인
        if (postApplicationRepository.existsByPost_IdAndApplicant_Id(postId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 지원한 게시글입니다");
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

    /**
     * 게시글 모집 마감
     */
    @Transactional
    public void closePost(Long postId, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));

        // 게시글 작성자만 모집을 마감할 수 있음
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 모집을 마감할 수 있습니다");
        }

        // 이미 마감되었는지 확인
        if (post.getIsClosed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 마감된 게시글입니다");
        }

        post.setIsClosed(true);
        postRepository.save(post);
    }

    /**
     * 지원 목록 조회 (페이징)
     */
    public PageResponse<PostApplicationResponse> getApplications(Long postId, Long currentUserId, Integer page, Integer size, String sort) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));

        // 게시글 작성자만 지원자 목록을 볼 수 있음
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 지원자 목록을 볼 수 있습니다");
        }

        Pageable pageable = pageUtils.createPostApplicationPageable(page, size, sort);
        Page<PostApplication> applications = postApplicationRepository.findByPost_Id(postId, pageable);
        
        return PageResponse.from(applications.map(PostApplicationResponse::from));
    }

    /**
     * 지원 수락
     */
    @Transactional
    public void acceptApplication(Long postId, Long applicantId, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));

        // 게시글 작성자만 지원을 수락할 수 있음
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 지원을 수락할 수 있습니다");
        }

        PostApplication application = postApplicationRepository.findByPost_IdAndApplicant_Id(postId, applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없습니다"));

        // 이미 처리된 지원인지 확인
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 처리된 지원입니다");
        }

        application.accept();
        postApplicationRepository.save(application);
    }

    /**
     * 지원 거절
     */
    @Transactional
    public void rejectApplication(Long postId, Long applicantId, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));

        // 게시글 작성자만 지원을 거절할 수 있음
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 지원을 거절할 수 있습니다");
        }

        PostApplication application = postApplicationRepository.findByPost_IdAndApplicant_Id(postId, applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없습니다"));

        // 이미 처리된 지원인지 확인
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 처리된 지원입니다");
        }

        application.reject();
        postApplicationRepository.save(application);
    }
}
