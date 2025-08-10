package com.sookmyung.campus_match.repository.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserEmbedding;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEmbeddingRepository extends JpaRepository<UserEmbedding, Long> {

    // 단건 조회
    Optional<UserEmbedding> findByUser(User user);
    Optional<UserEmbedding> findByUser_Id(Long userId);

    // 존재 여부
    boolean existsByUser_Id(Long userId);

    // 삭제
    void deleteByUser_Id(Long userId);

    // 최근 업데이트 순 상위 N (예: 재생성 대상 우선순위 확인용)
    List<UserEmbedding> findTop20ByOrderByUpdatedAtDesc();
}
