package com.sookmyung.campus_match.service.search;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 실시간 검색 자동완성
     * - 제목과 내용에서 키워드를 추출하여 자동완성 제공
     */
    public List<String> getPostSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        // 제목에서 키워드 검색
        List<Post> titleMatches = postRepository.findByTitleContainingIgnoreCase(keyword, Pageable.ofSize(10)).getContent();
        
        // 내용에서 키워드 검색
        List<Post> contentMatches = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword, Pageable.ofSize(10)).getContent();

        // 키워드 추출 및 중복 제거
        Set<String> suggestions = titleMatches.stream()
                .map(post -> extractKeywordsFromTitle(post.getTitle()))
                .flatMap(List::stream)
                .filter(word -> word.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toSet());

        suggestions.addAll(contentMatches.stream()
                .map(post -> extractKeywordsFromContent(post.getContent()))
                .flatMap(List::stream)
                .filter(word -> word.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toSet()));

        // 키워드 길이순으로 정렬하고 상위 10개 반환
        return suggestions.stream()
                .sorted((a, b) -> {
                    // 정확히 일치하는 키워드를 우선
                    if (a.toLowerCase().equals(keyword.toLowerCase())) return -1;
                    if (b.toLowerCase().equals(keyword.toLowerCase())) return 1;
                    // 길이순 정렬
                    return Integer.compare(a.length(), b.length());
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 검색 자동완성
     * - 이름과 학과에서 키워드를 추출하여 자동완성 제공
     */
    public List<String> getUserSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        // 승인된 사용자만 검색 대상
        List<User> users = userRepository.findByApprovalStatus(ApprovalStatus.APPROVED);

        Set<String> suggestions = users.stream()
                .map(user -> List.of(user.getFullName(), user.getDepartment()))
                .flatMap(List::stream)
                .filter(text -> text.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toSet());

        return suggestions.stream()
                .sorted((a, b) -> {
                    // 정확히 일치하는 키워드를 우선
                    if (a.toLowerCase().equals(keyword.toLowerCase())) return -1;
                    if (b.toLowerCase().equals(keyword.toLowerCase())) return 1;
                    // 길이순 정렬
                    return Integer.compare(a.length(), b.length());
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 검색
     */
    public Page<PostSearchResponse> searchPosts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword, pageable);

        return posts.map(PostSearchResponse::from);
    }

    /**
     * 사용자 검색
     */
    public Page<UserSearchResponse> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<User> users = userRepository.findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                keyword, keyword, pageable);

        // 승인된 사용자만 필터링하여 새로운 Page 생성
        List<UserSearchResponse> filteredContent = users.getContent().stream()
                .filter(user -> user.getApprovalStatus() == ApprovalStatus.APPROVED)
                .map(UserSearchResponse::from)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(filteredContent, pageable, users.getTotalElements());
    }

    /**
     * 제목에서 키워드 추출
     */
    private List<String> extractKeywordsFromTitle(String title) {
        if (title == null || title.isEmpty()) {
            return List.of();
        }

        return List.of(title.split("\\s+"));
    }

    /**
     * 내용에서 키워드 추출
     */
    private List<String> extractKeywordsFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }

        // 간단한 키워드 추출 (실제로는 더 정교한 NLP 처리 필요)
        return List.of(content.split("\\s+"));
    }

    /**
     * 통합 검색 자동완성
     * - 게시글과 사용자 검색을 모두 포함
     */
    public List<String> getUnifiedSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<String> postSuggestions = getPostSearchSuggestions(keyword);
        List<String> userSuggestions = getUserSearchSuggestions(keyword);

        // 통합하여 중복 제거
        Set<String> allSuggestions = new java.util.LinkedHashSet<>();
        allSuggestions.addAll(postSuggestions);
        allSuggestions.addAll(userSuggestions);

        return allSuggestions.stream()
                .sorted((a, b) -> {
                    // 정확히 일치하는 키워드를 우선
                    if (a.toLowerCase().equals(keyword.toLowerCase())) return -1;
                    if (b.toLowerCase().equals(keyword.toLowerCase())) return 1;
                    // 길이순 정렬
                    return Integer.compare(a.length(), b.length());
                })
                .limit(15) // 통합 검색이므로 더 많은 결과 제공
                .collect(Collectors.toList());
    }
}
