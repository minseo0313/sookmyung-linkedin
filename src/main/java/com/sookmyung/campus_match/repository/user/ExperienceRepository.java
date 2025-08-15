package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Experience;
import com.sookmyung.campus_match.domain.common.enums.ExperienceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUser_Id(Long userId);
    
    List<Experience> findByUser_IdAndExperienceType(Long userId, ExperienceType experienceType);
    
    @Query("SELECT e FROM Experience e WHERE " +
           "e.user.id = :userId AND " +
           "(:experienceType IS NULL OR e.experienceType = :experienceType) AND " +
           "(:isCurrent IS NULL OR e.isCurrent = :isCurrent)")
    Page<Experience> findByUserIdAndFilters(@Param("userId") Long userId,
                                           @Param("experienceType") ExperienceType experienceType,
                                           @Param("isCurrent") Boolean isCurrent,
                                           Pageable pageable);
    
    List<Experience> findByUserIdOrderByStartDateDesc(Long userId);
}
