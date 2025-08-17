package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.common.enums.CreatedFrom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCreatedBy_Id(Long createdById);
    
    List<Team> findByPost_Id(Long postId);
    
    List<Team> findByCreatedFrom(CreatedFrom createdFrom);
    
    @Query("SELECT t FROM Team t WHERE " +
           "(:createdById IS NULL OR t.createdBy.id = :createdById) AND " +
           "(:createdFrom IS NULL OR t.createdFrom = :createdFrom)")
    Page<Team> search(@Param("createdById") Long createdById,
                       @Param("createdFrom") CreatedFrom createdFrom,
                       Pageable pageable);

    // TeamService에서 호출하는 메서드들
    @Query("SELECT t FROM Team t JOIN t.members tm WHERE tm.user.id = :userId")
    List<Team> findByMembers_User_Id(@Param("userId") Long userId);
}
