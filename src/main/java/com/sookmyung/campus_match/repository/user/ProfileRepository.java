package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser_Id(Long userId);
    
    @Query("SELECT p FROM Profile p WHERE " +
           "(:department IS NULL OR p.department LIKE %:department%) AND " +
           "(:location IS NULL OR p.location LIKE %:location%)")
    Page<Profile> search(@Param("department") String department, 
                         @Param("location") String location, 
                         Pageable pageable);
    
    Page<Profile> findByViewCountGreaterThan(Integer minViewCount, Pageable pageable);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    boolean existsByUser(User user);
    
    @Modifying
    @Query("UPDATE Profile p SET p.viewCount = p.viewCount + 1 WHERE p.user.id = :userId")
    int incrementViewCount(@Param("userId") Long userId);
}
