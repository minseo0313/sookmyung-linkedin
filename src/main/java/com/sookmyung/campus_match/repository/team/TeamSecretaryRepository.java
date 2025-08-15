package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.TeamSecretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamSecretaryRepository extends JpaRepository<TeamSecretary, Long> {

    Optional<TeamSecretary> findByTeamId(Long teamId);
    
    boolean existsByTeamId(Long teamId);
}




