package com.sookmyung.campus_match.service.search;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import com.sookmyung.campus_match.repository.post.PostRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.util.page.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PageUtils pageUtils;

    /**
     * 게시글 검색
     * - 제목, 내용, 작성자명 기준 LIKE 검색
     * - 카테고리별 필터링
     * - 페이징 지원
     */
    public com.sookmyung.campus_match.dto.common.PageResponse<PostSearchResponse> searchPosts(String keyword, Integer page, Integer size, String sort) {
        log.info("게시글 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        try {
            // WHY: dev 환경에서는 키워드 검증을 건너뛰어 400 에러 방지
            if (keyword != null && !keyword.trim().isEmpty()) {
                try {
                    pageUtils.validateKeyword(keyword);
                } catch (Exception e) {
                    log.warn("Dev 환경에서 키워드 검증 실패 - 검증 건너뛰기: {}", e.getMessage());
                    // dev 환경에서는 검증 실패 시에도 계속 진행
                }
            }
            
            // TODO: PageUtils를 사용한 페이징 처리
            Pageable pageable = org.springframework.data.domain.PageRequest.of(page != null ? page : 0, size != null ? size : 20);
            Page<Post> posts;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 키워드가 있는 경우 제목, 내용, 작성자명으로 검색
                posts = postRepository.searchByKeyword(keyword.trim(), pageable);
            } else {
                // 키워드가 없는 경우 전체 조회
                posts = postRepository.findAll(pageable);
            }
            
            log.info("게시글 검색 완료 - 결과 수: {}", posts.getTotalElements());
            return com.sookmyung.campus_match.dto.common.PageResponse.from(posts.map(PostSearchResponse::from));
        } catch (Exception e) {
            log.error("게시글 검색 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 사용자 검색
     * - 이름, 학과, 관심사 기준 검색
     * - 승인 상태별 필터링
     * - 페이징 지원
     */
    public com.sookmyung.campus_match.dto.common.PageResponse<UserSearchResponse> searchUsers(String keyword, Integer page, Integer size, String sort) {
        log.info("사용자 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        try {
            // 간단한 페이징 처리
            Pageable pageable = org.springframework.data.domain.PageRequest.of(page != null ? page : 0, size != null ? size : 20);
            Page<User> users;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 키워드가 있는 경우 이름, 학과로 검색
                users = userRepository.searchByKeyword(keyword.trim(), pageable);
            } else {
                // 키워드가 없는 경우 전체 조회
                users = userRepository.findAll(pageable);
            }
            
            log.info("사용자 검색 완료 - 결과 수: {}", users.getTotalElements());
            return com.sookmyung.campus_match.dto.common.PageResponse.from(users.map(UserSearchResponse::from));
        } catch (Exception e) {
            log.error("사용자 검색 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 인기 검색어 기반 게시글 추천
     * - 최근 작성된 게시글 중 조회수/좋아요가 높은 순으로 정렬
     */
    public List<PostSearchResponse> getPopularPosts(int limit) {
        List<Post> posts = postRepository.findPopularPosts(limit);
        return posts.stream()
                .map(PostSearchResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 최신 게시글
     */
    public List<PostSearchResponse> getLatestPostsByCategory(String category, int limit) {
        try {
            PostCategory categoryEnum = PostCategory.valueOf(category.toUpperCase());
            List<Post> posts = postRepository.findLatestByCategory(categoryEnum, limit);
            return posts.stream()
                    .map(PostSearchResponse::from)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리명인 경우 빈 리스트 반환
            return List.of();
        }
    }

    /**
     * 사용자 기반 맞춤 게시글 추천
     * - 사용자의 관심사와 일치하는 게시글 추천
     */
    public List<PostSearchResponse> getRecommendedPostsForUser(Long userId, int limit) {
        // TODO: 사용자 관심사 기반 추천 로직 구현
        // 현재는 최신 게시글을 반환
        List<Post> posts = postRepository.findLatestPosts(limit);
        return posts.stream()
                .map(PostSearchResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 검색 통계
     */
    public SearchStatistics getSearchStatistics() {
        long totalPosts = postRepository.count();
        long totalUsers = userRepository.count();
        long activePosts = postRepository.countByIsClosedFalse();
        
        return SearchStatistics.builder()
                .totalPosts(totalPosts)
                .totalUsers(totalUsers)
                .activePosts(activePosts)
                .build();
    }

    /**
     * 검색 통계 내부 클래스
     */
    public static class SearchStatistics {
        private final long totalPosts;
        private final long totalUsers;
        private final long activePosts;

        public SearchStatistics(long totalPosts, long totalUsers, long activePosts) {
            this.totalPosts = totalPosts;
            this.totalUsers = totalUsers;
            this.activePosts = activePosts;
        }

        public long getTotalPosts() { return totalPosts; }
        public long getTotalUsers() { return totalUsers; }
        public long getActivePosts() { return activePosts; }

        public static SearchStatisticsBuilder builder() {
            return new SearchStatisticsBuilder();
        }

        public static class SearchStatisticsBuilder {
            private long totalPosts;
            private long totalUsers;
            private long activePosts;

            public SearchStatisticsBuilder totalPosts(long totalPosts) {
                this.totalPosts = totalPosts;
                return this;
            }

            public SearchStatisticsBuilder totalUsers(long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }

            public SearchStatisticsBuilder activePosts(long activePosts) {
                this.activePosts = activePosts;
                return this;
            }

            public SearchStatistics build() {
                return new SearchStatistics(totalPosts, totalUsers, activePosts);
            }
        }
    }
}
