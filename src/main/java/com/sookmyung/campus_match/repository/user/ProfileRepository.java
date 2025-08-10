package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // User 객체로 찾기
    Optional<Profile> findByUser(User user);

    // userId로 찾기
    Optional<Profile> findByUser_Id(Long userId);

    // userId로 존재 여부 확인
    boolean existsByUser_Id(Long userId);
    
    // User 객체로 존재 여부 확인
    boolean existsByUser(User user);

    // (선택) 조회수 증가 — Profile 엔티티에 viewCount 필드가 있어야 함
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Profile p set p.viewCount = p.viewCount + 1 where p.user.id = :userId")
    int incrementViewCount(Long userId);
}
