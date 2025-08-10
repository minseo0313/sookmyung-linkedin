package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Experience;
import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.user.enum_.ExperienceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // 프로필 기준 조회
    List<Experience> findByProfile(Profile profile);
    List<Experience> findByProfile_Id(Long profileId);

    // 정렬(표시 순서) 포함 조회
    List<Experience> findByProfile_IdOrderBySortOrderAsc(Long profileId);

    // 타입별 조회 (CAREER / ACTIVITY)
    List<Experience> findByProfile_IdAndTypeOrderBySortOrderAsc(Long profileId, ExperienceType type);

    // 카운트/일괄 삭제
    long countByProfile_Id(Long profileId);
    void deleteByProfile_Id(Long profileId);
}
