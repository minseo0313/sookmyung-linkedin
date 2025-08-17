package com.sookmyung.campus_match.service.search;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import com.sookmyung.campus_match.repository.post.PostRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
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

    /**
     * 게시글 검색
     * - 제목, 내용, 작성자명 기준 LIKE 검색
     * - 카테고리별 필터링
     * - 페이징 지원
     */
    public Page<PostSearchResponse> searchPosts(String keyword, String category, Pageable pageable) {
        Page<Post> posts;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드가 있는 경우 제목, 내용, 작성자명으로 검색
            PostCategory categoryEnum = null;
            if (category != null && !category.trim().isEmpty()) {
                try {
                    categoryEnum = PostCategory.valueOf(category.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 잘못된 카테고리명인 경우 무시
                }
            }
            posts = postRepository.searchByKeywordAndCategory(keyword.trim(), categoryEnum, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            // 카테고리만 있는 경우 카테고리로만 검색
            try {
                PostCategory categoryEnum = PostCategory.valueOf(category.trim().toUpperCase());
                posts = postRepository.findByCategory(categoryEnum, pageable);
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리명인 경우 전체 조회
                posts = postRepository.findAll(pageable);
            }
        } else {
            // 키워드와 카테고리가 모두 없는 경우 전체 조회
            posts = postRepository.findAll(pageable);
        }
        
        return posts.map(PostSearchResponse::from);
    }

    /**
     * 사용자 검색
     * - 이름, 학과, 관심사 기준 검색
     * - 승인 상태별 필터링
     * - 페이징 지원
     */
    public Page<UserSearchResponse> searchUsers(String keyword, String department, String interest, Pageable pageable) {
        Page<User> users;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드가 있는 경우 이름, 학과로 검색
            users = userRepository.searchByKeywordAndDepartment(keyword.trim(), department, pageable);
        } else if (department != null && !department.trim().isEmpty()) {
            // 학과만 있는 경우 학과로만 검색
            users = userRepository.findByDepartment(department.trim(), pageable);
        } else {
            // 키워드와 학과가 모두 없는 경우 전체 조회
            users = userRepository.findAll(pageable);
        }
        
        return users.map(UserSearchResponse::from);
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
