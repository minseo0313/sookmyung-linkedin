package com.sookmyung.campus_match.service.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserEmbedding;
import com.sookmyung.campus_match.domain.recommendation.UserRecommendation;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.UserInterest;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.repository.recommendation.UserEmbeddingRepository;
import com.sookmyung.campus_match.repository.recommendation.UserRecommendationRepository;
import com.sookmyung.campus_match.repository.user.UserInterestRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.repository.post.PostRepository;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final PostRepository postRepository;
    private final UserEmbeddingRepository userEmbeddingRepository;
    private final UserRecommendationRepository userRecommendationRepository;

    /**
     * 사용자에게 추천할 다른 사용자 목록을 생성
     * - 관심사, 자기소개, 작성한 글을 기반으로 유사도 분석
     */
    @Transactional
    public List<UserRecommendation> generateRecommendationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 승인된 사용자만 추천 대상
        List<User> approvedUsers = userRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
        approvedUsers.remove(user); // 본인 제외

        List<UserRecommendation> recommendations = new ArrayList<>();

        for (User candidate : approvedUsers) {
            double similarityScore = calculateSimilarity(user, candidate);
            
            if (similarityScore > 0.1) { // 최소 유사도 임계값
                UserRecommendation recommendation = UserRecommendation.builder()
                        .user(user)
                        .recommendedUser(candidate)
                        .similarityScore(BigDecimal.valueOf(similarityScore))
                        .build();
                
                recommendations.add(recommendation);
            }
        }

        // 유사도 점수 순으로 정렬
        recommendations.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));

        // 상위 20개만 저장
        List<UserRecommendation> topRecommendations = recommendations.stream()
                .limit(20)
                .collect(Collectors.toList());

        // 기존 추천 기록 삭제 후 새로운 추천 저장
        userRecommendationRepository.deleteByUserId(userId);
        userRecommendationRepository.saveAll(topRecommendations);

        return topRecommendations;
    }

    /**
     * 두 사용자 간의 유사도 계산
     * - 관심사 일치: 40%
     * - 학과 일치: 30%
     * - 게시글 카테고리 일치: 20%
     * - 자기소개 키워드 일치: 10%
     */
    private double calculateSimilarity(User user1, User user2) {
        double totalScore = 0.0;

        // 1. 관심사 일치도 (40%)
        double interestSimilarity = calculateInterestSimilarity(user1, user2);
        totalScore += interestSimilarity * 0.4;

        // 2. 학과 일치도 (30%)
        double departmentSimilarity = calculateDepartmentSimilarity(user1, user2);
        totalScore += departmentSimilarity * 0.3;

        // 3. 게시글 카테고리 일치도 (20%)
        double postSimilarity = calculatePostSimilarity(user1, user2);
        totalScore += postSimilarity * 0.2;

        // 4. 자기소개 키워드 일치도 (10%)
        double bioSimilarity = calculateBioSimilarity(user1, user2);
        totalScore += bioSimilarity * 0.1;

        return Math.min(1.0, totalScore);
    }

    /**
     * 관심사 일치도 계산
     */
    private double calculateInterestSimilarity(User user1, User user2) {
        List<UserInterest> interests1 = userInterestRepository.findByUser(user1);
        List<UserInterest> interests2 = userInterestRepository.findByUser(user2);

        if (interests1.isEmpty() && interests2.isEmpty()) {
            return 0.5; // 둘 다 관심사가 없으면 중간 점수
        }

        Set<Long> interestIds1 = interests1.stream()
                .map(interest -> interest.getInterest().getId())
                .collect(Collectors.toSet());
        
        Set<Long> interestIds2 = interests2.stream()
                .map(interest -> interest.getInterest().getId())
                .collect(Collectors.toSet());

        if (interestIds1.isEmpty() || interestIds2.isEmpty()) {
            return 0.0;
        }

        // Jaccard 유사도 계산
        Set<Long> intersection = new HashSet<>(interestIds1);
        intersection.retainAll(interestIds2);

        Set<Long> union = new HashSet<>(interestIds1);
        union.addAll(interestIds2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 학과 일치도 계산
     */
    private double calculateDepartmentSimilarity(User user1, User user2) {
        return user1.getDepartment().equals(user2.getDepartment()) ? 1.0 : 0.0;
    }

    /**
     * 게시글 카테고리 일치도 계산
     */
    private double calculatePostSimilarity(User user1, User user2) {
        List<Post> posts1 = postRepository.findByAuthor_Id(user1.getId(), null).getContent();
        List<Post> posts2 = postRepository.findByAuthor_Id(user2.getId(), null).getContent();

        if (posts1.isEmpty() && posts2.isEmpty()) {
            return 0.5; // 둘 다 게시글이 없으면 중간 점수
        }

        Set<PostCategory> categories1 = posts1.stream()
                .map(post -> post.getCategory())
                .collect(Collectors.toSet());
        
        Set<PostCategory> categories2 = posts2.stream()
                .map(post -> post.getCategory())
                .collect(Collectors.toSet());

        if (categories1.isEmpty() || categories2.isEmpty()) {
            return 0.0;
        }

        // Jaccard 유사도 계산
        Set<PostCategory> intersection = new HashSet<>(categories1);
        intersection.retainAll(categories2);

        Set<PostCategory> union = new HashSet<>(categories1);
        union.addAll(categories2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 자기소개 키워드 일치도 계산
     */
    private double calculateBioSimilarity(User user1, User user2) {
        // 간단한 키워드 매칭 (실제로는 더 정교한 NLP 처리 필요)
        String bio1 = user1.getProfile() != null ? user1.getProfile().getBio() : "";
        String bio2 = user2.getProfile() != null ? user2.getProfile().getBio() : "";

        if (bio1.isEmpty() && bio2.isEmpty()) {
            return 0.5;
        }

        // 공통 키워드 찾기 (간단한 구현)
        Set<String> keywords1 = extractKeywords(bio1);
        Set<String> keywords2 = extractKeywords(bio2);

        if (keywords1.isEmpty() || keywords2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(keywords1);
        intersection.retainAll(keywords2);

        Set<String> union = new HashSet<>(keywords1);
        union.addAll(keywords2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 텍스트에서 키워드 추출 (간단한 구현)
     */
    private Set<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }

        // 간단한 키워드 추출 (실제로는 더 정교한 NLP 처리 필요)
        return Arrays.stream(text.toLowerCase()
                .replaceAll("[^a-zA-Z가-힣\\s]", " ")
                .split("\\s+"))
                .filter(word -> word.length() > 1)
                .collect(Collectors.toSet());
    }

    /**
     * 사용자의 추천 목록 조회
     */
    public List<UserRecommendation> getRecommendationsForUser(Long userId) {
        return userRecommendationRepository.findByUser_Id(userId);
    }

    /**
     * 모든 사용자의 추천 목록 재생성 (배치 작업용)
     */
    @Transactional
    public void regenerateAllRecommendations() {
        List<User> approvedUsers = userRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
        
        for (User user : approvedUsers) {
            try {
                generateRecommendationsForUser(user.getId());
                log.info("Generated recommendations for user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Failed to generate recommendations for user: {}", user.getUsername(), e);
            }
        }
    }

    /**
     * 사용자에게 추천할 다른 사용자 목록 조회 (페이징)
     */
    public Page<User> getRecommendedUsers(String username, Pageable pageable) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // 승인된 사용자 중 본인을 제외한 사용자들을 반환
        return userRepository.findByApprovalStatusAndIdNot(ApprovalStatus.APPROVED, user.getId(), pageable);
    }

    /**
     * 특정 사용자에게 추천할 다른 사용자 목록 조회
     */
    public List<User> getRecommendedUsersForUser(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // 승인된 사용자 중 본인을 제외한 사용자들을 반환
        List<User> recommendedUsers = userRepository.findByApprovalStatusAndIdNot(ApprovalStatus.APPROVED, user.getId());
        
        // 간단한 추천 로직: 같은 학과 사용자 우선
        recommendedUsers.sort((u1, u2) -> {
            boolean sameDept1 = u1.getDepartment().equals(user.getDepartment());
            boolean sameDept2 = u2.getDepartment().equals(user.getDepartment());
            if (sameDept1 && !sameDept2) return -1;
            if (!sameDept1 && sameDept2) return 1;
            return 0;
        });
        
        return recommendedUsers.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 관심사 기반 사용자 추천
     */
    public List<User> getUsersByInterest(String interestType, int limit) {
        // 간단한 구현: 승인된 사용자 중 랜덤하게 반환
        List<User> users = userRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
        Collections.shuffle(users);
        return users.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 학과 기반 사용자 추천
     */
    public List<User> getUsersByDepartment(String department, int limit) {
        return userRepository.findByDepartmentAndApprovalStatus(department, ApprovalStatus.APPROVED)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
