package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.UserInterest;
import com.sookmyung.campus_match.domain.user.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 특정 유저의 관심사 목록
    List<UserInterest> findByUser(User user);
    List<UserInterest> findByUserId(Long userId);

    // 특정 관심사를 가진 유저 목록
    List<UserInterest> findByInterest(Interest interest);
    List<UserInterest> findByInterestId(Long interestId);

    // 유저 + 관심사로 단건 조회
    Optional<UserInterest> findByUserAndInterest(User user, Interest interest);

    // 유저 + 관심사 존재 여부
    boolean existsByUserAndInterest(User user, Interest interest);

    // 특정 유저 관심사 전체 삭제
    void deleteByUser(User user);
    void deleteByUserId(Long userId);
}
