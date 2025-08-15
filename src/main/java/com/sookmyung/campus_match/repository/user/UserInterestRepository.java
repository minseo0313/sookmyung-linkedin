package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.UserInterest;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    List<UserInterest> findByUser_Id(Long userId);
    
    List<UserInterest> findByInterestId(Long interestId);
    
    @Query("SELECT ui FROM UserInterest ui WHERE ui.user.id = :userId AND ui.interest.id = :interestId")
    UserInterest findByUserIdAndInterestId(@Param("userId") Long userId, @Param("interestId") Long interestId);
    
    boolean existsByUserIdAndInterestId(Long userId, Long interestId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<UserInterest> findByUser(User user);
}
